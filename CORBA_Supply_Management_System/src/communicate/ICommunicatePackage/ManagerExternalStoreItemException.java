package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ManagerExternalStoreItemException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Monday, October 19, 2020 5:33:33 PM EDT
*/

public final class ManagerExternalStoreItemException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ManagerExternalStoreItemException ()
  {
    super(ManagerExternalStoreItemExceptionHelper.id());
  } // ctor

  public ManagerExternalStoreItemException (String _reason)
  {
    super(ManagerExternalStoreItemExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ManagerExternalStoreItemException (String $reason, String _reason)
  {
    super(ManagerExternalStoreItemExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ManagerExternalStoreItemException
