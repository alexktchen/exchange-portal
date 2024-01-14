package com.exchange.portal.exchangeportal.common.vo.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSummaryVO<T, S, PAGEVO extends PageVO> extends PageRootVO<T, PAGEVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    private S summary;

    public PageSummaryVO(List<T> data, S summary, PAGEVO paging) {
        super();
        this.summary = summary;
        this.data = data;
        this.paging = paging;
    }

}
