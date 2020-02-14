package com.youyue.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.youyue.framework.domain.cms.CmsPage;
import com.youyue.framework.domain.cms.CmsTemplate;
import com.youyue.framework.domain.cms.request.QueryPageRequest;
import com.youyue.framework.domain.cms.response.CmsCode;
import com.youyue.framework.domain.cms.response.CmsPageResult;
import com.youyue.framework.exception.ExceptionCast;
import com.youyue.framework.model.response.CommonCode;
import com.youyue.framework.model.response.QueryResponseResult;
import com.youyue.framework.model.response.QueryResult;
import com.youyue.framework.model.response.ResponseResult;
import com.youyue.manage_cms.dao.CmsPageRepository;
import com.youyue.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPage) {
        //构建分页条件
        if (page<1){
            page=1;
        }
        if (size<1){
            size=10;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        //1.创建条件选择器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage=new CmsPage();
        if (queryPage!=null){
            setAttribute(cmsPage,queryPage);
        }
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        //3.查询数据
        Page<CmsPage> pages = cmsPageRepository.findAll(example,pageable);
        //4.封装数据
        QueryResult queryResult=new QueryResult();
        queryResult.setList(pages.getContent());//数据列表
        queryResult.setTotal(pages.getTotalElements());//数据的总记录数
        QueryResponseResult queryResponseResult=new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //将参数二中与参数一属性值相同的值设置给参数一的属性
    private <T,V>void setAttribute(T t,V v) {
        //1.获取到两个参数的class对象
        Class class1 = t.getClass();
        Class class2 = v.getClass();
        //2.获取到参数二的属性列表
        Field[] class2Fields = class2.getDeclaredFields();
        for (Field field : class2Fields) {
            try {
                //获取到当前属性的类型及属性名 以及将属性名首字母变大写好拼接
                String fieldName = field.getName();
                fieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                Class<?> fieldType = field.getType();
                //3.2取出参数二的getxxx方法  获取到结果
                Method class2Method = class2.getMethod("get"+fieldName);
                Object invoke = class2Method.invoke(v);
                if ("".equals(invoke)) {
                    continue;
                }
                //3.3取出参数一的setxxx方法
                Method class1Method = class1.getMethod("set" + fieldName, fieldType);
                //执行get和set方法将值注入给参数一
                class1Method.invoke(t,invoke);

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("注入参数时发生错误！没有找到对应的get set 方法");
            }
        }
    }

    public CmsPageResult add(CmsPage cmsPage){
        if (cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //校验页面名称 站点id
        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (page!=null){
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);
        page=cmsPageRepository.save(cmsPage);
        CmsPageResult cmsPageResult=new CmsPageResult(CommonCode.SUCCESS,page);
        return  cmsPageResult;
    }

    public CmsPageResult update(CmsPage cmsPage){
        CmsPageResult cmsPageResult=null;
        CmsPage page=null;
        try {
            page=cmsPageRepository.save(cmsPage);
            cmsPageResult=new CmsPageResult(CommonCode.SUCCESS,page);
        }catch (Exception e){
            e.printStackTrace();
            cmsPageResult=new CmsPageResult(CommonCode.FAIL,page);
        }
        return  cmsPageResult;

    }

    public CmsPageResult findById(String pageId){
        CmsPageResult cmsPageResult=null;
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            cmsPageResult=new CmsPageResult(CommonCode.SUCCESS,optional.get());
        }else {
            cmsPageResult=new CmsPageResult(CommonCode.FAIL,null);
        }
        return  cmsPageResult;
    }

    public ResponseResult delete(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            cmsPageRepository.delete(optional.get());
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return  new ResponseResult(CommonCode.FAIL);

    }


    public String getPageHtml(String pageId){
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if(model == null){
             //获取页面模型数据为空
             ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
         }
        //获取页面模板
        String template = getTemplateByPageId(pageId);
         if(StringUtils.isEmpty(template)){
             //页面模板为空
             ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
         }
        //执行静态化
        String html = generateHtml(template,model);
         if(StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
         }
        return html;
    }
    private Map getModelByPageId(String pageId){
        //1.取出页面信息
        CmsPageResult cmsPageResult = findById(pageId);
        if (cmsPageResult.getCmsPage()==null){
            ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        //2.取出页面dataUrl
        String dataUrl = cmsPageResult.getCmsPage().getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){//成立  表示dataUrl 为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //3.通过restTemplate请求dataUrl获取模板数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map map = forEntity.getBody();
        return  map;
    }

    private String getTemplateByPageId(String pageId){
        CmsPageResult cmsPageResult = findById(pageId);
        if (cmsPageResult.getCmsPage()==null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPageResult.getCmsPage().getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //取出模板文件内容
            GridFSFile gridFSFile =gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String generateHtml(String templateContent,Map<String,Object> models){
        System.out.println(templateContent+"模板内容");
        System.out.println("****************");
        System.out.println(models.get("model")+"模板数据");
         try {
             //生成配置类
             Configuration configuration = new Configuration(Configuration.getVersion());
             //模板加载器
             StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
             stringTemplateLoader.putTemplate("template",templateContent);
             //配置模板加载器
             configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
             Template template = configuration.getTemplate("template");
             Map<String,Object> model = new HashMap<>();
             model.put("model",models.get("model"));
             String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
             return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
