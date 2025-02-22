package communicate;


/**
* communicate/ICommunicatePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ICommunicate.idl
* Sunday, November 8, 2020 4:59:29 PM EST
*/

public abstract class ICommunicatePOA extends org.omg.PortableServer.Servant
 implements communicate.ICommunicateOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addItem", new java.lang.Integer (0));
    _methods.put ("removeItem", new java.lang.Integer (1));
    _methods.put ("listItemAvailability", new java.lang.Integer (2));
    _methods.put ("purchaseItem", new java.lang.Integer (3));
    _methods.put ("findItem", new java.lang.Integer (4));
    _methods.put ("returnItem", new java.lang.Integer (5));
    _methods.put ("addCustomerToWaitQueue", new java.lang.Integer (6));
    _methods.put ("exchangeItem", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // communicate/ICommunicate/addItem
       {
         try {
           String managerID = in.read_wstring ();
           String itemID = in.read_wstring ();
           String itemName = in.read_wstring ();
           int quantity = in.read_long ();
           int price = in.read_long ();
           String $result = null;
           $result = this.addItem (managerID, itemID, itemName, quantity, price);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ManagerExternalStoreItemException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ManagerExternalStoreItemExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ManagerItemPriceMismatchException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ManagerItemPriceMismatchExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 1:  // communicate/ICommunicate/removeItem
       {
         try {
           String managerID = in.read_wstring ();
           String itemID = in.read_wstring ();
           int quantity = in.read_long ();
           String $result = null;
           $result = this.removeItem (managerID, itemID, quantity);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ManagerExternalStoreItemException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ManagerExternalStoreItemExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ManagerRemoveNonExistingItemException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ManagerRemoveNonExistingItemExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ManagerRemoveBeyondQuantityException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ManagerRemoveBeyondQuantityExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 2:  // communicate/ICommunicate/listItemAvailability
       {
         try {
           String managerID = in.read_wstring ();
           String $result = null;
           $result = this.listItemAvailability (managerID);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 3:  // communicate/ICommunicate/purchaseItem
       {
         try {
           String customerID = in.read_wstring ();
           String itemID = in.read_wstring ();
           String dateOfPurchase = in.read_wstring ();
           String $result = null;
           $result = this.purchaseItem (customerID, itemID, dateOfPurchase);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ItemOutOfStockException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ItemOutOfStockExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.NotEnoughFundsException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.NotEnoughFundsExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ExternalStorePurchaseLimitException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 4:  // communicate/ICommunicate/findItem
       {
         try {
           String customerID = in.read_wstring ();
           String itemName = in.read_wstring ();
           String $result = null;
           $result = this.findItem (customerID, itemName);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 5:  // communicate/ICommunicate/returnItem
       {
         try {
           String customerID = in.read_wstring ();
           String itemID = in.read_wstring ();
           String dateOfReturn = in.read_wstring ();
           String $result = null;
           $result = this.returnItem (customerID, itemID, dateOfReturn);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.CustomerNeverPurchasedItemException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.CustomerNeverPurchasedItemExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ReturnPolicyException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ReturnPolicyExceptionHelper.write (out, $ex);
         }
         break;
       }

       case 6:  // communicate/ICommunicate/addCustomerToWaitQueue
       {
         String customerID = in.read_wstring ();
         String itemID = in.read_wstring ();
         String $result = null;
         $result = this.addCustomerToWaitQueue (customerID, itemID);
         out = $rh.createReply();
         out.write_wstring ($result);
         break;
       }

       case 7:  // communicate/ICommunicate/exchangeItem
       {
         try {
           String customerID = in.read_wstring ();
           String newItemID = in.read_wstring ();
           String oldItemID = in.read_wstring ();
           String dateOfExchange = in.read_wstring ();
           String $result = null;
           $result = this.exchangeItem (customerID, newItemID, oldItemID, dateOfExchange);
           out = $rh.createReply();
           out.write_wstring ($result);
         } catch (communicate.ICommunicatePackage.IncorrectUserRoleException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.IncorrectUserRoleExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ReturnPolicyException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ReturnPolicyExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.CustomerNeverPurchasedItemException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.CustomerNeverPurchasedItemExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ExternalStorePurchaseLimitException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ExternalStorePurchaseLimitExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.ItemOutOfStockException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.ItemOutOfStockExceptionHelper.write (out, $ex);
         } catch (communicate.ICommunicatePackage.NotEnoughFundsException $ex) {
           out = $rh.createExceptionReply ();
           communicate.ICommunicatePackage.NotEnoughFundsExceptionHelper.write (out, $ex);
         }
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:communicate/ICommunicate:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ICommunicate _this() 
  {
    return ICommunicateHelper.narrow(
    super._this_object());
  }

  public ICommunicate _this(org.omg.CORBA.ORB orb) 
  {
    return ICommunicateHelper.narrow(
    super._this_object(orb));
  }


} // class ICommunicatePOA
