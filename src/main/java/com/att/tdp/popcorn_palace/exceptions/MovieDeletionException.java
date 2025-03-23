package com.att.tdp.popcorn_palace.exceptions;

public class MovieDeletionException extends RuntimeException {
    public MovieDeletionException(String message) {
        super(message);
    }
}
