package communicate.ICommunicatePackage;

/**
* communicate/ICommunicatePackage/IncorrectUserRoleExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Friday, October 16, 2020 7:02:15 PM EDT
*/

public final class IncorrectUserRoleExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public communicate.ICommunicatePackage.IncorrectUserRoleException value = null;

  public IncorrectUserRoleExceptionHolder ()
  {
  }

  public IncorrectUserRoleExceptionHolder (communicate.ICommunicatePackage.IncorrectUserRoleException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.type ();
  }

}
