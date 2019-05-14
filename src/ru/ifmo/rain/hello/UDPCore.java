package ru.ifmo.rain.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

abstract class UDPCore {
  final DatagramSocket socket;

  UDPCore(DatagramSocket socket) throws SocketException {
    this.socket = socket;
    socket.setSoTimeout(0);

  }

  DatagramPacket receivePacket() throws IOException {
    int bufferSize = socket.getReceiveBufferSize();
    DatagramPacket reqPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
    socket.receive(reqPacket);

    return reqPacket;
  }

  void sendPacket(SocketAddress socketAddress, String respContent) throws IOException {
    byte[] respBytes = respContent.getBytes();
    DatagramPacket respPacket = new DatagramPacket(respBytes, respBytes.length, socketAddress);
    socket.send(respPacket);
  }
}
