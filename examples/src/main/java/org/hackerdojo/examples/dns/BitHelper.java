package org.hackerdojo.examples.dns;

import org.apache.commons.codec.binary.Hex;

public class BitHelper {
    public static boolean getBit(short data, int bitNum) {
        if (bitNum > 15) {
            throw new IllegalArgumentException("Can't ask for bit " + bitNum + " of a short");
        }
        return ((data >> bitNum) & 0x1) == 1;
    }

    public static byte getByteStartingAtBit(short data, int startingBit, int length) {
        byte mask = (byte) (~((1 << 31) >> (31 - length)) & 0xFF);
//        hexDump(mask);
        short shifted = (short) (data >> startingBit);
//        hexDump(shifted);
        return (byte) (shifted & mask);
    }

    private static void hexDump(short i) {
        hexDump(new byte[]{(byte) (i >> 8 & 0xFF), (byte) (i & 0xFF)});
    }

    private static void hexDump(int i) {
        hexDump(new byte[]{(byte) (i >> 24 & 0xFF), (byte) (i >> 16 & 0xFF), (byte) (i >> 8 & 0xFF),
                (byte) (i & 0xFF)});
    }

    private static void hexDump(byte mask) {
        hexDump(new byte[]{mask});
    }

    public static void hexDump(byte[] bytes) {
        System.out.println("0x" + Hex.encodeHexString(bytes));
    }

    public static short swapBytes(short data) {
        return (short) ((data << 8) | (data >> 8 & 0xFF));
    }

}
