package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ExternalStorePurchaseLimitException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class ExternalStorePurchaseLimitException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ExternalStorePurchaseLimitException ()
  {
    super(ExternalStorePurchaseLimitExceptionHelper.id());
  } // ctor

  public ExternalStorePurchaseLimitException (String _reason)
  {
    super(ExternalStorePurchaseLimitExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ExternalStorePurchaseLimitException (String $reason, String _reason)
  {
    super(ExternalStorePurchaseLimitExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ExternalStorePurchaseLimitException
