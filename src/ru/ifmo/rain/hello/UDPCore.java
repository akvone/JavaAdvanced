package ru.ifmo.rain.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class UDPCore implements AutoCloseable {

  private final DatagramSocket socket;

  UDPCore(DatagramSocket socket) {
    this.socket = socket;
  }

  DatagramPacket receivePacket() throws IOException {
    int bufferSize = socket.getReceiveBufferSize();
    DatagramPacket respPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
    socket.receive(respPacket);

    return respPacket;
  }

  void sendPacket(SocketAddress socketAddress, String reqMessage) throws IOException {
    byte[] reqBytes = reqMessage.getBytes();
    DatagramPacket reqPacket = new DatagramPacket(reqBytes, reqBytes.length, socketAddress);
    socket.send(reqPacket);
  }

  @Override
  public void close() {
    socket.close();
  }
}
