package org.mpierce.concurrency.examples.dns;

public enum Opcode {

    QUERY(0),
    IQUERY(1),
    STATUS(2);

    private final int code;

    Opcode(int code) {
        this.code = code;
    }

    public int getIntCode() {
        return code;
    }

    public static Opcode getForIntCode(int intCode) throws InvalidRequestException {
        for (Opcode opcode : values()) {
            if (opcode.getIntCode() == intCode) {
                return opcode;
            }
        }

        throw new InvalidRequestException("Invalid type code " + intCode);
    }
}
