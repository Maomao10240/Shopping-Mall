package com.maohua.search.service.impl;

import com.maohua.search.service.MallSearchService;
import com.maohua.search.vo.SearchParam;
import com.maohua.search.vo.SearchResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class MallSearchServiceImpl implements MallSearchService {



    @Override
    public SearchResponse search(SearchParam param) {
           if(!StringUtils.isEmpty(param.getKeyword())){
               //List<>
           }
        return null;
    }
}
