package com.maohua.common.constant;

public class WareConstant {
    public enum PurchaseStatus {
        CREATED(0, "new"), ASSIGNED(1, "ASSIGNED"), RECEIVED(2, "RECEIVED"), FINISH(3, "FINISH"), HASERROR(4, "ERROR");
        private int code;
        private String msg;
        PurchaseStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailStatus {
        CREATED(0, "new"), ASSIGNED(1, "ASSIGNED"), BUYING(2, "RECEIVED"), FINISH(3, "FINISH"), HASERROR(4, "ERROR");
        private int code;
        private String msg;
        PurchaseDetailStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }
}
