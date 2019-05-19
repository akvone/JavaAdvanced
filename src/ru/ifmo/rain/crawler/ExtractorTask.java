package ru.ifmo.rain.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;

class ExtractorTask {

  private final Document document;
  private final String from;
  private final int depthToSet;

  ExtractorTask(Document document, String from, int depthToSet) {
    this.document = document;
    this.from = from;
    this.depthToSet = depthToSet;
  }

  public Document getDocument() {
    return document;
  }

  public int getDepthToSet() {
    return depthToSet;
  }

  public String getFrom() {
    return from;
  }
}
