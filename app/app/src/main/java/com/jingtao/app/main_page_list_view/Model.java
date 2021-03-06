package com.jingtao.app.main_page_list_view;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shirley on 15-09-30.
 */
public class Model implements Serializable{
    private String subject;
    private String text;
    private String status;
    private String hintType;
    private String msglst;
    private String askedby;
    private String id;
    private String answeredby;
    private String createdtime;
    private String updatedtime;
    private ArrayList<String> Tag;
    private boolean IsStudent;


    public Model(String subject, String text, String status, String hintType,
                 String askedby,String id,String createdtime,String updatedtime,String msglst,String answeredBy,
                 boolean IsStudent) {
        super();
        this.subject = subject;
        this.text = text;
        this.status = status;
        this.hintType = hintType;
        this.askedby=askedby;
        this.id=id;
        this.createdtime=createdtime;
        this.updatedtime=updatedtime;
        this.msglst=msglst;
        this.answeredby=answeredBy;
        this.IsStudent=IsStudent;
        this.Tag= new ArrayList<>();
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
    public String getAskedby(){
        return this.askedby;
    }
    public String getId(){
        return this.id;
    }
    public  String getAnsweredby(){
        return this.answeredby;
    }
    public  String getCreatedtime(){
        return this.createdtime;
    }
    public String getUpdatedtime(){
        return this.updatedtime;
    }
    public String getMsglst(){
        return this.msglst;
    }
    public void setMsglst(String msgs){this.msglst=msgs;}
    public void setUpdatedtime(String time){this.updatedtime=time;}
    public boolean IsStudent(){return this.IsStudent;}
    public ArrayList<String> getTag(){return this.Tag;}
    public void setTag(ArrayList<String> tags){this.Tag=tags;}
}
