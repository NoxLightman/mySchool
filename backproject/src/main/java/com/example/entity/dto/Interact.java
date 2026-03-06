package com.example.entity.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class Interact {
    Integer tid;
    Integer uid;
    Date time;
    String type;

    public String toKey(){
        return tid + ":" + uid;
    }

    public Interact(Integer tid, Integer uid, Date time, String type) {
        this.tid = tid;
        this.uid = uid;
        this.time = time;
        this.type = type;
    }

    public static Interact parseInteract(String str, String type){
        String [] split = str.split(":");
        return new Interact(Integer.parseInt( split[0] ), Integer.parseInt( split[1] ), new Date(), type);
    }
}
