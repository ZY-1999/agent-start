-- MySQL 主键索引名固定为 PRIMARY，无法按规范写成 pk_id。
CREATE TABLE t_order (
    id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no char(20) NOT NULL COMMENT '订单号',
    customer_name varchar(64) NOT NULL COMMENT '客户名称',
    product_name varchar(128) NOT NULL COMMENT '商品名称',
    quantity int NOT NULL COMMENT '数量',
    unit_price decimal(10, 2) NOT NULL COMMENT '单价',
    total_amount decimal(10, 2) NOT NULL COMMENT '总金额',
    status varchar(16) NOT NULL COMMENT '状态',
    remark varchar(255) DEFAULT NULL COMMENT '备注',
    is_deleted tinyint unsigned NOT NULL DEFAULT 0 COMMENT '是否删除：1是 0否',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单';
