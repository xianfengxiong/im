package cn.wanru.im.protocol.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 所有协议继承此类
 *
 * @author xxf
 * @since 2019/10/19
 */
public class BaseProtocol implements Protocol {

    private static final short HAS_NO_LENGTH = -1;

    private ByteBuf buf;

    private int len;
    private int uri;
    private short channel;

    private void checkInited() {
        if (buf == null) {
            buf = Unpooled.buffer(MAX_LEN, MAX_LEN);
            buf.writerIndex(HEAD_LEN);
        }
    }

    private void resetBuf() {
        buf.setIndex(0, HEAD_LEN);
    }

    @Override
    public byte[] serialize() {
        checkInited();
        len = buf.writerIndex() - buf.readerIndex();
        buf.setShort(0, len);
        buf.setInt(2, uri);
        buf.setShort(6, channel);
        byte[] ret = new byte[len];
        buf.readerIndex(0);
        buf.readBytes(ret);

        resetBuf();

        return ret;
    }

    @Override
    public void deserialize(byte[] bytes) {
        assert bytes.length <= MAX_LEN;
        this.buf = Unpooled.wrappedBuffer(bytes);
        this.len = buf.readShort();
        this.uri = buf.readInt();
        this.channel = buf.readShort();
    }

    // region Protocol type

    public void writeProtocol(Protocol protocol) {
        checkInited();
        if (protocol == null) {
            protocol = new BaseProtocol();
        }
        byte[] bytes = protocol.serialize();
        writeByteArray(bytes);
    }

    public Protocol readProtocol(Class<? extends BaseProtocol> clazz) {
        byte[] bytes = readByteArray();
        try {
            BaseProtocol protocol = clazz.newInstance();
            protocol.deserialize(bytes);
            return protocol;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // endregion

    // region primitive type

    public void writeBool(boolean value) {
        checkInited();
        buf.writeByte(value ? 1 : 0);
    }

    public boolean readBool() {
        return buf.isReadable(1) && buf.readByte() == 1;
    }

    public void writeByte(byte value) {
        checkInited();
        buf.writeByte(value);
    }

    public byte readByte() {
        return buf.isReadable(1) ? buf.readByte() : 0;
    }

    public void writeShort(short value) {
        checkInited();
        buf.writeShort(value);
    }

    public short readShort() {
        return buf.isReadable(2) ? buf.readShort() : 0;
    }

    public void writeInt(int value) {
        checkInited();
        buf.writeInt(value);
    }

    public int readInt() {
        return buf.isReadable(4) ? buf.readInt() : 0;
    }

    public void writeLong(long value) {
        checkInited();
        buf.writeLong(value);
    }

    public long readLong() {
        return buf.isReadable(8) ? buf.readLong() : 0;
    }

    public void writeString(String value) {
        checkInited();
        if (value == null) {
            writeArrayLength(HAS_NO_LENGTH);
        } else {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            int len = bytes.length;
            assert len <= Short.MAX_VALUE;
            writeArrayLength(len);
            buf.writeBytes(bytes);
        }
    }

    public String readString() {
        short len = readArrayLength();
        if (len < 0) {
            return null;
        }
        if (len == 0) {
            return "";
        }
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // endregion

    // region array type

    private short readArrayLength() {
        return buf.isReadable(2) ? buf.readShort() : HAS_NO_LENGTH;
    }

    private void writeArrayLength(int length) {
        buf.writeShort(length);
    }

    public void writeByteArray(byte[] value) {
        checkInited();
        int len = value == null ? HAS_NO_LENGTH : value.length;
        writeArrayLength(len);
        if (len > 0) {
            buf.writeBytes(value);
        }
    }

    public byte[] readByteArray() {
        short len = readArrayLength();
        if (len < 0) {
            return null;
        }
        byte[] ret = new byte[len];
        buf.readBytes(ret);
        return ret;
    }

    public void writeShortArray(short[] value) {
        checkInited();
        int len = value == null ? HAS_NO_LENGTH : value.length;
        writeArrayLength(len);
        for (int i = 0; i < len; i++) {
            buf.writeShort(value[i]);
        }
    }

    public short[] readShortArray() {
        int len = readArrayLength();
        if (len < 0) {
            return null;
        }
        short[] ret = new short[len];
        for (int i = 0; i < len; i++) {
            ret[i] = buf.readShort();
        }
        return ret;
    }

    public void writeIntArray(int[] value) {
        checkInited();
        int len = value == null ? HAS_NO_LENGTH : value.length;
        writeArrayLength(len);
        for (int i = 0; i < len; i++) {
            buf.writeInt(value[i]);
        }
    }

    public int[] readIntArray() {
        int len = readArrayLength();
        if (len < 0) {
            return null;
        }
        int[] ret = new int[len];
        for (int i = 0; i < len; i++) {
            ret[i] = buf.readInt();
        }
        return ret;
    }

    public void writeLongArray(long[] value) {
        checkInited();
        int len = value == null ? HAS_NO_LENGTH : value.length;
        writeArrayLength(len);
        for (int i = 0; i < len; i++) {
            buf.writeLong(value[i]);
        }
    }

    public long[] readLongArray() {
        int len = readArrayLength();
        if (len < 0) {
            return null;
        }
        long[] ret = new long[len];
        for (int i = 0; i < len; i++) {
            ret[i] = buf.readLong();
        }
        return ret;
    }

    public void writeStringArray(String[] value) {
        checkInited();
        int len = value == null ? HAS_NO_LENGTH : value.length;
        writeArrayLength(len);
        for (int i = 0; i < len; i++) {
            writeString(value[i]);
        }
    }

    public String[] readStringArray() {
        int len = readArrayLength();
        if (len < 0) {
            return null;
        }
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            ret[i] = readString();
        }
        return ret;
    }

    // endregion

    // region collection type

    private void writeObj(Object obj) {
        if (obj instanceof Boolean) {
            writeBool((Boolean) obj);
        } else if (obj instanceof Byte) {
            writeByte((Byte) obj);
        } else if (obj instanceof Short) {
            writeShort((Short) obj);
        } else if (obj instanceof Integer) {
            writeInt((Integer) obj);
        } else if (obj instanceof Long) {
            writeLong((Long) obj);
        } else if (obj instanceof String) {
            writeString((String) obj);
        } else if (obj instanceof Protocol) {
            writeProtocol((Protocol) obj);
        } else {
            throw new RuntimeException("Unsupported type [" + obj.getClass() + "]");
        }
    }

    private Object readObj(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return readBool();
        } else if (clazz == Byte.class) {
            return readByte();
        } else if (clazz == Short.class) {
            return readShort();
        } else if (clazz == Integer.class) {
            return readInt();
        } else if (clazz == Long.class) {
            return readLong();
        } else if (clazz == String.class) {
            return readString();
        } else if (Protocol.class.isAssignableFrom(clazz)) {
            return readProtocol((Class<? extends BaseProtocol>) clazz);
        }
        throw new RuntimeException("Unsupported type " + clazz.getName());
    }

    private short readCollectionSize() {
        return readArrayLength();
    }

    private void writeCollectionSize(int size) {
        buf.writeShort(size);
    }

    public <E> void writeList(List<E> list) {
        checkInited();
        int size = list == null ? HAS_NO_LENGTH : list.size();
        writeCollectionSize(size);
        if (list != null) {
            for (E e : list) {
                writeObj(e);
            }
        }
    }

    public <E> List<E> readList(Class<E> clazz) {
        short size = readCollectionSize();
        if (size < 0) {
            return null;
        }
        List ret = new ArrayList(size);
        for (short i = 0; i < size; i++) {
            ret.add(readObj(clazz));
        }
        return ret;
    }

    public <E> void writeSet(Set<E> set) {
        checkInited();
        int size = set == null ? HAS_NO_LENGTH : set.size();
        writeCollectionSize(size);
        if (set != null) {
            for (E e : set) {
                writeObj(e);
            }
        }
    }

    public <E> Set<E> readSet(Class<E> clazz) {
        short size = readCollectionSize();
        if (size < 0) {
            return null;
        }
        Set ret = new HashSet(size);
        for (short i = 0; i < size; i++) {
            ret.add(readObj(clazz));
        }
        return ret;
    }

    public <K, V> void writeMap(Map<K, V> map) {
        checkInited();
        int size = map == null ? HAS_NO_LENGTH : map.size();
        writeCollectionSize(size);
        if (map != null) {
            for (Entry<K, V> ety : map.entrySet()) {
                writeObj(ety.getClass());
                writeObj(ety.getValue());
            }
        }
    }

    public <K, V> Map<K, V> readMap(Class<? extends K> keyClazz, Class<? extends V> valClazz) {
        short size = readCollectionSize();
        if (size < 0) {
            return null;
        }
        Map ret = new HashMap(capacity(size));
        for (int i = 0; i < size; i++) {
            Object key = readObj(keyClazz);
            Object val = readObj(valClazz);
            ret.put(key, val);
        }
        return ret;
    }

    private static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            assert expectedSize >= 0;
            return expectedSize + 1;
        }
        return (int) ((float) expectedSize / 0.75F + 1.0F);
    }


    // endregion

}
