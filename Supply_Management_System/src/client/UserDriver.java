package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserDriver {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            DateFormat sourceFormat = new SimpleDateFormat("ddMMyyyy");
            Manager managerQC = new Manager("QCM1111", "QC");
            managerQC.setupLogger();
            Manager managerBC = new Manager("BCM2222", "BC");
            managerBC.setupLogger();

//            managerQC.addItem("QCM1111", "QC1111", "cola", 2, 20);
//            managerQC.addItem("QCM1111", "QC1111", "cola", 3, 50);
            // managerQC.addItem("QCM1111", "QC2222", "bacon", 1, 10);
//             managerQC.addItem("QCM1111", "QC3333", "gold", 7, 100);
//            managerQC.listItemAvailability("QCM1111");
//            managerQC.listItemAvailability("QCU1234");
//            managerQC.removeItem("QCM1111", "QC1111", 3);
//            managerQC.removeItem("QCM1111", "QC1111", 3);
//
              managerBC.addItem("BCM4444", "BC3333", "bacon", 5, 55);
//            managerBC.removeItem("BCM4444", "BC3333", -1);
//            managerBC.addItem("BCM1234", "BC3333", "noodles", 5, 55);
            Customer customerQC = new Customer("QCU9999", "QC");
            customerQC.setupLogger();

            // customerON.findItem("ONU9999","bacon");
//            customerQC.purchaseItem("QCU9999", "QC3456", sourceFormat.parse("23021999"));
//            customerQC.purchaseItem("QCU9999", "QC2222", sourceFormat.parse("23022005"));
//            customerQC.purchaseItem("QCU9999", "QC3333", sourceFormat.parse("01022020"));
//            customerQC.returnItem("QCU9999", "QC3333", sourceFormat.parse("29022020"));
            customerQC.purchaseItem("QCU9999", "BC3333", sourceFormat.parse("01022020"));
            customerQC.returnItem("QCU9999", "BC1234", sourceFormat.parse("15042020"));
            customerQC.returnItem("QCU9999", "BC3333", sourceFormat.parse("29022020"));
//            customerQC.purchaseItem("QCU9999", "BC3333", sourceFormat.parse("23022020"));
//            customerQC.returnItem("QCU9999", "BC3333", sourceFormat.parse("29022020"));
//            Customer customerBC = new Customer("BCU1234", "BC");
//            customerBC.setupLogger();
//            //customerBC.purchaseItem("BCU1234", "QC2222", sourceFormat.parse("23021999"));
//            customerBC.purchaseItem("BCU1234", "QC3333", sourceFormat.parse("23021999"));

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

//    private static void sendMessage(int serverPort, String msg) {
//        DatagramSocket aSocket = null;
//        try {
//            aSocket = new DatagramSocket();
//            byte[] message = msg.getBytes();
//            InetAddress aHost = InetAddress.getByName("localhost");
//            DatagramPacket request = new DatagramPacket(message, msg.length(), aHost, serverPort);
//            aSocket.send(request);
//            System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
//                    + new String(request.getData()));
//            byte[] buffer = new byte[1000];
//            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
//
//            aSocket.receive(reply);
//            System.out.println("Reply received from the server with port number " + serverPort + " is: "
//                    + new String(reply.getData()));
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("IO: " + e.getMessage());
//        } finally {
//            if (aSocket != null)
//                aSocket.close();
//        }
//    }
}
