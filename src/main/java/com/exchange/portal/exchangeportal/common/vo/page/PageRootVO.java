package com.exchange.portal.exchangeportal.common.vo.page;

import com.exchange.portal.exchangeportal.common.jackson.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分頁父類別
 *
 * @param <T>    資料型別
 * @param <PAGE> 分頁實作
 */
@Data
@JsonView(BaseJsonView.class)
@NoArgsConstructor
@AllArgsConstructor
public class PageRootVO<T, PAGE extends PageVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected List<T> data = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected PAGE paging;

}
