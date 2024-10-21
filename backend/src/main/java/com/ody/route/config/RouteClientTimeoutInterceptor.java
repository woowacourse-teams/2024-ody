package com.ody.route.config;

import com.ody.common.exception.OdyServerErrorException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class RouteClientTimeoutInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (SocketTimeoutException exception) {
            log.error("RouteClient timeout error - ", exception);
            throw new OdyServerErrorException("RouteClient timeout 에러 입니다.");
        } catch (IOException exception) {
            log.error("RouteClient request fail - ", exception);
            throw new OdyServerErrorException("RouteClient 요청 실패했습니다.");
        }
    }
}
