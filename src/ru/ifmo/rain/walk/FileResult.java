package ru.ifmo.rain.walk;

class FileResult {

  final String path;
  final int hash;

  FileResult(String path, int hash) {
    this.path = path;
    this.hash = hash;
  }

  public String getPath() {
    return path;
  }

  public int getHash() {
    return hash;
  }
}
