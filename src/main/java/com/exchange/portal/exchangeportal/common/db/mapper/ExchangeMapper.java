package com.exchange.portal.exchangeportal.common.db.mapper;

import com.exchange.portal.exchangeportal.common.db.po.ExchangePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.github.pagehelper.Page;
import java.util.Collection;
@Mapper
@Repository
public interface ExchangeMapper {

    void addExchange(@Param("list") Collection<ExchangePO> exchanges);


    Page<ExchangePO> listExchange(@Param("iban") String iban,
                                  @Param("pageNum") Integer pageNum,
                                  @Param("pageSize") Integer pageSize);


}
