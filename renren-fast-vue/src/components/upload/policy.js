import http from '@/utils/httpRequest.js'
export function policy() {
   return  new Promise((resolve,reject)=>{
        http({
            url: http.adornUrl("/thirdparty/aws/policy"),
            method: "get",
            params: http.adornParams({ extension: 'jpg' })
        }).then(({ data }) => {
            resolve(data);
        })
    });
}