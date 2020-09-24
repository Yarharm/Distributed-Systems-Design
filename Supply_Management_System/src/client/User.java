package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class User {
    public static class InnerClass implements Runnable{
        public List<String> res;
        public Manager mngr;
        public InnerClass(Manager mngr) {
            res = new ArrayList<>();
            this.mngr = mngr;
        }
        @Override
        public void run() {
            try{
                res = this.mngr.listItemAvailability("IQ");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Manager mngr = new Manager("QCM1111", "QC");
            List<String> res = new ArrayList<>();
            InnerClass ic = new InnerClass(mngr);
            Thread threadRMI = new Thread(ic);
            threadRMI.start();
            System.out.println("Now fetching using UDP");
            sendMessage(8888, "Hello UDP");
            threadRMI.join();
            res.addAll(ic.res);
            System.out.println(res);
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }

    private static void sendMessage(int serverPort, String msg) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] message = msg.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, msg.length(), aHost, serverPort);
            aSocket.send(request);
            System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
                    + new String(request.getData()));
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            System.out.println("Reply received from the server with port number " + serverPort + " is: "
                    + new String(reply.getData()));
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
