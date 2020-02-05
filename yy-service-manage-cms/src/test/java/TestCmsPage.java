import com.youyue.framework.domain.cms.CmsPage;
import com.youyue.manage_cms.ManageCmsApplication;
import com.youyue.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = ManageCmsApplication.class)
@RunWith(SpringRunner.class)
public class TestCmsPage {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> list = cmsPageRepository.findAll();
        for (CmsPage page : list) {
            System.out.println(page);
        }
    }


    @Test
    public void testFindPage(){
        int page=0;
        int size=10;
        PageRequest request = PageRequest.of(page, size);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(request);
        List<CmsPage> list = cmsPages.getContent();
        for (CmsPage p : list) {
            System.out.println(p);
        }
    }



    @Test
    public void testUpdate(){
        //先查询 再修改
        Optional<CmsPage> optional = cmsPageRepository.findById("5e3a684461461b1b28433375");
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("test01");
            cmsPageRepository.save(cmsPage);
        }
    }

    @Test
    public void testDelete(){
       cmsPageRepository.deleteById("5e3a684461461b1b28433375");
    }

    @Test
    public void testSave(){
       CmsPage cmsPage=new CmsPage();
       cmsPage.setPageName("测试添加");
       cmsPageRepository.save(cmsPage);
    }

    @Test
    public void testFindName(){
        CmsPage cmsPage = cmsPageRepository.findByPageNameEquals("ccc");
        System.out.println(cmsPage+"*********************");
    }
}
