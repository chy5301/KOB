package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer player1Id;
    private Integer player1StartX;
    private Integer player1StartY;
    private Integer player2Id;
    private Integer player2StartX;
    private Integer player2StartY;
    private String player1Steps;
    private String player2Steps;
    private String map;
    private String loser;
    private Date createTime;
}
