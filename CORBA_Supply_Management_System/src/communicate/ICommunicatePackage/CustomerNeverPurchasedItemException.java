package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/CustomerNeverPurchasedItemException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class CustomerNeverPurchasedItemException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public CustomerNeverPurchasedItemException ()
  {
    super(CustomerNeverPurchasedItemExceptionHelper.id());
  } // ctor

  public CustomerNeverPurchasedItemException (String _reason)
  {
    super(CustomerNeverPurchasedItemExceptionHelper.id());
    reason = _reason;
  } // ctor


  public CustomerNeverPurchasedItemException (String $reason, String _reason)
  {
    super(CustomerNeverPurchasedItemExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class CustomerNeverPurchasedItemException
