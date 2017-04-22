 //@ sourceURL=nodes.js
+function(baseurl){
	
	// 初始函数
	+function init(){
		$.JsonRPC('nodes').done(function(result){
			var data=[];
			for(var i=0;i<result.length;i++){
				data.push([i+1,result[i].hostname,result[i].ip,result[i].port,result[i].status]);
			}
			var columns=[
				          {"title":"序号"},
				          {"title":"主机名称"},
				          {"title":"主机地址"},
				          {"title":"通讯端口"},
				          {"title":"运行状态"}];
			$('.box-body').table(data,columns);
		});
	}();
}(baseurl);
