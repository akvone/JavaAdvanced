package ru.ifmo.rain.walk;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class FNVHasher {

  public static final int FNV0 = 0x811c9dc5;
  public static final int FNV_32_PRIME = 0x01000193;

  public static int getHash(Path path) {
    try (FileInputStream inputStream = new FileInputStream(path.toFile())) {
      byte[] chunk = new byte[4096];
      int chunkLen;

      int hval = FNV0;
      while ((chunkLen = inputStream.read(chunk)) != -1) {
        for (int i = 0; i < chunkLen; i++) {
          hval = (hval * FNV_32_PRIME) ^ (chunk[i] & 0xff);
        }
      }

      return hval;
    } catch (IOException e) {
      return 0;
    }
  }
}
