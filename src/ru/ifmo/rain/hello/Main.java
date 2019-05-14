package ru.ifmo.rain.hello;

import info.kgeorgiy.java.advanced.hello.Tester;

public class Main {

  public static void main(String[] args) {
    Tester.main("client", HelloClientImpl.class.getName());
//    Tester.main("server-i18n", HelloServerImpl.class.getName());

//    new HelloServerImpl().start(8888, 1);
//    new HelloClientImpl().run("127.0.0.1", 8888, "AAA", 1, 1);
  }
}
