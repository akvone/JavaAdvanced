package ru.ifmo.rain.concurrent;

import info.kgeorgiy.java.advanced.concurrent.Tester;

public class Main {

  public static void main(String[] args) {
    Tester.main("scalar", IterativeParallelism.class.getName());
    Tester.main("list", IterativeParallelism.class.getName());
  }
}
