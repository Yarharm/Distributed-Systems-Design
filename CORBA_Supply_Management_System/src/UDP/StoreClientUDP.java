package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StoreClientUDP implements Callable<List<String>> {
    private final List<String> args;
    private final int serverPort;
    private final RequestHandlerUDP handler;
    public StoreClientUDP(List<String> args, int serverPort) {
        this.args = new ArrayList<>(args);
        this.serverPort = serverPort;
        this.handler = new RequestHandlerUDP();
    }

    @Override
    public List<String> call() {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            InetAddress aHost = InetAddress.getByName("localhost");
            byte[] buf = this.handler.marshallMesage(this.args);
            DatagramPacket request = new DatagramPacket(buf, buf.length, aHost, this.serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[20000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);

            return this.handler.unmarshallMessage(reply);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}