<template>
  <el-tree
    :data="data"
    :props="defaultProps"
    @node-click="handleNodeClick"
  ></el-tree>
</template>

<script>
export default {
    // data:[],
    // defaultProps:{
    // children: "children",
    // label:"name"
    data() {
    return {
      data: [], // Initialize as an empty array
      defaultProps: {
        children: "children",
        label: "name"
      }
    };
 },
  //计算属性，类似于data概念
   computed:{},
  //监控data中数据变化
  //watch:{},
  //Collections of all functions
  methods:{
    handleNodeClick(data){
        console.log(data);
    },
    getMenus(){
        this.$http({
            url:this.$http.adornUrl('/product/category/list/tree'),
            method:'get',
        }).then(({data})=>{

            console.log("succeeddd ", data.data)
            this.data = data.data
        })
    }
  },
  beforeCreate(){},

  //create the cycle
  created(){
    this.getMenus();
  },
  beforeMount(){},
  //load
  mounted(){},
  
}
</script>

<style>
</style>