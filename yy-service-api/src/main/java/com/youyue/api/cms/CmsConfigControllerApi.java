package com.youyue.api.cms;

import com.youyue.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;

@Api(value="cms页面管理接口",description="cms页面管理接口，提供页面的增、删、改、查")
public interface CmsConfigControllerApi {
    public CmsConfig getModel(String id);
}
