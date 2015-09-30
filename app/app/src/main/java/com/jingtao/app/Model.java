package com.jingtao.app;

/**
 * Created by shirley on 15-09-30.
 */
public class Model {
    private String subject;
    private String text;
    private String status;
    private String hintType;

    public Model(String subject, String text, String status, String hintType) {
        super();
        this.subject = subject;
        this.text = text;
        this.status = status;
        this.hintType = hintType;
    }

    public String getSubject(){
        return this.subject;
    }

    public String getText(){
        return this.text;
    }

    public String getStatus(){
        return this.status;
    }

    public String getHintType(){
        return this.hintType;
    }

}
