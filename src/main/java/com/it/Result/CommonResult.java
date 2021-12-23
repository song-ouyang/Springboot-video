package com.it.Result;

import lombok.Data;

/**
 * @author lixiaolong
 * @date 2020/12/18 20:17
 * @description
 */
@Data
public class CommonResult<T> {

    private Integer code;
    private String message;
    private T data;
    public CommonResult() {
    }
    public CommonResult(Integer code, String message) {
        this(code, message, null);
    }
    public CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
