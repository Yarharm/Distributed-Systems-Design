package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ManagerItemPriceMismatchException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Tuesday, October 13, 2020 11:47:16 PM EDT
*/

public final class ManagerItemPriceMismatchException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ManagerItemPriceMismatchException ()
  {
    super(ManagerItemPriceMismatchExceptionHelper.id());
  } // ctor

  public ManagerItemPriceMismatchException (String _reason)
  {
    super(ManagerItemPriceMismatchExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ManagerItemPriceMismatchException (String $reason, String _reason)
  {
    super(ManagerItemPriceMismatchExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ManagerItemPriceMismatchException
