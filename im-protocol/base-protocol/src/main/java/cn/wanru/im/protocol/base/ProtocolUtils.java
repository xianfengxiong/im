package cn.wanru.im.protocol.base;

/**
 * @author xxf
 * @since 2019/10/19
 */
public abstract class ProtocolUtils {

    private static final int URI_OFFSET = Protocol.FILED_LEN;
    private static final int CHANNEL_OFFSET = Protocol.FILED_LEN + Protocol.FIELD_URI;

    public static short getLen(byte[] bytes) {
        assert bytes.length >= Protocol.HEAD_LEN;
        return (short) (bytes[0] << 8 | bytes[1] & 0xFF);
    }

    public static int getUri(byte[] bytes) {
        assert bytes.length >= Protocol.HEAD_LEN;
        return (bytes[URI_OFFSET] & 0xFF) << 24
            | (bytes[URI_OFFSET + 1] & 0xFF) << 16
            | (bytes[URI_OFFSET + 2] & 0xFF) << 8
            | (bytes[URI_OFFSET + 3] & 0xFF);
    }

    public static short getChannel(byte[] bytes) {
        assert bytes.length >= Protocol.HEAD_LEN;
        return (short) (bytes[CHANNEL_OFFSET] << 8 | bytes[CHANNEL_OFFSET + 1] & 0xFF);
    }


}
