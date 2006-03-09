package ie.omk.smpp.util;

public class UTF16Encoding extends ie.omk.smpp.util.AlphabetEncoding {
    private static final int DCS = 8;

    private static final UTF16Encoding BE_INSTANCE = new UTF16Encoding(true);

    private static final UTF16Encoding LE_INSTANCE = new UTF16Encoding(false);

    private String encType = "UTF-16BE";

    /**
     * Construct a new UTF16 encoding.
     * 
     * @param bigEndian
     *            true to use UTF-16BE, false to use UTF-16LE.
     */
    public UTF16Encoding(boolean bigEndian) {
        super(DCS);

        if (!bigEndian) {
            encType = "UTF-16LE";
        }
    }

    /**
     * Get the singleton instance of the big-endian UTF16Encoding.
     * @deprecated
     */
    public static UTF16Encoding getInstance() {
        return BE_INSTANCE;
    }

    /**
     * Get the singleton instance of either the big-endian or little-endian
     * instance of UTF16Encoding.
     * 
     * @param bigEndian
     *            true to get the big-endian instance, false to get the
     *            little-endian instance.
     * @deprecated
     */
    public static UTF16Encoding getInstance(boolean bigEndian) {
        if (bigEndian) {
            return BE_INSTANCE;
        } else {
            return LE_INSTANCE;
        }
    }

    /**
     * Decode SMS message text to a Java String. The SMS message is expected to
     * be in UTF16 format.
     */
    public String decodeString(byte[] b) {
       try {
           if (b != null) {
               return new String(b, encType);
           }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return "";
    }

    /**
     * Encode a Java String to bytes using UTF16.
     */
    public byte[] encodeString(String s) {
        try {
            if (s != null) {
                return s.getBytes(encType);
            }
        } catch (java.io.UnsupportedEncodingException x) {
        }
        return new byte[0];
    }
}

