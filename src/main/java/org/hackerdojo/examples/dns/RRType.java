package org.hackerdojo.examples.dns;

public enum RRType {

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
    TXT(16);

    private final int intCode;

    RRType(int intCode) {
        this.intCode = intCode;
    }

    public int getIntCode() {
        return intCode;
    }
}
