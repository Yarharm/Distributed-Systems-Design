package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ManagerRemoveBeyondQuantityException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Monday, October 19, 2020 5:33:33 PM EDT
*/

public final class ManagerRemoveBeyondQuantityException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ManagerRemoveBeyondQuantityException ()
  {
    super(ManagerRemoveBeyondQuantityExceptionHelper.id());
  } // ctor

  public ManagerRemoveBeyondQuantityException (String _reason)
  {
    super(ManagerRemoveBeyondQuantityExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ManagerRemoveBeyondQuantityException (String $reason, String _reason)
  {
    super(ManagerRemoveBeyondQuantityExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ManagerRemoveBeyondQuantityException
