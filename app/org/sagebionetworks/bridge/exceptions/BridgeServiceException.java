package org.sagebionetworks.bridge.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class BridgeServiceException extends RuntimeException {
    private static final long serialVersionUID = -4425097573703184608L;
    
    private final int statusCode;
    
    public BridgeServiceException(String message) {
        super(message);
        this.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }
    
    public BridgeServiceException(Throwable throwable) {
        this(throwable, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
    
    protected BridgeServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    protected BridgeServiceException(Throwable throwable, int statusCode) {
        super(throwable);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }

}
