package ru.ifmo.rain.hello;

import info.kgeorgiy.java.advanced.hello.Tester;

public class Main {

  public static void main(String[] args) {
    Tester.main("client-i18n", HelloClientImpl.class.getName());
    Tester.main("server-i18n", HelloServerImpl.class.getName());
  }
}
