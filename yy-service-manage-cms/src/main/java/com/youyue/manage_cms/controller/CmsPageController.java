package com.youyue.manage_cms.controller;

import com.youyue.api.cms.CmsPageControllerApi;
import com.youyue.framework.domain.cms.CmsPage;
import com.youyue.framework.domain.cms.request.QueryPageRequest;
import com.youyue.framework.domain.cms.response.CmsPageResult;
import com.youyue.framework.domain.cms.response.GenerateHtmlResult;
import com.youyue.framework.model.response.CommonCode;
import com.youyue.framework.model.response.QueryResponseResult;
import com.youyue.framework.model.response.QueryResult;
import com.youyue.framework.model.response.ResponseResult;
import com.youyue.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
    private PageService service;
    @GetMapping("/findList/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,
                                        @PathVariable("size")int size,
                                        QueryPageRequest queryPage) {
        //调用service完成查询功能
        //暂时测试
       /* QueryResult<CmsPage> result=new QueryResult();
        List<CmsPage> list=new ArrayList();
        CmsPage cmsPage=new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage);
        result.setList(list);
        result.setTotal(1);
        QueryResponseResult responseResult=new QueryResponseResult(CommonCode.SUCCESS,result);
       */
       return service.findList(page,size,queryPage);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        CmsPageResult cmsPageResult=null;
        //判断当对象是否有id  如果有id说明是修改  没有则是添加
        if (cmsPage.getPageId()==null||"".equals(cmsPage.getPageId())){
            System.out.println("执行保存方法！");
            cmsPageResult=service.add(cmsPage);
        }else {
            System.out.println("执行修改方法！");
            cmsPageResult=service.update(cmsPage);
        }
        return cmsPageResult;
    }

    @Override
    @GetMapping("/findById")
    public CmsPageResult findById(@RequestParam("pageId") String pageId) {
        return service.findById(pageId);
    }

    @Override
    @DeleteMapping("/delete/{pageId}")
    public ResponseResult delete(@PathVariable("pageId") String pageId) {
        return service.delete(pageId);
    }

    @Override
    @GetMapping("/releasePageHtml/{pageId}")
    public ResponseResult releasePageHtml(@PathVariable("pageId") String pageId) {
        System.out.println("请求id为："+pageId);
        return service.releasePageHtml(pageId);
    }

    @Override
    @GetMapping("/findReleaseStatus/{pageId}")
    public GenerateHtmlResult findReleaseStatus(@PathVariable("pageId")String pageId){
       return service.findReleaseStatus(pageId);
    }
}
