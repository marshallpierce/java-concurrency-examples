package org.hackerdojo.examples.dns;

public enum ResponseCode {

    NO_ERROR(0),
    FORMAT_ERROR(1),
    SERVER_FAILURE(2),
    NAME_ERROR(3),
    NOT_IMPLEMENTED(4),
    REFUSED(5),;

    private final int intCode;

    ResponseCode(int intCode) {
        this.intCode = intCode;
    }

    public int getIntCode() {
        return intCode;
    }
}
