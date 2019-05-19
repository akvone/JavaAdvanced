package ru.ifmo.rain.walk;

import static java.lang.String.format;
import static ru.ifmo.rain.walk.FNVHasher.getHash;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

public class Walker {


  public static void main(String[] args) {
    new Walker().walk(args);
  }

  private void walk(String[] args) {
    if (args == null || args.length != 2) {
      System.err.println("Arguments are incorrect");
      return;
    }

    String inputPath = args[0];
    String outputPath = args[1];

    try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      String rootFilePath = reader.readLine();

      while (rootFilePath != null) {
        try {
          walkRecursively(rootFilePath, writer);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InvalidPathException e) {
          writeToFile(writer, new FileResult(rootFilePath, 0));
        }

        rootFilePath = reader.readLine();
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  private void walkRecursively(String rootFilePath, BufferedWriter writer) throws IOException {
    Consumer<FileResult> consumer = (fileResult) -> {
      try {
        writeToFile(writer, fileResult);
        writer.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
    Path path = Paths.get(rootFilePath);

    Files.walkFileTree(path, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        FileResult result = new FileResult(path.toString(), getHash(path));
        consumer.accept(result);

        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path path, IOException e) {
        FileResult result = new FileResult(path.toString(), 0);
        consumer.accept(result);

        return FileVisitResult.CONTINUE;
      }
    });
  }

  private void writeToFile(BufferedWriter writer, FileResult fileResult) throws IOException {
    writer.write(format("%08x %s", fileResult.getHash(), fileResult.getPath()));
  }

}
