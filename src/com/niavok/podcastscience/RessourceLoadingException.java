package com.niavok.podcastscience;

public class RessourceLoadingException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8008627325328430063L;

    public RessourceLoadingException(String message, Throwable e) {
        super(message, e);
    }

    public RessourceLoadingException(String message) {
        super(message);
    }

}
