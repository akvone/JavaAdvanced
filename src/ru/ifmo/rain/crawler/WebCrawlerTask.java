package ru.ifmo.rain.crawler;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

public class WebCrawlerTask {

  private final int maxDepth;
  private final String rootUrl;
  private final Downloader downloader;
  private final ExecutorService downloadersPool;
  private final ExecutorService extractorsPool;

  private Map<String, DownloadResult> rawResult = new ConcurrentHashMap<>();

  private Phaser phaser;

  public WebCrawlerTask(String rootUrl, int maxDepth, Downloader downloader,
      ExecutorService downloadersPool, ExecutorService extractorsPool) {

    this.maxDepth = maxDepth;
    this.rootUrl = rootUrl;
    this.downloader = downloader;
    this.downloadersPool = downloadersPool;
    this.extractorsPool = extractorsPool;
  }

  public Result download() {
    System.out.println("Start download. Url " + rootUrl + ", depth " + maxDepth);
    phaser = new Phaser(1);
    submitTask(new DownloadTask(rootUrl, 1));
    phaser.arriveAndAwaitAdvance();

    return prepareResult(rawResult);
  }

  private Result prepareResult(Map<String, DownloadResult> rawResult) {
    var list = rawResult.entrySet().stream()
        .filter(e -> e.getValue().isNoError())
        .map(Entry::getKey)
        .collect(toList());
    var errors = rawResult.entrySet().stream()
        .filter(e -> e.getValue().isError())
        .collect(toMap(Entry::getKey, e -> e.getValue().getException()));

    return new Result(list, errors);
  }


  private void submitTask(ExtractorTask extractorTask) {
    submitTask(extractorsPool, () -> extractNext(extractorTask));
  }

  private void submitTask(DownloadTask downloadTask) {
    submitTask(downloadersPool, () -> downloadNext(downloadTask));
  }

  private void submitTask(ExecutorService executorService, SpecialRunnable r) {
    syncPlus();
    executorService.submit(() -> {
      try {
        r.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Throwable t) {
        t.printStackTrace();
      } finally {
        syncMinus();
      }
    });
  }

  private void syncMinus() {
    phaser.arrive();
  }

  private void syncPlus() {
    phaser.register();
  }

  private void downloadNext(DownloadTask task) {
    int currentTaskDepth = task.getDepth();
    String url = task.getUrl();
    rawResult.computeIfAbsent(url, s -> {
      try {
        System.out.println("Try to load [" + currentTaskDepth + "] " + url);
        Document document = downloader.download(url);

        if (currentTaskDepth + 1 <= maxDepth) {
          submitTask(new ExtractorTask(document, currentTaskDepth + 1));
        }
        return new DownloadResult(null);
      } catch (IOException e) {
        return new DownloadResult(e);
      } catch (Throwable t) {
        t.printStackTrace();
        return null;
      }
    });
  }

  private void extractNext(ExtractorTask task) {
    try {
      System.out.println("Try to extract from next document ");

      List<String> urls = task.getDocument().extractLinks();
      for (String url : urls) {
        submitTask(new DownloadTask(url, task.getDepthToSet()));
      }
    } catch (IOException e) {
      Thread.currentThread().interrupt();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void close() {
//    downloadersPool.shutdownNow();
//    extractorsPool.shutdownNow();
//    phaser.forceTermination(); // TODO
  }

}
