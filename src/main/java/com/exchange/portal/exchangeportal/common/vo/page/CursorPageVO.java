package com.exchange.portal.exchangeportal.common.vo.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;

/**
 * Implement cursor mechanism.
 */
@Data
public class CursorPageVO implements Serializable, PageVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "next page cursor")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextCursor = Strings.EMPTY;

    @ApiModelProperty(value = "previous page cursor")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previousCursor = Strings.EMPTY;

}
