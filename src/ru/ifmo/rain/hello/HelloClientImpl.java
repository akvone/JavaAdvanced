package ru.ifmo.rain.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;
import java.net.SocketException;

public class HelloClientImpl implements HelloClient {

  @Override
  public void run(String host, int port, String reqPrefix, int concurrentNumber, int reqNumber) {
    try {
      UDPClient client = new UDPClient(host, port, concurrentNumber);
      client.handle(concurrentNumber, reqPrefix, reqNumber);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }
}
