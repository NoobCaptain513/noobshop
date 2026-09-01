package com.app.noobshop.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MQ 业务消息消费记录，唯一 changeId 保证库存增量更新只执行一次。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mq_consume_record")
public class MqConsumeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String changeId;

    private String orderNo;

    private Long productId;

    private Long specId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
