package com.jingtao.app.data;

/**
 * Created by shengyingpan on 2015-09-23.
 */
public class SoundClip {
    private String name;
    private String path;
    private int length; //length is in seconds

    public SoundClip(String name, String path, int length) {
        this.name = name;
        this.path = path;
        this.length = length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public String getPath() {
        return this.path;
    }
}
