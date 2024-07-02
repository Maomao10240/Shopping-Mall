package com.maohua.search.controller;

import com.maohua.search.service.MallSearchService;
import com.maohua.search.vo.SearchParam;
import com.maohua.search.vo.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {
    @Autowired
    MallSearchService mallService;

    //spring mvc 自动降提交过来的所有参数封装成指定的对象

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){
        SearchResponse result = mallService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
