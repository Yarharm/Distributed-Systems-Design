package Client;

import server.Hello;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class HelloWorldClient{

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://localhost:9999/ws/hello?wsdl");
        QName qname = new QName("http://server/", "HelloWorldService");

        Service service = Service.create(url, qname);

        Hello hello = service.getPort(Hello.class);

        System.out.println(hello.getHelloWorldAsString());

    }

}