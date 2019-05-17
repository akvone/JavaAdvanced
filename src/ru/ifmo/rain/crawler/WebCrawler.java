package ru.ifmo.rain.crawler;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler implements Crawler {


  private final Downloader downloader;
  private final int perHost;

  private final ExecutorService downloadersPool;
  private final ExecutorService extractorsPool;

  public WebCrawler(Downloader downloader, int downloadersNumber, int extractorsNumber, int perHost) {
    this.downloader = downloader;
    downloadersPool = Executors.newFixedThreadPool(Math.min(downloadersNumber, 100));
    extractorsPool = Executors.newFixedThreadPool(Math.min(downloadersNumber, 100));
    this.perHost = perHost;
  }

  private WebCrawlerTask webCrawlerTask;

  @Override
  public Result download(String url, int depth) {
    if (depth < 1){
      throw new IllegalArgumentException();
    }
    webCrawlerTask = new WebCrawlerTask(url, depth, downloader, downloadersPool, extractorsPool);
    return webCrawlerTask.download();
  }

  @Override
  public void close() {
    webCrawlerTask.close();
  }
}
