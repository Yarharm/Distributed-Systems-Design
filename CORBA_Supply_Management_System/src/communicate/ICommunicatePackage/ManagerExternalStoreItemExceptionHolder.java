package communicate.ICommunicatePackage;

/**
* communicate/ICommunicatePackage/ManagerExternalStoreItemExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class ManagerExternalStoreItemExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public communicate.ICommunicatePackage.ManagerExternalStoreItemException value = null;

  public ManagerExternalStoreItemExceptionHolder ()
  {
  }

  public ManagerExternalStoreItemExceptionHolder (communicate.ICommunicatePackage.ManagerExternalStoreItemException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = communicate.ICommunicatePackage.ManagerExternalStoreItemExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    communicate.ICommunicatePackage.ManagerExternalStoreItemExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return communicate.ICommunicatePackage.ManagerExternalStoreItemExceptionHelper.type ();
  }

}
