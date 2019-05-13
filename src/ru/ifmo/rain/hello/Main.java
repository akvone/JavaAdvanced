package ru.ifmo.rain.hello;

import info.kgeorgiy.java.advanced.hello.Tester;
import java.net.UnknownHostException;

public class Main {

  public static void main(String[] args) throws UnknownHostException {
    Tester.main("server-i18n", HelloServerImpl.class.getName());
  }
}
