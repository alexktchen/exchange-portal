package com.exchange.portal.exchangeportal.domain.message;

import java.util.List;
public interface MessageDeserializer<T> {
    List<T> deserialize(List<String> messageList);
}
