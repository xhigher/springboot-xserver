package com.cheercent.springboot.xserver.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class XExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(XExceptionAdvice.class);

    @ExceptionHandler(value = { Exception.class })
    @ResponseBody
    public String handle(Exception e) {
    	if(e instanceof HttpRequestMethodNotSupportedException) {
    		return XLogicResult.errorMethod();
    	}
        logger.error("InternalError", e);
        return XLogicResult.errorInternal();
    }
}
