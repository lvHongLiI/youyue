package com.youyue.manage_course.dao;

import com.youyue.framework.domain.course.CourseBase;
import com.youyue.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator.
 */
@Mapper
public interface TeachplanMapper {

   TeachplanNode selectList(@Param("courseId") String courseId);
}
