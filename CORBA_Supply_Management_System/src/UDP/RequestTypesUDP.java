package UDP;

public final class RequestTypesUDP {
    private RequestTypesUDP(){}

    public static final String UDP_DELIM = "/";
    public static final String UDP_REQUEST_STATUS_SUCCESS = "UDP_REQUEST_STATUS_SUCCESS";
    public static final String UDP_REQUEST_STATUS_FAILURE = "UDP_REQUEST_STATUS_FAILURE";
    public static final String FIND_ITEM = "FIND_ITEM";
    public static final String PURCHASE_ITEM = "PURCHASE_ITEM";
    public static final String RETURN_ITEM = "RETURN_ITEM";
    public static final String ADD_CUSTOMER_TO_WAIT_QUEUE = "ADD_CUSTOMER_TO_WAIT_QUEUE";
    public static final String AUTOMATICALLY_ASSIGN_ITEM = "AUTOMATICALLY_ASSIGN_ITEM";
    public static final String CLEAR_ALL_ITEM_PURCHASES = "CLEAR_ALL_ITEM_PURCHASES";
    public static final String FETCH_PRODUCT_PRICE = "FETCH_PRODUCT_PRICE";

}
