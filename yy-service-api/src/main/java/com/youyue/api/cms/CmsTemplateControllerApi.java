package com.youyue.api.cms;

import com.youyue.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 页面查询
 */
@Api(value="cms页面管理接口",description="cms页面管理接口，提供页面的增、删、改、查")
public interface CmsTemplateControllerApi {
   @ApiOperation("模板查询页面列表")
   QueryResponseResult findList();
}
