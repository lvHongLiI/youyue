package com.youyue.manage_cms_client.dao;

import com.youyue.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 页面查询DAO
 */

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    CmsPage findByPageNameEquals(String pageName);

    //根据页面名称 站点id 页面webpath查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String PageWebPath);
}
