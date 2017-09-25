package model;

/**
 * @author Sugeesh Chandraweera
 */
public class ContentData {
    private String contentType;
    private int level;

    public ContentData() {
    }

    public ContentData(String contentType, int level) {
        this.contentType = contentType;
        this.level = level;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
