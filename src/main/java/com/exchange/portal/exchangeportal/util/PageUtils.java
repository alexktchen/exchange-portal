package com.exchange.portal.exchangeportal.util;

import com.exchange.portal.exchangeportal.common.vo.page.OffsetPageVO;
import com.exchange.portal.exchangeportal.common.vo.page.PageRootVO;
import com.github.pagehelper.Page;

import java.util.Objects;


public class PageUtils {

    public static <T> PageRootVO<T, OffsetPageVO> getPageDataByPage(Integer pageNum, Integer pageSize, Page<T> dataList) {
        OffsetPageVO pageMeta = null;
        if (Objects.nonNull(pageNum) && Objects.nonNull(pageSize)) {
            Long count = dataList.getTotal();
            pageMeta = getOffsetPage(pageNum, pageSize, dataList.getTotal());
        } else {
            pageMeta = OffsetPageVO.builder().hasNextPage(false).hasPreviousPage(false).totalCount(dataList.getTotal()).totalPage(1).build();
        }

        PageRootVO pageRootVO = new PageRootVO();
        pageRootVO.setData(dataList);
        pageRootVO.setPaging(pageMeta);
        return pageRootVO;
    }

    public static OffsetPageVO getOffsetPage(int pageNum, int pageSize, long totalCount) {
        OffsetPageVO offsetPageVO = new OffsetPageVO();

        if (pageSize == 0) {
            offsetPageVO.setTotalPage(1);
        } else {
            offsetPageVO.setTotalPage((int) (totalCount / pageSize) + (totalCount % pageSize > 0 ? 1 : 0));
        }
        offsetPageVO.setTotalCount(totalCount);
        offsetPageVO.setTotalPage(pageNum);

        offsetPageVO.setHasPreviousPage(1 < pageNum && pageNum <= offsetPageVO.getTotalPage());
        offsetPageVO.setHasNextPage(pageNum < offsetPageVO.getTotalPage());
        return offsetPageVO;
    }
}
