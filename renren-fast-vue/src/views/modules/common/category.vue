<template>
  <el-tree :data="data" :props="defaultProps" node-key="catId" ref="menuTree" @node-click="nodeclick">
  </el-tree>
</template>

<script>
export default {
  data() {
    return {
      data: [], // Initialize as an empty array
      expandedKey: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  methods: {
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get",
      }).then(({ data }) => {
        console.log("succeeddd ", data.data);
        this.data = data.data;
      });
    },
    nodeclick(data, node, component){
        console.log("child of the category has been clicked",data, node, component)
        //send events
        this.$emit("tree-node-click", data, node, component)
    }
  },
  created() {
    this.getMenus();
  },
};
</script>

<style>
</style>