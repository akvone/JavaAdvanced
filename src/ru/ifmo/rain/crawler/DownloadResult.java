package ru.ifmo.rain.crawler;

import java.io.IOException;

public class DownloadResult {

  DownloadResult(IOException e) {
    this.e = e;
  }

  private IOException e;

  boolean isError() {
    return e != null;
  }

  boolean isNoError() {
    return !isError();
  }

  public IOException getException() {
    return e;
  }
}
