package ie.omk.smpp.message.tlv;

import ie.omk.smpp.message.param.BitmaskParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import junit.framework.TestCase;

public class TLVTableTest extends TestCase {
    public void testTLVTableAddParams() {
        TLVTable table = new TLVTable();

        try {
            assertFalse(table.isSet(Tag.DEST_ADDR_SUBUNIT));
            table.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
            assertTrue(table.isSet(Tag.DEST_ADDR_SUBUNIT));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 1");
        }

        try {
            assertFalse(table.isSet(Tag.DEST_TELEMATICS_ID));
            table.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
            assertTrue(table.isSet(Tag.DEST_TELEMATICS_ID));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 2");
        }

        try {
            assertFalse(table.isSet(Tag.QOS_TIME_TO_LIVE));
            table.set(Tag.QOS_TIME_TO_LIVE, new Long(0xe4e3e2e1L));
            assertTrue(table.isSet(Tag.QOS_TIME_TO_LIVE));
        } catch (Exception x) {
            fail("Failed to set IntegerValue size 4");
        }

        try {
            assertFalse(table.isSet(Tag.ADDITIONAL_STATUS_INFO_TEXT));
            table.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
            assertTrue(table.isSet(Tag.ADDITIONAL_STATUS_INFO_TEXT));
        } catch (Exception x) {
            fail("Failed to set StringValue.");
        }

        try {
            assertFalse(table.isSet(Tag.CALLBACK_NUM_ATAG));
            byte[] b = {0x67, 0x67, 0x67};
            table.set(Tag.CALLBACK_NUM_ATAG, b);
            assertTrue(table.isSet(Tag.CALLBACK_NUM_ATAG));
        } catch (Exception x) {
            fail("Failed to set OctetValue.");
        }
        try {
            assertFalse(table.isSet(Tag.MS_MSG_WAIT_FACILITIES));
            BitSet bitSet = new BitSet();
            table.set(Tag.MS_MSG_WAIT_FACILITIES, bitSet);
            assertTrue(table.isSet(Tag.MS_MSG_WAIT_FACILITIES));
            Tag newTag = Tag.defineTag(0xdead, new BitmaskParamDescriptor(), 1);
            assertFalse(table.isSet(newTag));
            table.set(newTag, bitSet);
            assertTrue(table.isSet(newTag));
        } catch (Exception x) {
            fail("Failed to set Bitmask value");
        }
    }
    
    public void testTLVTableFailAddParams() {
        TLVTable tab = new TLVTable();

        try {
            // Try and set a string that's too long.
            String longString = new String(
                    "111111111111111111111111111111111111111111111111111111111111111111111111111"
                    + "222222222222222222222222222222222222222222222222222222222222222222222222222"
                    + "333333333333333333333333333333333333333333333333333333333333333333333333333"
                    + "444444444444444444444444444444444444444444444444444444444444444444444444444"
                    + "555555555555555555555555555555555555555555555555555555555555555555555555555"
                    + "666666666666666666666666666666666666666666666666666666666666666666666666666");
            tab.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, longString);
            fail("Set a StringValue that was too long.");
        } catch (InvalidSizeForValueException x) {
        }

        try {
            // Try and set an OctetValue that's too short
            byte[] b = new byte[1];
            tab.set(Tag.SOURCE_SUBADDRESS, b);
            fail("Set an OctetValue that was too short.");
        } catch (InvalidSizeForValueException x) {
        }

        try {
            // Try and set an OctetValue that's too long
            byte[] b = new byte[70];
            tab.set(Tag.CALLBACK_NUM_ATAG, b);
            fail("Set an OctetValue that was too long.");
        } catch (InvalidSizeForValueException x) {
        }
    }

    public void testTLVTableSerialize() {
        // If testTLVTableAddParams fails, this will fail too...make sure it's
        // working first!
        // First, create a table with at least one parameter in it for
        // each type of encoder defined.
        TLVTable origTable = new TLVTable();
        byte[] b = {0x56, 0x67, 0x69};
        BitSet bitSet = new BitSet();
        bitSet.set(3);
        // 0x56 == 86 decimal
        origTable.set(Tag.DEST_ADDR_SUBUNIT, new Integer(0x56));
        // 0xe2e1 == 58081 decimal
        origTable.set(Tag.DEST_TELEMATICS_ID, new Integer(0xe2e1));
        origTable.set(Tag.QOS_TIME_TO_LIVE, new Long((long) Integer.MAX_VALUE));
        origTable.set(Tag.ADDITIONAL_STATUS_INFO_TEXT, "Test info");
        origTable.set(Tag.CALLBACK_NUM_ATAG, b);
        origTable.set(Tag.MS_MSG_WAIT_FACILITIES, bitSet);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            origTable.writeTo(out);
        } catch (IOException x) {
            fail("I/O Exception while writing to output stream.");
        }
        byte[] serialized = out.toByteArray();

        // The table must report the same length as it actually serializes to..
        if (origTable.getLength() != serialized.length) {
            fail("Table getLength is different to actual encoded length");
        }

        ParsePosition position = new ParsePosition(0);
        TLVTable newTable = new TLVTable();
        newTable.readFrom(serialized, position, serialized.length);
        doTableAssertions(origTable, newTable);
        assertEquals(serialized.length, position.getIndex());

        position = new ParsePosition(0);
        newTable = new TLVTable();
        newTable.readFrom(serialized, position, serialized.length);
        newTable.parseAllOpts();
        doTableAssertions(origTable, newTable);
        assertEquals(serialized.length, position.getIndex());
    }

    /**
     * This test creates a byte array representing a TLVTable which contains a
     * tag that the API does not know about. The API should be able to decode
     * any optional parameter that is well-formed - the fact that it doesn't
     * know about it beforehand should not cause an error in the API.
     */
    public void testTLVTableDeSerializeUnknown() throws Exception {
        // Set up a byte array which contains 2 known optional parameters
        // followed by 2 unknowns.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ParamDescriptor descriptor;
        int length = 0;

        descriptor = Tag.DEST_TELEMATICS_ID.getParamDescriptor();
        Integer i = new Integer(0xbcad);
        length = descriptor.sizeOf(i);
        SMPPIO.writeShort(Tag.DEST_TELEMATICS_ID.intValue(), out);
        SMPPIO.writeShort(length, out);
        descriptor.writeObject(i, out);

        String v = "smppapi tlv tests";
        descriptor = Tag.ADDITIONAL_STATUS_INFO_TEXT.getParamDescriptor();
        length = descriptor.sizeOf(v);
        SMPPIO.writeShort(Tag.ADDITIONAL_STATUS_INFO_TEXT.intValue(), out);
        SMPPIO.writeShort(length, out);
        descriptor.writeObject(v, out);

        // Tag '0xcafe', length 2.
        byte[] cafe = new byte[] {
                (byte) 0xca,
                (byte) 0xfe,
                (byte) 0x00,
                (byte) 0x02,
                (byte) 0xfe,
                (byte) 0xed,
        };
        // Tag '0xbeef', length 5
        byte[] beef = new byte[] {
                (byte) 0xbe,
                (byte) 0xef,
                (byte) 0x00,
                (byte) 0x05,
                (byte) 0xba,
                (byte) 0xbe,
                (byte) 0xde,
                (byte) 0xad,
                (byte) 0x99,
        };
        out.write(cafe);
        out.write(beef);

        byte[] b = out.toByteArray();
        try {
            // Run the test - attempt to deserialize the table.
            TLVTable tab = new TLVTable();
            ParsePosition position = new ParsePosition(0);
            tab.readFrom(b, position, b.length);
            assertEquals(b.length, position.getIndex());

            tab.parseAllOpts();

            assertEquals(tab.get(Tag.DEST_TELEMATICS_ID), i);
            assertEquals(tab.get(Tag.ADDITIONAL_STATUS_INFO_TEXT), v);

            b = (byte[]) tab.get(0xcafe);
            byte[] expectedValue = {(byte) 0xfe, (byte) 0xed};

            assertTrue(Arrays.equals(b, expectedValue));

            b = (byte[]) tab.get(0xbeef);
            expectedValue = new byte[] {(byte) 0xba, (byte) 0xbe, (byte) 0xde,
                    (byte) 0xad, (byte) 0x99, };

            assertTrue(Arrays.equals(b, expectedValue));

        } catch (Exception x) {
            x.printStackTrace(System.err);
            fail("Deserialize failed. " + x.getMessage());
        }
    }
    
    private void doTableAssertions(TLVTable origTable, TLVTable newTable) {
        assertEquals(((Number) origTable.get(Tag.DEST_ADDR_SUBUNIT)).longValue(),
                ((Number) newTable.get(Tag.DEST_ADDR_SUBUNIT)).longValue());
        assertEquals(((Number) origTable.get(Tag.DEST_TELEMATICS_ID)).longValue(),
                ((Number) newTable.get(Tag.DEST_TELEMATICS_ID)).longValue());
        assertEquals(((Number) origTable.get(Tag.QOS_TIME_TO_LIVE)).longValue(),
                ((Number) newTable.get(Tag.QOS_TIME_TO_LIVE)).longValue());
        assertEquals(origTable.get(Tag.ADDITIONAL_STATUS_INFO_TEXT),
                newTable.get(Tag.ADDITIONAL_STATUS_INFO_TEXT));
        assertTrue(Arrays.equals((byte[]) origTable.get(Tag.CALLBACK_NUM_ATAG),
                        (byte[]) newTable.get(Tag.CALLBACK_NUM_ATAG)));
        assertEquals((BitSet) origTable.get(Tag.MS_MSG_WAIT_FACILITIES),
                (BitSet) newTable.get(Tag.MS_MSG_WAIT_FACILITIES));
    }
}