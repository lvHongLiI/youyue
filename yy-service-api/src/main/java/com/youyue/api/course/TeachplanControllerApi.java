package com.youyue.api.course;

import com.youyue.framework.domain.course.Teachplan;
import com.youyue.framework.domain.course.ext.TeachplanNode;
import com.youyue.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="教学计划管理接口",description="教学计划，提供页面的增、删、改、查")
public interface TeachplanControllerApi {

    @ApiOperation("查询页面")
    TeachplanNode findList(String courseId);


   ResponseResult addTeachplan(Teachplan teachplan);
}
