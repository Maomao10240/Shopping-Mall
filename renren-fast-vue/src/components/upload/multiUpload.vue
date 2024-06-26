<template>
  <div>
    <el-upload
      action="#"
      :http-request="uploadFile"
      :data="dataObj"
      list-type="picture-card"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-remove="handleRemove"
      :on-success="handleUploadSuccess"
      :on-preview="handlePreview"
      :limit="maxCount"
      :on-exceed="handleExceed"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
  </div>
</template>
<script>
import { policy } from "./policy";
import { getUUID } from "@/utils";
import axios from "axios";

export default {
  name: "multiUpload",
  props: {
    //图片属性数组
    value: Array,
    //最大上传图片数量
    maxCount: {
      type: Number,
      default: 30,
    },
  },
  data() {
    return {
      dataObj: {
        policy: "",
        signature: "",
        key: "",
        awsAccessKeyId: "",
        dir: "",
        host: "",
        callback: "",
      },
      dialogVisible: false,
      dialogImageUrl: null,
    };
  },
  computed: {
    fileList() {
      let fileList = [];
      for (let i = 0; i < this.value.length; i++) {
        fileList.push({ url: this.value[i] });
      }

      return fileList;
    },
  },
  mounted() {},
  methods: {
    emitInput(fileList) {
      let value = [];
      for (let i = 0; i < fileList.length; i++) {
        value.push(fileList[i].url);
      }
      this.$emit("input", value);
    },
    handleRemove(file, fileList) {
      this.emitInput(fileList);
    },
    handlePreview(file) {
      this.dialogVisible = true;
      this.dialogImageUrl = file.url;
    },
    beforeUpload(file) {
      let _self = this;
      return new Promise((resolve, reject) => {
        policy()
          .then((response) => {
            console.log("response data ", response);
            _self.dataObj.policy = response.data.policy;
            _self.dataObj.signature = response.data.signature;
            _self.dataObj.awsAccessKeyId = response.data.accessId;
            _self.dataObj.key = response.data.key;
            _self.dataObj.dir = response.data.dir;
            _self.dataObj.host = response.data.host;
            console.log("response data ....", _self.dataObj);
            resolve(true);
          })
          .catch((err) => {
            console.log("出错了...", err);
            reject(false);
          });
      });
    },
    handleUploadSuccess(res, file) {
      this.fileList.push({
        name: file.name,
        // url: this.dataObj.host + "/" + this.dataObj.dir + "/" + file.name； 替换${filename}为真正的文件名
        url:
          this.dataObj.host +
          "/" +
          this.dataObj.key.replace("${filename}", file.name),
      });
      this.emitInput(this.fileList);
    },
    handleExceed(files, fileList) {
      this.$message({
        message: "最多只能上传" + this.maxCount + "张图片",
        type: "warning",
        duration: 1000,
      });
    },
    uploadFile(file) {
      //console.log("ffff", file);
      let param = new FormData();
      param.append("file", file.file);
      //console.log(param.get('file'));
      let config = {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      };
      return axios
        .put(this.dataObj.signature, file.file, config)
        .then((response) => {
          console.log("File uploaded successfully:", response);
        })
        .catch((error) => {
          console.error("Error uploading file:", error);
        });
    },
  },
};
</script>
<style>
</style>


