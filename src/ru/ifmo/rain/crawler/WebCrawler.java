package ru.ifmo.rain.crawler;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler implements Crawler {


  private final Downloader downloader;
  private final int downloadersNumber;
  private final int extractorsNumber;
  private final int perHost;

  private final ExecutorService downloadersPool;
  private final ExecutorService extractorsPool;

  public WebCrawler(Downloader downloader, int downloadersNumber, int extractorsNumber, int perHost) {
    this.downloader = downloader;
    this.downloadersNumber = downloadersNumber;
    downloadersPool = Executors.newFixedThreadPool(downloadersNumber);
    this.extractorsNumber = extractorsNumber;
    extractorsPool = Executors.newFixedThreadPool(extractorsNumber);
    this.perHost = perHost;
  }

  @Override
  public Result download(String url, int depth) {
    WebCrawlerTask webCrawlerTask = new WebCrawlerTask(url, depth, downloader,
        downloadersPool, downloadersNumber,
        extractorsPool, extractorsNumber);
    return webCrawlerTask.download();
  }

  @Override
  public void close() {

  }
}
