package com.coamctech.bxloan.manager.dao;


import com.coamctech.bxloan.manager.domain.DocInfo;
import com.coamctech.bxloan.manager.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocInfoDao extends JpaSpecificationExecutor<DocInfo>,PagingAndSortingRepository<DocInfo,Long> {
    List<DocInfo> findFirst6ByColumnIdInOrderByUpdateTimeDesc(Collection<Long> columnIds);
    @Query(value = "select top :count t from DocInfo t order by t.ifTop desc , t.updateTime desc ")
    List<DocInfo> findByColumnIdInOrderByIfTopDescUpdateTimeDesc(Collection<Long> columnIds,Integer count);

    List<DocInfo> findFirst6ByColumnIdInOrderByIfTopDescUpdateTimeDesc(Collection<Long> columnIds);
}