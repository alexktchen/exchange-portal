package com.exchange.portal.exchangeportal.common.rest;

import com.exchange.portal.exchangeportal.common.vo.Result;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

public interface CommonRestService {

    <T> Optional<T> commonPost(String url, String body, HttpHeaders headers, ParameterizedTypeReference<T> typeReference);

    <T> Optional<T> postForResult(String url, String body, HttpHeaders headers, ParameterizedTypeReference<Result<T>> typeReference);

    <T> Optional<T> commonGet(String url, HttpHeaders headers, ParameterizedTypeReference<T> typeReference);

    <T> Optional<T> getForResult(String url, HttpHeaders headers, ParameterizedTypeReference<Result<T>> typeReference);

    <T> Optional<T> getForResult(String url, ParameterizedTypeReference<Result<T>> typeReference, Map<String, String> uriVariables);

    <T> Optional<T> commonPost(String url, String body, ParameterizedTypeReference<T> typeReference);

    <T> Optional<T> postForResult(String url, String body, ParameterizedTypeReference<Result<T>> typeReference);

    <T> Optional<T> commonGet(String url, ParameterizedTypeReference<T> typeReference);

    <T> Optional<T> getForResult(String url, ParameterizedTypeReference<Result<T>> typeReference);
}
