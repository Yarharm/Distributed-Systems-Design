package communicate.ICommunicatePackage;

/**
* communicate/ICommunicatePackage/CustomerNeverPurchasedItemExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Tuesday, October 13, 2020 11:47:16 PM EDT
*/

public final class CustomerNeverPurchasedItemExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public communicate.ICommunicatePackage.CustomerNeverPurchasedItemException value = null;

  public CustomerNeverPurchasedItemExceptionHolder ()
  {
  }

  public CustomerNeverPurchasedItemExceptionHolder (communicate.ICommunicatePackage.CustomerNeverPurchasedItemException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = communicate.ICommunicatePackage.CustomerNeverPurchasedItemExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    communicate.ICommunicatePackage.CustomerNeverPurchasedItemExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return communicate.ICommunicatePackage.CustomerNeverPurchasedItemExceptionHelper.type ();
  }

}
