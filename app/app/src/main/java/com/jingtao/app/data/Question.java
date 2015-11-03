package com.jingtao.app.data;
import java.io.Serializable;

import lombok.Data;

/**
 * Created by maoningguo on 2015-09-22.
 */



@Data
public class Question implements Serializable {

    public enum Type  {
        OPEN, CLOSE, REOPEN, FINAL, CANCELLED
    }
    // private Date CreatedAt;
    // private Date UpdatedAt;
    private String subject;
    // private Type type;
    private String askedBy;
    //private String Ta;
    // private String[] tags;
    private MessageDTO messageDTO;
    public Question(String subject, String askedBy, MessageDTO messageDTO) {
        this.subject = subject;
        this.askedBy = askedBy;
        this.messageDTO = messageDTO;
    }

    public MessageDTO getMessageDTO() {
        return this.messageDTO;
    }

    public void setMessageDTO(MessageDTO mdto) {
        this.messageDTO = mdto;
    }
}
