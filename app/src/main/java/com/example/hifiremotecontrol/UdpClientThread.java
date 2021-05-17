package com.example.hifiremotecontrol;


import android.os.Message;
import android.os.RemoteCallbackList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread {

    String dstAddress;
    int dstPort;
    String RMessage;
    private boolean running;
    final Boolean DEBUG = true;
    MainActivity.UdpClientHandler handler;

    DatagramSocket socket;
    InetAddress address;

    public UdpClientThread(String addr, int port, String BMessage, MainActivity.UdpClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        RMessage = BMessage;

        this.handler = handler;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void sendState(String state) {
        handler.sendMessage(
                Message.obtain(handler,
                        MainActivity.UdpClientHandler.UPDATE_STATE, state));
    }

    @Override
    public void run() {
        sendState("connecting...");

        running = true;

        try {
            sendState("Opening Socket...  ");
            socket = new DatagramSocket();

            address = InetAddress.getByName(dstAddress);

            // send request
            byte[] buf = new byte[256];
            buf = RMessage.getBytes();
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length, address, dstPort);
            sendState("sending...   ");
            sendState(address.toString());
            socket.send(packet);
            String sent = "Sent   ";
            sent = sent.concat("data");
            sendState("Sent %s to %s %d");

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                sendState("closing socket");
                socket.close();
                handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
                sendState("closed");
            }
        }
    }
}
