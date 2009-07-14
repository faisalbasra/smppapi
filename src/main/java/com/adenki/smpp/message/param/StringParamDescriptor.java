package com.adenki.smpp.message.param;

import java.io.IOException;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

public class StringParamDescriptor extends AbstractDescriptor {
    private static final long serialVersionUID = 2L;
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((String) obj).length();
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, PacketEncoder encoder) throws IOException {
        if (obj != null) {
            String s = obj.toString();
            encoder.writeString(s, s.length());
        }
    }

    public Object readObject(PacketDecoder decoder, int length) throws IOException {
        return decoder.readString(length);
    }
}
