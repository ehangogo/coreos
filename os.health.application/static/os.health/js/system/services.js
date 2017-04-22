 //@ sourceURL=services.js
+function(baseurl){
	
	// 初始函数
	+function init(){
		$.JsonRPC('services').done(function(result){
			var data=[];
			var count=1;
			for(var i=0;i<result.length;i++){
				var method="";
				for(var j=0;j<result[i].methods.length&&j<3;j++){
					method+=result[i].methods[j]+",";
				}
				method=method.replace(/,$/ig,"");
				if(result[i].name.startsWith('os.')){
					data.push([count++,result[i].name,result[i].ip,method,'RUNNING']);
				}
			}
			var columns=[
				          {"title":"标识"},
				          {"title":"服务名称"},
				          {"title":"主机地址"},
				          {"title":"接口列表"},
				          {"title":"运行状态"}];
			$('.box-body').table(data,columns);
		});
	}();
}(baseurl);
