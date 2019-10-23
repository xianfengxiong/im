package cn.wanru.im.protocol.base;

/**
 * @author xxf
 * @since 2019/10/24
 */
public class TextMsg extends BaseProtocol {

    private String text;

    private String fontName;

    private short fontSize;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public short getFontSize() {
        return fontSize;
    }

    public void setFontSize(short fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public byte[] serialize() {
        writeString(text);
        writeString(fontName);
        writeShort(fontSize);
        return super.serialize();
    }

    @Override
    public void deserialize(byte[] bytes) {
        super.deserialize(bytes);
        text = readString();
        fontName = readString();
        fontSize = readShort();
    }

    @Override
    public String toString() {
        return "TextMsg{" +
            "text='" + text + '\'' +
            ", fontName='" + fontName + '\'' +
            ", fontSize=" + fontSize +
            '}';
    }
}
