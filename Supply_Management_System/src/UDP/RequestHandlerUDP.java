package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestHandlerUDP implements Serializable{

    public byte[] marshallMesage(List<String> args) {
        String str = String.join(RequestTypesUDP.UDP_DELIM, args);
        return str.getBytes();
    }

    public List<String> unmarshallMessage(DatagramPacket request) {
        String str = new String(request.getData(), request.getOffset(), request.getLength());
        String[] strs = str.split(RequestTypesUDP.UDP_DELIM);
        return Arrays.stream(strs).collect(Collectors.toList());
    }
}
