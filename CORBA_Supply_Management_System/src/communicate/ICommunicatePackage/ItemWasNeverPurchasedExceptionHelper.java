package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ItemWasNeverPurchasedExceptionHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Monday, October 19, 2020 5:33:33 PM EDT
*/

abstract public class ItemWasNeverPurchasedExceptionHelper
{
  private static String  _id = "IDL:communicate/ICommunicate/ItemWasNeverPurchasedException:1.0";

  public static void insert (org.omg.CORBA.Any a, communicate.ICommunicatePackage.ItemWasNeverPurchasedException that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static communicate.ICommunicatePackage.ItemWasNeverPurchasedException extract (org.omg.CORBA.Any a)
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
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (communicate.ICommunicatePackage.ItemWasNeverPurchasedExceptionHelper.id (), "ItemWasNeverPurchasedException", _members0);
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

  public static communicate.ICommunicatePackage.ItemWasNeverPurchasedException read (org.omg.CORBA.portable.InputStream istream)
  {
    communicate.ICommunicatePackage.ItemWasNeverPurchasedException value = new communicate.ICommunicatePackage.ItemWasNeverPurchasedException ();
    // read and discard the repository ID
    istream.read_string ();
    value.reason = istream.read_wstring ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, communicate.ICommunicatePackage.ItemWasNeverPurchasedException value)
  {
    // write the repository ID
    ostream.write_string (id ());
    ostream.write_wstring (value.reason);
  }

}
