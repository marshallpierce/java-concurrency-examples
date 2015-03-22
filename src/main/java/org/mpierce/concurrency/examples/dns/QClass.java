package org.mpierce.concurrency.examples.dns;

public enum QClass {

    // basic classes
    IN(1),
    CS(2),
    CH(3),
    HS(4),
    // QClass extensions
    ANY_CLASS(255);

    private final int intCode;

    QClass(int intCode) {
        this.intCode = intCode;
    }

    public int getIntCode() {
        return intCode;
    }

    public static QClass getForIntCode(int intCode) throws InvalidRequestException {
        for (QClass qClass : values()) {
            if (qClass.getIntCode() == intCode) {
                return qClass;
            }
        }

        throw new InvalidRequestException("Invalid type code " + intCode);
    }

}
