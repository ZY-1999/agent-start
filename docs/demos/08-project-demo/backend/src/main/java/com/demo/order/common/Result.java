package com.demo.order.common;

/**
 * 统一接口包装。
 *
 * @param <T> 业务数据
 * @author 张颖
 * @date 2026/10/13
 */
public class Result<T> {

    /** 业务码，成功为 0。 */
    private Integer code;

    /** 说明。 */
    private String message;

    /** 业务数据，失败时为空。 */
    private T data;

    /**
     * 成功结果。
     *
     * @param data 业务数据
     * @param <T> 业务数据类型
     * @return code 为 0 的结果
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<T>();
        result.setCode(0);
        result.setMessage("ok");
        result.setData(data);
        return result;
    }

    /**
     * 失败结果，data 为空。
     *
     * @param code 业务码
     * @param message 说明
     * @param <T> 业务数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * @return 业务码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code 业务码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return 说明
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message 说明
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return 业务数据
     */
    public T getData() {
        return data;
    }

    /**
     * @param data 业务数据
     */
    public void setData(T data) {
        this.data = data;
    }
}
