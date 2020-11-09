package communicate.ICommunicatePackage;


/**
* communicate/ICommunicatePackage/ReturnPolicyException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public final class ReturnPolicyException extends org.omg.CORBA.UserException
{
  public String reason = null;

  public ReturnPolicyException ()
  {
    super(ReturnPolicyExceptionHelper.id());
  } // ctor

  public ReturnPolicyException (String _reason)
  {
    super(ReturnPolicyExceptionHelper.id());
    reason = _reason;
  } // ctor


  public ReturnPolicyException (String $reason, String _reason)
  {
    super(ReturnPolicyExceptionHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class ReturnPolicyException
