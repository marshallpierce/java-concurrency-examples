package org.mpierce.concurrency.examples.dns;

public enum RRClass {

    // basic classes
    IN(1),
    CS(2),
    CH(3),
    HS(4);

    private final int intCode;

    RRClass(int intCode) {
        this.intCode = intCode;
    }

    public int getIntCode() {
        return intCode;
    }

}
