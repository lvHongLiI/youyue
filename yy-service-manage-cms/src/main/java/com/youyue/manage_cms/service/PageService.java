package com.youyue.manage_cms.service;

import com.youyue.framework.domain.cms.CmsPage;
import com.youyue.framework.domain.cms.request.QueryPageRequest;
import com.youyue.framework.model.response.CommonCode;
import com.youyue.framework.model.response.QueryResponseResult;
import com.youyue.framework.model.response.QueryResult;
import com.youyue.manage_cms.dao.CmsPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPage) {
        if (page<1){
            page=1;
        }
        if (size<1){
            size=10;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CmsPage> pages = cmsPageRepository.findAll(pageable);
        QueryResult queryResult=new QueryResult();
        queryResult.setList(pages.getContent());//数据列表
        queryResult.setTotal(pages.getTotalElements());//数据的总记录数
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

}
