package communicate.ICommunicatePackage;

/**
* communicate/ICommunicatePackage/NotEnoughFundsExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class NotEnoughFundsExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public communicate.ICommunicatePackage.NotEnoughFundsException value = null;

  public NotEnoughFundsExceptionHolder ()
  {
  }

  public NotEnoughFundsExceptionHolder (communicate.ICommunicatePackage.NotEnoughFundsException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = communicate.ICommunicatePackage.NotEnoughFundsExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    communicate.ICommunicatePackage.NotEnoughFundsExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return communicate.ICommunicatePackage.NotEnoughFundsExceptionHelper.type ();
  }

}
