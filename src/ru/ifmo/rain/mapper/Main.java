package ru.ifmo.rain.mapper;

import info.kgeorgiy.java.advanced.mapper.Tester;

public class Main {

  public static void main(String[] args) {
    Tester.main("list", ParallelMapperImpl.class.getName() + "," + ExtendedIterativeParallelism.class.getName());
  }
}
