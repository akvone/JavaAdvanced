package ru.ifmo.rain.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;

class ExtractorTask {

  private final Document document;
  private final int depthToSet;

  ExtractorTask(Document document, int depthToSet) {
    this.document = document;
    this.depthToSet = depthToSet;
  }

  public Document getDocument() {
    return document;
  }

  public int getDepthToSet() {
    return depthToSet;
  }
}
