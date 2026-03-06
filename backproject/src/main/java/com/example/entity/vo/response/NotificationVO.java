package com.example.entity.vo.response;

import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationVO implements BaseData {
    Integer id;
    int uid;
    String title;
    String content;
    String type;
    String url;
    Date time;
}
