package com.exchange.portal.exchangeportal.common.vo.page;

import com.exchange.portal.exchangeportal.common.jackson.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Implement offset mechanism.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonView(BaseJsonView.class)
public class OffsetPageVO implements PageVO, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private Long totalCount;
    private Integer totalPage;

}
