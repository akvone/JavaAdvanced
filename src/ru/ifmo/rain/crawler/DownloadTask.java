package ru.ifmo.rain.crawler;

class DownloadTask {

  private final String from;
  private final String url;
  private final int depth;

  DownloadTask(String from, String url, int depth) {
    this.from = from;
    this.url = url;
    this.depth = depth;
  }

  public String getUrl() {
    return url;
  }

  public int getDepth() {
    return depth;
  }

  public String getFrom() {
    return from;
  }
}
