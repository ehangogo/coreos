 //@ sourceURL=bundles.js
+function(baseurl){
	
	// 初始函数
	+function init(){
		$.JsonRPC('bundles').done(function(result){
			var data=[];
			for(var i=0;i<result.length;i++){
				if(result[i].name.startsWith("os.")){
					var opt="<a href='#'>停止</a>|<a href='#'>更新</a>|<a href='#'>卸载</a>"; 
					data.push([result[i].id,result[i].name,result[i].version,result[i].ip,result[i].port,result[i].status,opt]);
				}
			}
			var columns=[
				          {"title":"标识"},
				          {"title":"组件名称"},
				          {"title":"组件版本"},
				          {"title":"主机地址"},
				          {"title":"通讯端口"},
				          {"title":"运行状态"},
				          {"title":"操作"}];
			$('.box-body').table(data,columns);
		});
	}();
}(baseurl);
