package com.exchange.portal.exchangeportal.common.rest;

import com.exchange.portal.exchangeportal.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CommonRestServiceImpl implements CommonRestService {

    private RestTemplate restTemplate;

    private static HttpHeaders defaultHeaders;

    static {
        defaultHeaders = new HttpHeaders();
        defaultHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        defaultHeaders.set("Accept-Encoding", MediaType.APPLICATION_JSON_VALUE);
    }

    @Autowired
    public CommonRestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public <T> Optional<T> commonPost(String url, String body, HttpHeaders headers, ParameterizedTypeReference<T> typeReference) {
        try {
            HttpEntity httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
            T responseBody = response.getBody();

            return Optional.ofNullable(responseBody);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Optional.empty();
    }

    public <T> Optional<T> postForResult(String url, String body, HttpHeaders headers, ParameterizedTypeReference<Result<T>> typeReference) {
        try {
            HttpEntity httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Result<T>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
            Result<T> responseBody = response.getBody();
            if (responseBody == null || responseBody.getStatus() != Result.SUCCESS_CODE) {
                return Optional.empty();
            }


            T responseData = responseBody.getData();

            return Optional.ofNullable(responseData);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Optional.empty();
    }

    public <T> Optional<T> commonGet(String url, HttpHeaders headers, ParameterizedTypeReference<T> typeReference) {
        try {
            HttpEntity httpEntity = new HttpEntity<>(headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, typeReference);
            T responseBody = response.getBody();

            return Optional.ofNullable(responseBody);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getForResult(String url, HttpHeaders headers, ParameterizedTypeReference<Result<T>> typeReference) {
        try {
            HttpEntity<T> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<Result<T>> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, typeReference);
            Result<T> responseBody = response.getBody();
            if (responseBody == null || responseBody.getStatus() != Result.SUCCESS_CODE) {
                return Optional.empty();
            }
            T responseData = responseBody.getData();


            return Optional.ofNullable(responseData);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Optional.empty();
    }

    public <T> Optional<T> getForResult(String url, ParameterizedTypeReference<Result<T>> typeReference, Map<String, String> uriVariables) {
        try {
            HttpEntity<T> httpEntity = new HttpEntity<>(defaultHeaders);
            ResponseEntity<Result<T>> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, typeReference, uriVariables);
            Result<T> responseBody = response.getBody();
            if (responseBody == null || responseBody.getStatus() != Result.SUCCESS_CODE) {
                return Optional.empty();
            }
            T responseData = responseBody.getData();


            return Optional.ofNullable(responseData);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Optional.empty();
    }

    public <T> Optional<T> commonPost(String url, String body, ParameterizedTypeReference<T> typeReference) {
        return commonPost(url, body, defaultHeaders, typeReference);
    }

    public <T> Optional<T> postForResult(String url, String body, ParameterizedTypeReference<Result<T>> typeReference) {
        return postForResult(url, body, defaultHeaders, typeReference);
    }

    public <T> Optional<T> commonGet(String url, ParameterizedTypeReference<T> typeReference) {
        return commonGet(url, defaultHeaders, typeReference);
    }

    public <T> Optional<T> getForResult(String url, ParameterizedTypeReference<Result<T>> typeReference) {
        return getForResult(url, defaultHeaders, typeReference);
    }


}