package communicate.ICommunicatePackage;

/**
* communicate/ICommunicatePackage/ExternalStorePurchaseLimitExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class ExternalStorePurchaseLimitExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public communicate.ICommunicatePackage.ExternalStorePurchaseLimitException value = null;

  public ExternalStorePurchaseLimitExceptionHolder ()
  {
  }

  public ExternalStorePurchaseLimitExceptionHolder (communicate.ICommunicatePackage.ExternalStorePurchaseLimitException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.type ();
  }

}
