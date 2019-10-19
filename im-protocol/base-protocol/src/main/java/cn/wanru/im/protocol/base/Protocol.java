package cn.wanru.im.protocol.base;

/**
 * 数据包的结构:协议头 + 负荷
 * ------------------------------------------
 * | len(2) | uri(4) | channel(2) | payload |
 * ------------------------------------------
 * <p>
 * 协议头 : len,uri,channel
 * len - 2字节，数据包总长度，包括协议头
 * uri - 4字节，协议的具体类型
 * channel - 2字节，业务线标识
 * 负荷 : payload 具体的数据
 * <p>
 * 协议的定义是向后兼容的，新加的字段不会影响老协议的解析
 * 字段的规定：
 * 1. 数组类型的字段长度最大为Short.MAX_VALUE
 * 2. String类型字段长度最大为Integer.MAX_VALUE
 * 3. 集合类型的value不能为null，map类型的key,value不能为null，size最大值为Short.MAX_VALUE
 * 4. 集合类型的value必须是基本类型(包括string)或Protocol类型,
 * map类型的key,value必须是基本类型(包括string)获Protocol类型
 * <p>
 * 字段的初始值规定：
 * 1. 基本类型的初始值与java定义一样
 * 2. 数组类型的初始值为长度为0的数组
 *
 * @author xxf
 * @since 2019/10/19
 */
public interface Protocol {

    int MAX_LEN = 64 * 1024; // 64kb

    short FILED_LEN = 2;

    short FIELD_URI = 4;

    short FIELD_CHANNEL = 2;

    short HEAD_LEN = FILED_LEN + FIELD_URI + FIELD_CHANNEL;

    byte[] serialize();

    void deserialize(byte[] bytes);

}
