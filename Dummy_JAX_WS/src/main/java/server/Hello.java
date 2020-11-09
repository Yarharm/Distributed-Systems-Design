package server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL)
public interface Hello {

    @WebMethod
    String getHelloWorldAsString();

}