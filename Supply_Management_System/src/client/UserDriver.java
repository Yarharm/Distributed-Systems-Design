package client;

import exceptions.IncorrectUserRoleException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.NotBoundException;


public class UserDriver {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Manager managerQC = new Manager("QCM1111", "QC");
            managerQC.setupLogger();
            Manager managerBC = new Manager("BCM2222", "BC");
            managerBC.setupLogger();

            managerQC.addItem("QCM1111", "QC1111", "cola", 2, 20);
            managerQC.addItem("QCM1111", "QC1111", "cola", 3, 50);
            managerQC.removeItem("QCM1111", "QC1111", 3);

            managerBC.addItem("BCM4444", "BC3333", "noodles", 5, 55);
            managerBC.removeItem("BCM4444", "BC3333", -1);

            managerQC.removeItem("QCM1111", "QC1111", 3);
            managerBC.addItem("BCU1234", "BC3333", "noodles", 5, 55);
        } catch(IOException e) {
            System.err.println("Could not setup user logger");
            e.printStackTrace();
        } catch(NotBoundException e) {
            System.err.println("Could not lookup store in registry");
        } catch (Exception e) {
            System.err.println("User exception:");
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
