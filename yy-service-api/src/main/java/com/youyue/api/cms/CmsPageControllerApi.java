package com.youyue.api.cms;

import com.youyue.framework.domain.cms.CmsPage;
import com.youyue.framework.domain.cms.request.QueryPageRequest;
import com.youyue.framework.domain.cms.response.CmsPageResult;
import com.youyue.framework.domain.cms.response.GenerateHtmlResult;
import com.youyue.framework.model.response.QueryResponseResult;
import com.youyue.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 页面查询
 */
@Api(value="cms页面管理接口",description="cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
   @ApiOperation("分页查询页面列表")
   @ApiImplicitParams({
           @ApiImplicitParam(name="page",value="页码",required=true,paramType="path",dataType="int"),
           @ApiImplicitParam(name="size",value="每页记录数",required=true,paramType="path",dataType="int") })
   QueryResponseResult findList(int page, int size, QueryPageRequest queryPage);

   @ApiOperation("新增页面")
   CmsPageResult save(CmsPage cmsPage);

   @ApiOperation("查询单个cmsPage对象")
   @ApiImplicitParam(name="pageId",value="ID",required=true,paramType="path",dataType="String")
   CmsPageResult findById(String pageId);

   @ApiOperation("删除页面")
   ResponseResult delete(String pageId);

   @ApiOperation("发布页面")
   ResponseResult releasePageHtml(String pageId);

   @ApiOperation("查询发布页面情况")
   GenerateHtmlResult findReleaseStatus(String pageId);
}
