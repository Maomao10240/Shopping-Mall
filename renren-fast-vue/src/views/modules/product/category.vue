<template>
  <div>
    <el-tree
      :data="data"
      :props="defaultProps"
      :expand-on-click-node="false"
      show-checkbox
      node-key="catId"
      :default-expanded-keys="expandedKey"
      draggable
      :allow-drop="allowDrop"
      ><span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="() => append(data)"
          >
            Append
          </el-button>
          <el-button type="text" size="mini" @click="() => edit(data)">
            Edit
          </el-button>
          <el-button
            v-if="node.childNodes.length == 0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >
            Delete
          </el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="category">
        <el-form-item label="Category name" :label-width="formLabelWidth">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Icon" :label-width="formLabelWidth">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Category unit" :label-width="formLabelWidth">
          <el-input
            v-model="category.productUnit"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitData">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  // data:[],
  // defaultProps:{
  // children: "children",
  // label:"name"
  data() {
    return {
      maxLevel: 0,
      title: "",
      dialogType: "",
      category: {
        name: "",
        parentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        productUnit: "",
        icon: "",
        catId: null,
      },
      dialogVisible: false,
      data: [], // Initialize as an empty array
      expandedKey: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  //计算属性，类似于data概念
  computed: {},
  //监控data中数据变化
  //watch:{},
  //Collections of all functions
  methods: {
    submitData() {
      if (this.dialogType == "add") {
        this.addCategory();
      }
      if (this.dialogType == "edit") {
        this.editCategory();
      }
    },
    editCategory() {
      var { catId, name, icon, productUnit } = this.category;
      this.$http({
        url: this.$http.adornUrl("/product/category/update"),
        method: "post",
        data: this.$http.adornData({ catId, name, icon, productUnit }, false),
      }).then(({ data }) => {
        this.$message({
          type: "success",
          message: "successfully edited",
        });
        //refresh the menu
        this.dialogVisible = false;
        this.getMenus();
        //expand the deleted menu
        this.expandedKey = [this.category.parentCid];
      });
    },
    addCategory() {
      console.log("add new category", this.category);
      this.$http({
        url: this.$http.adornUrl("/product/category/save"),
        method: "post",
        data: this.$http.adornData(this.category, false),
      }).then(({ data }) => {
        this.$message({
          type: "success",
          message: "successfully added",
        });
        //refresh the menu
        this.dialogVisible = false;
        this.getMenus();
        //expand the deleted menu
        this.expandedKey = [this.category.parentCid];
      });
    },
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get",
      }).then(({ data }) => {
        console.log("succeeddd ", data.data);
        this.data = data.data;
      });
    },
    append(data) {
      this.dialogType = "add";
      this.title = "append";
      console.log("data", data);
      this.dialogVisible = true;
      this.category.parentCid = data.catId;
      this.category.catLevel = data.catLevel * 1 + 1;
      this.category.catId = null;
      this.category.name = "";
      this.category.icon = "";
      this.category.productUnit = "";
      this.category.sort = 0;
      this.category.showStatus = 1;
    },
    edit(data) {
      this.dialogType = "edit";
      this.title = "edit";
      console.log("update ", data);
      this.dialogVisible = true;
      console.log("add new category", this.category);
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: "post",
      }).then(({ data }) => {
        console.log("data shown", data);
        this.category.name = data.data.name;
        this.category.catId = data.data.catId;
        this.category.icon = data.data.icon;
        this.category.productUnit = data.data.productUnit;
        this.category.parentCid = data.data.parentCid;
      });
    },
    remove(node, data) {
      //   console.log("remove", node, data);
      var ids = [data.catId];
      this.$confirm(
        `This will permanently delete the ${data.name}. Continue?`,
        "Warning",
        {
          confirmButtonText: "OK",
          cancelButtonText: "Cancel",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/product/category/delete"),
            method: "post",
            data: this.$http.adornData(ids, false),
          }).then(({ data }) => {
            // console.log("succeedly deleted ");
            this.$message({
              type: "success",
              message: "successfully deleted",
            });
            //refresh the menu
            this.getMenus();
            //expand the deleted menu
            this.expandedKey = [node.parent.data.catId];
          });
        })
        .catch(() => {});
    },
    allowDrop(draggingNode, dropNode, type) {
      console.log("allowDrop ", draggingNode, dropNode, type);
      //被拖动的当前节点+所在的父节点不能大于三
      this.countNodeLevel(draggingNode.data);
      //当前正在拖动的节点 + 父节点所在深度不大于3
      console.log("max: ", this.maxLevel);

      let deep = this.maxLevel - draggingNode.data.catLevel + 1;
     
      console.log("shendu: ", deep);
      if (type == "inner") {
        return deep + dropNode.level <= 3;
      } else {
        return deep + dropNode.parent.level <= 3;
      }
      return true;
    },
    countNodeLevel(node) {
      //找出所有子节点， 求最大深度
      if (node.children != null && node.children.length > 0) {
        for (let i = 0; i < node.children.length; i++) {
          if (node.children[i].catLevel > this.maxLevel) {
            this.maxLevel = node.children[i].catLevel;
          }
          this.countNodeLevel(node.children[i]);
        }
      }
    },
  },
  beforeCreate() {},

  //create the cycle
  created() {
    this.getMenus();
  },
  beforeMount() {},
  //load
  mounted() {},
};
</script>

<style>
</style>