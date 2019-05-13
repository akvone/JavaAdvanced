package ru.ifmo.rain.hello;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UDPServer {

  private final int threadNumber;
  private final ExecutorService executorService;
  private final DatagramSocket socket;

  UDPServer(int port, int threadNumber) throws SocketException {
    this.threadNumber = threadNumber;
    executorService = Executors.newFixedThreadPool(threadNumber);
    socket = new DatagramSocket(port);
  }

  void start() {
    System.out.println("Start UDPServer");

    for (int i = 0; i < threadNumber; i++) {
      executorService.submit(this::receiveAndSend);
    }
  }

  void stop() {
    System.out.println("Stop UDPServer");

    executorService.shutdownNow();
    try {
      executorService.awaitTermination(1, SECONDS);
    } catch (InterruptedException e) {
      System.err.println("Stop operation has been interrupted");
    }
    socket.close();
  }

  private void receiveAndSend() {
    while (!Thread.interrupted()) {
      try {
        DatagramPacket reqPacket = receivePacket();
        String reqContent = new String(reqPacket.getData(), reqPacket.getOffset(), reqPacket.getLength());
        String respContent = "Hello, " + reqContent;
        sendPacket(reqPacket.getSocketAddress(), respContent);
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private DatagramPacket receivePacket() throws IOException {
    int bufferSize = socket.getReceiveBufferSize();
    DatagramPacket reqPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
    socket.receive(reqPacket);
    return reqPacket;
  }

  private void sendPacket(SocketAddress socketAddress, String respContent) throws IOException {
    byte[] respBytes = respContent.getBytes();
    DatagramPacket respPacket = new DatagramPacket(respBytes, respBytes.length, socketAddress);
    socket.send(respPacket);
  }
}
