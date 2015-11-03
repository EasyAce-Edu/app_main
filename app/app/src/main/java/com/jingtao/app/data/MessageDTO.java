package com.jingtao.app.data;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by maoningguo on 2015-09-22.
 */
@Data
public class MessageDTO implements Serializable{
    // private Date transferTime;
    //  private String UserFrom;
    //   private String UserTo;
    private String textMsg;
    private String zipFileUri;
    public MessageDTO(String textMsg, String zipFileUri) {
        this.textMsg = textMsg;
        this.zipFileUri = zipFileUri;
    }

    public String getTextMsg() {
        return this.textMsg;
    }
}
