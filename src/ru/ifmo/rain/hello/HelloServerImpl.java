package ru.ifmo.rain.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import java.net.SocketException;

public class HelloServerImpl implements HelloServer {

  private UDPServer server;

  @Override
  public void start(int port, int threadNumber) {
    try {
      server = new UDPServer(port, threadNumber);
      server.start();
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
    server.stop();
  }
}
