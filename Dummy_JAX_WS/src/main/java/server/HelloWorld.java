package server;

import javax.jws.WebService;

@WebService(endpointInterface = "server.Hello")
public class HelloWorld implements Hello {

    @Override
    public String getHelloWorldAsString() {
        return "Hello ";
    }

}