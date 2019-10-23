package cn.wanru.im.protocol.base;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author xxf
 * @since 2019/10/24
 */
public class ProtocolTest {

    @Test
    public void testSerialize() {
        TextMsg msg = new TextMsg();
        msg.setText("hello");
        msg.setFontName("yahei");
        msg.setFontSize((short) 12);
        byte[] bytes = msg.serialize();

        assertThat((short) 24, equalTo(ProtocolUtils.getLen(bytes)));
        assertThat(0, equalTo(ProtocolUtils.getUri(bytes)));
        assertThat((short) 0, equalTo(ProtocolUtils.getChannel(bytes)));

        TextMsg msg1 = new TextMsg();
        msg1.deserialize(bytes);

        assertThat(msg.getText(), equalTo(msg1.getText()));
        assertThat(msg.getFontName(), equalTo(msg1.getFontName()));
        assertThat(msg.getFontSize(), equalTo(msg1.getFontSize()));
    }

    @Test
    public void testMultiSerialize() {
        TextMsg msg = new TextMsg();
        msg.setText("hello");
        msg.setFontName("yahei");
        msg.setFontSize((short) 12);
        byte[] bytes = msg.serialize();
        byte[] bytes1 = msg.serialize();

        assertThat(bytes, equalTo(bytes1));

        msg.setText("world");
        byte[] bytes2 = msg.serialize();

        assertThat(bytes.length, equalTo(bytes2.length));
    }

}
