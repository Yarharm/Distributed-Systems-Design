package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ItemOutOfStockException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Tuesday, October 13, 2020 11:47:16 PM EDT
*/

public final class ItemOutOfStockException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ItemOutOfStockException ()
  {
    super(ItemOutOfStockExceptionHelper.id());
  } // ctor

  public ItemOutOfStockException (String _reason)
  {
    super(ItemOutOfStockExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ItemOutOfStockException (String $reason, String _reason)
  {
    super(ItemOutOfStockExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ItemOutOfStockException
