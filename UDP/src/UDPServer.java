import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {

    public static void main(String[] args) {
        Runnable task = () -> {
            receive();
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    private static void receive() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(8888);
            byte[] buffer = new byte[1000];
            System.out.println("Server 8888 Started............");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

}