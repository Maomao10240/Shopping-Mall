package com.maohua.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catelog1Id; //level 1 parent id
    private List<Catelog3Vo> catelog3List; //level 3 children
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catelog2Id;
        private String name;
        private String id;
    }

}
