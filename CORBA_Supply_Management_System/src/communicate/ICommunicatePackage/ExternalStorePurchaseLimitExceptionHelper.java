package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ExternalStorePurchaseLimitExceptionHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

abstract public class ExternalStorePurchaseLimitExceptionHelper
{
  private static String  _id = "IDL:communicate/ICommunicate/ExternalStorePurchaseLimitException:1.0";

  public static void insert (org.omg.CORBA.Any a, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static communicate.ICommunicatePackage.ExternalStorePurchaseLimitException extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_wstring_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "reason",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.id (), "ExternalStorePurchaseLimitException", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static communicate.ICommunicatePackage.ExternalStorePurchaseLimitException read (org.omg.CORBA.portable.InputStream istream)
  {
    communicate.ICommunicatePackage.ExternalStorePurchaseLimitException value = new communicate.ICommunicatePackage.ExternalStorePurchaseLimitException ();
    // read and discard the repository ID
    istream.read_string ();
    value.reason = istream.read_wstring ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, communicate.ICommunicatePackage.ExternalStorePurchaseLimitException value)
  {
    // write the repository ID
    ostream.write_string (id ());
    ostream.write_wstring (value.reason);
  }

}
