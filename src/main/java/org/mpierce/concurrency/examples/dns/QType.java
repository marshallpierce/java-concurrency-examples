package org.mpierce.concurrency.examples.dns;

public enum QType {
    // basic record types
    A(1),
    NS(2),
    MD(3),
    MF(4),
    CNAME(5),
    SOA(6),
    MB(7),
    MG(8),
    MR(9),
    NULL(10),
    WKS(11),
    PTR(12),
    HINFO(13),
    MINFO(14),
    MX(15),
    TXT(16),
    // QType extensions
    AXFR(252),
    MAILB(253),
    MAILA(254),
    ALL_RECORDS(255);

    private final int intCode;

    QType(int intCode) {
        this.intCode = intCode;
    }

    public int getIntCode() {
        return intCode;
    }

    public static QType getForIntCode(int intCode) throws InvalidRequestException {
        for (QType qType : values()) {
            if (qType.getIntCode() == intCode) {
                return qType;
            }
        }

        throw new InvalidRequestException("Invalid type code " + intCode);
    }
}
