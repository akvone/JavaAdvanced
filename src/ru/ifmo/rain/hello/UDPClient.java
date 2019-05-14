package ru.ifmo.rain.hello;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UDPClient extends UDPCore {

  private final ExecutorService executorService;
  private final SocketAddress destinationAddress;

  UDPClient(String host, int port, int threadNumber) throws SocketException {
    super(new DatagramSocket());
    destinationAddress = new InetSocketAddress(host, port);
    executorService = Executors.newFixedThreadPool(threadNumber);
    socket.setSoTimeout(200);
  }

  void handle(int threadAmount, String reqPrefix, int reqNumber) {
    for (int threadNumber = 0; threadNumber < threadAmount; threadNumber++) {
      int finalThreadNumber = threadNumber;
      executorService.submit(() -> this.send(finalThreadNumber, reqPrefix, reqNumber));
    }

    try {
      executorService.shutdown(); // some problem here!!

      boolean successful = executorService.awaitTermination(3, SECONDS);
      if (!successful) {
        System.err.println("[ERROR] Not terminated");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void send(int threadNumber, String reqPrefix, int reqAmount) {
    try {
      for (int reqNumber = 0; reqNumber < reqAmount; reqNumber++) {
        boolean iterWithError;
        do {
          try {
            String reqMessage = reqPrefix + threadNumber + "_" + reqNumber;
            System.out.println("[request] " + reqMessage);
            sendPacket(destinationAddress, reqMessage);

            DatagramPacket packet = receivePacket();
            String respMessage = new String(packet.getData(), packet.getOffset(), packet.getLength());
            System.out.println("[answer] " + respMessage);

            String checkMessage = "Hello, " + reqMessage;

            iterWithError = !checkMessage.equals(respMessage);
          } catch (SocketTimeoutException e) {
            System.err.println("[ERROR] Timeout exceeded");

            iterWithError = true;
          }
        } while (iterWithError);
      }
      System.out.println("Thread " + threadNumber + " done");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
