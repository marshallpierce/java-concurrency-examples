package org.hackerdojo.examples.dns;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitHelperTest {

    @Test
    public void testGetBitAllSet() {
        for (int i = 0; i <= 15; i++) {
            assertTrue(BitHelper.getBit((short) 0xFFFF, i));
        }
    }

    @Test
    public void testGetBitNoneSet() {
        for (int i = 0; i <= 15; i++) {
            assertFalse(BitHelper.getBit((short) 0x0000, i));
        }
    }

    @Test
    public void testGetBitFirstSet() {
        assertFalse(BitHelper.getBit((short) 0x0001, 1));
        assertTrue(BitHelper.getBit((short) 0x0001, 0));
    }

    @Test
    public void testGetBitLastSet() {
        assertTrue(BitHelper.getBit((short) 0x8000, 15));
        assertFalse(BitHelper.getBit((short) 0x8000, 14));
    }

    @Test
    public void testGetBitFirstInLastByte() {
        assertFalse(BitHelper.getBit((short) 0x0100, 7));
        assertTrue(BitHelper.getBit((short) 0x0100, 8));
        assertFalse(BitHelper.getBit((short) 0x0100, 9));
    }

    @Test
    public void testGetByteSomeSet1() {
        assertByteEquals((byte) 0xF0, BitHelper.getByteStartingAtBit((short) 0xFF00, 4, 8));
    }

    @Test
    public void testGetByteSomeSet2() {
        assertByteEquals((byte) 0xe0, BitHelper.getByteStartingAtBit((short) 0xFF00, 3, 8));
    }

    @Test
    public void testGetByteSomeSet3() {
        assertByteEquals((byte) 0x60, BitHelper.getByteStartingAtBit((short) 0xFF00, 3, 7));
    }

    @Test
    public void testGetByteSomeSet4() {
        assertByteEquals((byte) 0x20, BitHelper.getByteStartingAtBit((short) 0xFF00, 3, 6));
    }

    @Test
    public void testGetByteSomeSet5() {
        assertByteEquals((byte) 0x30, BitHelper.getByteStartingAtBit((short) 0xFF00, 4, 6));
    }

    @Test
    public void testGetByteSomeSet6() {
        assertByteEquals((byte) 0x08, BitHelper.getByteStartingAtBit((short) 0xFF00, 5, 4));
    }


    @Test
    public void testGetByteSomeSet7() {
        assertByteEquals((byte) 0x0F, BitHelper.getByteStartingAtBit((short) 0xFF00, 8, 4));
    }

    @Test
    public void testSwapBytes() {
        assertShortEquals((short) 0xAABB, BitHelper.swapBytes((short) 0xBBAA));
    }

    @Test
    public void testSwapBytes2() {
        assertShortEquals((short) 0x0100, BitHelper.swapBytes((short) 0x1));
    }

    @Test
    public void testSwapBytes3() {
        assertShortEquals((short) 0x1, BitHelper.swapBytes((short) 0x100));
    }


    private static void assertShortEquals(short expected, short actual) {
        assertByteArrayEquals(new byte[]{(byte) (expected >>> 8), (byte) (expected & 0xFF)},
                new byte[]{(byte) (actual >>> 8), (byte) (actual & 0xFF)
                });
    }

    private static void assertByteEquals(byte expected, byte actual) {
        assertByteArrayEquals(new byte[]{expected}, new byte[]{actual});
    }

    private static void assertByteArrayEquals(byte[] expected, byte[] actual) {
        assertEquals(Hex.encodeHexString(expected), Hex.encodeHexString(actual));
    }

}
