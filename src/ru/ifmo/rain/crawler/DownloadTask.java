package ru.ifmo.rain.crawler;

class DownloadTask {

  private final String url;
  private final int depth;

  DownloadTask(String url, int depth) {
    this.url = url;
    this.depth = depth;
  }

  public String getUrl() {
    return url;
  }

  public int getDepth() {
    return depth;
  }
}
