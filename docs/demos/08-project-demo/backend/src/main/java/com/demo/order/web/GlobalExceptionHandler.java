package com.demo.order.web;

import com.demo.order.common.Result;
import com.demo.order.service.BadOrderRequestException;
import com.demo.order.service.OrderConflictException;
import com.demo.order.service.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * 把创建失败收成统一的 Result。
 *
 * @author 张颖
 * @date 2026/10/13
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 缺字段或校验不通过时返回 400。
     *
     * @param ex 校验异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleInvalid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message = "请求参数不合法";
        if (!fieldErrors.isEmpty()) {
            List<String> parts = new ArrayList<String>();
            for (FieldError fieldError : fieldErrors) {
                parts.add(fieldError.getField() + fieldError.getDefaultMessage());
            }
            message = String.join(";", parts);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, message));
    }

    /**
     * 请求体类型不合法时返回 400。
     *
     * @param ex 读取异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, "请求体不合法"));
    }

    /**
     * 订单号重试耗尽时返回 409。
     *
     * @param ex 冲突异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(OrderConflictException.class)
    public ResponseEntity<Result<Void>> handleConflict(OrderConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.fail(409, ex.getMessage()));
    }

    /**
     * 页码或页大小不合法时返回 400。
     *
     * @param ex 参数异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(BadOrderRequestException.class)
    public ResponseEntity<Result<Void>> handleBadRequest(BadOrderRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, ex.getMessage()));
    }

    /**
     * 查询参数类型不合法时返回统一的 400。
     *
     * @param ex 类型转换异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, "请求参数不合法"));
    }

    /**
     * 订单不存在时返回 404。
     *
     * @param ex 未找到异常
     * @return 空 data 的失败结果
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail(404, ex.getMessage()));
    }
}
