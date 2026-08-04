package com.api.localportfogram.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.util.UUID;

public class MdcLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // HandlerMethod가 아닌 경우 (예: ResourceHttpRequestHandler) 처리
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String handlerName = handlerMethod.getMethod().getName();
            String methodName = handlerMethod.getBeanType().getSimpleName();
            String controllerInfo = methodName + "." + handlerName;
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            MDC.put("serviceName", controllerInfo);
        } else {
            // 정적 리소스 요청 등의 경우에도 traceId는 생성
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            MDC.put("serviceName", handler.getClass().getSimpleName());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        MDC.clear();
    }
}

