package ru.ifmo.rain.crawler;

import java.util.List;

class DownloadTask {

  private final List<String> urls;
  private final int depth;

  DownloadTask(List<String> urls, int depth) {
    this.urls = urls;
    this.depth = depth;
  }

  public List<String> getUrls() {
    return urls;
  }

  public int getDepth() {
    return depth;
  }
}
