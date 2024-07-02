package com.maohua.search.service;

import com.maohua.search.vo.SearchParam;
import com.maohua.search.vo.SearchResponse;

public interface MallSearchService {
    SearchResponse search(SearchParam param);
}
