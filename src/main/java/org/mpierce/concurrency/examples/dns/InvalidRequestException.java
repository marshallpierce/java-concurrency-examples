package org.mpierce.concurrency.examples.dns;

public class InvalidRequestException extends Exception {

    InvalidRequestException(String message) {
        super(message);
    }
}
