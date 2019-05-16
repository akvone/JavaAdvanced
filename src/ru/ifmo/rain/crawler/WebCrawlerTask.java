package ru.ifmo.rain.crawler;

import static java.util.concurrent.TimeUnit.SECONDS;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;

public class WebCrawlerTask {

  public static final int MAX_TASK_WAITING_TIMEOUT = 3;
  private final int maxDepth;
  private final String url;
  private final Downloader downloader;
  private final int downloadersNumber;
  private final ExecutorService extractorsPool;
  private final int extractorsNumber;
  private final ExecutorService downloadersPool;

  //
  private LinkedBlockingQueue<DownloadTask> downloadQueue = new LinkedBlockingQueue<>();
  private LinkedBlockingQueue<ExtractorTask> extractorQueue = new LinkedBlockingQueue<>();

  private List<String> downloaded = new ArrayList<>();
  private Map<String, IOException> errors = new HashMap<>();

  public WebCrawlerTask(String url, int maxDepth, Downloader downloader,
      ExecutorService downloadersPool, int downloadersNumber,
      ExecutorService extractorsPool, int extractorsNumber) {

    this.maxDepth = maxDepth;
    this.url = url;
    this.downloader = downloader;
    this.downloadersNumber = 1;
    this.extractorsPool = extractorsPool;
    this.extractorsNumber = 1;
    this.downloadersPool = downloadersPool;
  }

  public Result download() {
    try {
      downloadQueue.put(new DownloadTask(List.of(url), 1));
      startDownloaders();
      startExtractors();
      SECONDS.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return new Result(downloaded, errors);
  }

  private void startDownloaders() {
    runInParallel(downloadersPool, downloadersNumber, this::downloadNext);
  }

  private void startExtractors() {
    runInParallel(extractorsPool, extractorsNumber, this::extractNext);
  }

  private void runInParallel(ExecutorService executorService, int poolSize, SpecialRunnable r) {
    for (int i = 0; i < poolSize; i++) {
      executorService.submit(() -> {
        while (!Thread.interrupted()) {
          try {
            r.run();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      });
    }
  }

  public interface SpecialRunnable {

    public void run() throws InterruptedException;
  }


  public void downloadNext() {
    try {
      DownloadTask task = downloadQueue.poll(MAX_TASK_WAITING_TIMEOUT, SECONDS);
      if (task == null){

        throw new InterruptedException();
      }
      int currentTaskDepth = task.getDepth();
      if (currentTaskDepth <= maxDepth) {
        for (String url : task.getUrls()) {
          try {
            System.out.println("Try to load " + url);
            Document document = downloader.download(url);
            extractorQueue.put(new ExtractorTask(document, currentTaskDepth + 1));
          } catch (IOException e) {
            errors.put(url, e);
          }
        }
      } else {
        System.out.println("Try to add urls to result" + task.getUrls());
        downloaded.addAll(task.getUrls());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void extractNext() throws InterruptedException {
    ExtractorTask task = extractorQueue.take();
    try {
      System.out.println("Try to extract from next document ");

      List<String> urls = task.getDocument().extractLinks();
      downloadQueue.put(new DownloadTask(urls, task.getDepthToSet()));
    } catch (IOException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void close() {
//    CachingDownloader
  }


}
