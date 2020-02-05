package com.youyue.framework.domain.cms.request;

import lombok.Data;

@Data
public class QueryPageRequest {
    private String siteId;
    private String templateId;
    private String pageAliase;
}
