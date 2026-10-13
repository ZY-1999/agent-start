package com.demo.order.web;

/**
 * 重置示例数据的结果。
 *
 * @author 张颖
 * @date 2026/10/13
 */
public class ResetResponse {

    /** 重新写入的条数。 */
    private Integer inserted;

    /**
     * @param inserted 重新写入的示例条数
     */
    public ResetResponse(int inserted) {
        this.inserted = Integer.valueOf(inserted);
    }

    /** 返回重新写入的条数。 */
    public Integer getInserted() {
        return inserted;
    }

    /** 设置重新写入的条数。 */
    public void setInserted(Integer inserted) {
        this.inserted = inserted;
    }
}
