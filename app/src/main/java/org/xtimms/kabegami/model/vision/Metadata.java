package org.xtimms.kabegami.model.vision;

public class Metadata {

    public int height;
    public int width;
    public String format;

    public Metadata() {
    }

    public Metadata(int height, int width, String format) {
        this.height = height;
        this.width = width;
        this.format = format;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
