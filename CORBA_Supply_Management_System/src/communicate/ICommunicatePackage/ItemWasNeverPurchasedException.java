package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ItemWasNeverPurchasedException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Monday, October 19, 2020 5:33:33 PM EDT
*/

public final class ItemWasNeverPurchasedException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ItemWasNeverPurchasedException ()
  {
    super(ItemWasNeverPurchasedExceptionHelper.id());
  } // ctor

  public ItemWasNeverPurchasedException (String _reason)
  {
    super(ItemWasNeverPurchasedExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ItemWasNeverPurchasedException (String $reason, String _reason)
  {
    super(ItemWasNeverPurchasedExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ItemWasNeverPurchasedException
