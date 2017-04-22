 //@ sourceURL=coreos.js
+function(baseurl){
	
	// 初始化
	+function init(){
		// 查询组件仓库
		repertories();
		// 查询网络拓扑
		infos();
	}();
	
	// 请求函数
	function repertories(){
		// 组件仓库信息
		$.adminRPC('admin/repertories').done(function(bundles){
			$('#bundle-chooser').html('');
			$('#bundle_info').tmpl(repertories_translate(bundles)).appendTo($('#bundle-chooser'));
			repertory_bind();
		});
	}
	// 查询网络拓扑
	function infos(){
		// 系统信息
		$.adminRPC('admin/infos').done(function(nodes){
			$('#nodes-container').html('');
			$('#coreos_info').tmpl(bundles_translate(nodes)).appendTo($('#nodes-container'));
			coreos_bind();
		});
	}
	// 执行系统命令
	function execute(action,param){
		param=param||{};
		param.method=action;
		// 系统信息
		$.adminRPC('admin/execute',param).done(function(nodes){
			$('#nodes-container').html('');
			$('#coreos_info').tmpl(bundles_translate(nodes)).appendTo($('#nodes-container'));
			coreos_bind();
		});
	}
	
	// 拖拽：安装
	function install(to,bundle){
		
		var action='install';
		var param={addr:to,location:bundle,start:true};
		
		console.info('%s->%s->%s',action,to,bundle);
		execute(action,param);
	}
	// 拖拽：迁移
	function move(from,to,bundle,status){
		
		var action='move';
		var param={bundle:bundle,from:from,to:to};
		if(status=='32'){
			param.start=true;
		}
		console.info('%s->%s->%s->%s',action,bundle,from,to);
		execute(action,param);
		
	}
	// 单机：组件启动,停止,卸载,更新
	function bundle_cmd(action,node,bundle){
		var action=action;
		var param={addr:node,bundle:bundle};
		console.info('%s->%s->%s',action,node,bundle);
		execute(action,param);
	}
	// 全局：组件启动,停止,卸载,更新
	function repertory_cmd(action,bundle){
		var action=action;
		var param={bundle:bundle};
		console.info('%s->%s',action,bundle);
		execute(action,param);
	}
	// 内核
	function coreos_cmd(action,node){
		console.info(action);
		console.info(node);
		if(action=='cmd'){
			command(node);
		}else{
			alert('暂不支持改功能');
		}
	}
	
	//###########
	// 事件绑定
	//###########
	function repertory_bind(){
		// 仓库拖拽
	    $('#bundle-chooser').sortable({
				sort: false,
				filter:'.nodrag',
				group:{
					name: 'advanced',
					pull: 'clone',
					put: false
				},
				animation: 150
		});
		$('#bundle-chooser li').smartMenu(repertory_menu,{name:'repertory'});
	}
	function coreos_bind(){
		// 节点之间拖拽
		$('.bundles-container').sortable({
				sort: true,
				filter:'.nodrag',
				group: {
					name: 'advanced',
					pull: true,
					put: true
				},
				animation: 150,
				onAdd:function(evt){
					var clone=evt.clone;
					var from=evt.from;
					var bundle=evt.item;  
					var to=evt.to;
					
					var lis=$(to).find('li:visible');
					if(lis.length>9){
						alert('超出目标主机最大组件安装个数');
						$(bundle).remove();
						$(clone).smartMenu(repertory_menu,{name:'repertory'});
						var f=$(from).data('role')||$(from).data('node');
						if(f!='repertory'){
							$(from).append(bundle);
						}
						return;
					}
					var installnum=0;
					for(var i=0;i<lis.length;i++){
						var trgt=lis[i];
						
						var src_bdl=$(bundle).data('bundle');
						var src_name=src_bdl.split(':')[0];
						var src_version=src_bdl.split(':')[1];
						
						var tgt_bdl=$(trgt).data('bundle');
						var tgt_name=tgt_bdl.split(':')[0];
						var tgt_version=tgt_bdl.split(':')[1];
						if(src_name.indexOf(tgt_name)>-1&&src_version==tgt_version){
							installnum++;
						}
					}
					if(installnum>=2){
						alert('目标主机已安装改组件');
						$(bundle).remove();
						$(clone).smartMenu(repertory_menu,{name:'repertory'});
						return;
					}
					$(clone).smartMenu(repertory_menu,{name:'repertory'});
					$(bundle).smartMenu(bundle_menu,{name:'bundle'});
					
					var from=$(from).data('role')||$(from).data('node');
					var location=$(bundle).data('location');
					var status=$(bundle).data('status');
					var bundle=$(bundle).data('bundle');
					var to=$(to).data('node');
					if(from=='repertory'){
						install(to,location);
					}else{
						move(from,to,bundle,status);
					}
					
				}
		});
		
		// 双击打开命令行接口
		$('.node').on('dblclick', function(){
			var node=$(this).find('.coreos').data('node');
			coreos_cmd('cmd',node);
		});
		
		$('.bundles-container li').smartMenu(bundle_menu,{name:'bundle'});
		$('.coreos').smartMenu(coreos_menu,{name:'coreos'});
	}

	//###########
	// 邮件菜单
	//###########
	// 仓库菜单
	var repertory_menu=[
			[{
		        text:'安装',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('install',bundle);
		        }
		    },{
		        text:'启动',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('start',bundle);
		        }
		    },{
		        text:'更新',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('update',bundle);
		        }
		    },{
		        text:'停止',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('stop',bundle);
		        }
		    },{
		        text:'卸载',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('uninstall',bundle);
		        }
		    }]
	];
	// 组件菜单
	var bundle_menu=[
			    [{
			        text:'启动',
			        func:function()
			        {
			            var bundle=$(this).data('bundle');
			            var nodes=$(this).parents('ul').data('node');
			            bundle_cmd('start',nodes,bundle);
			        }
			    },{
			        text:'停止',
			        func:function()
			        {
			        	var bundle=$(this).data('bundle');
			            var nodes=$(this).parents('ul').data('node');
			        	bundle_cmd('stop',nodes,bundle);
			        }
			    },{
			        text:'更新',
			        func:function()
			        {
			        	var bundle=$(this).data('bundle');
			            var nodes=$(this).parents('ul').data('node');
			        	bundle_cmd('update',nodes,bundle);
			        }
			    },{
			        text:'卸载',
			        func:function()
			        {
			        	var bundle=$(this).data('bundle');
			            var nodes=$(this).parents('ul').data('node');
			        	bundle_cmd('uninstall',nodes,bundle);
			        }
			    }]
    ];
	// 内核菜单
	var coreos_menu=[
			    [{
			        text:'启动',
			        func:function()
			        {
			            var node=$(this).data('node');
			            coreos_cmd('start',node);
			        }
			    },{
			        text:'终止',
			        func:function()
			        {
			        	var node=$(this).data('node');
			            coreos_cmd('stop',node);
			        }
			    },{
			        text:'重启',
			        func:function()
			        {
			        	var node=$(this).data('node');
			            coreos_cmd('restart',node);
			        }
			    },{
			        text:'命令行',
			        func:function()
			        {
			        	var node=$(this).data('node');
			            coreos_cmd('cmd',node);
			        }
			    }]
    ];
	
	function command(node){
		
		// 主机端口号减去1000为telnet端口
		var ip=node.split(':')[0];
		var port=node.split(':')[1];
		port=port-1000;
		node=ip+':'+port;
		
		layer.open({
				  type: 2,
				  shade: 0,
				  zIndex: layer.zIndex,
				  title: '命令行窗口',
				  maxmin: true,
				  shadeClose: true,
				  area : ['735px' , '500px'],
				  content: 'pages/system/telnet.html',
				  success: function(layero, index){
					    var body = layer.getChildFrame('body', index);
					    var iframeWin = window[layero.find('iframe')[0]['name']];
					    if(node){
					    	iframeWin.telnet(node);
					    }
				 }
			  });
	}
	
	//###########
	// 私有函数
	//###########
	// 排序
	var order={
		'os.core':1,
		'os.network':2,
		'os.route':3,
		'os.admin':4,
		'os.moudel.db':5,
		'os.moudel.log':6,
		'os.moudel.person':7,
		'os.moudel.guard':8,
		'os.moudel.user':9,
		'os.health':10,
		'os.other':11
	}
	function repertories_translate(bundles){
		for(var i=0;i<bundles.length;i++){
			translate(bundles[i]);
		}
		bundles.sort(function(obj1,obj2){
			var order1=0;
			var order2=0;
			for(var key in order){
				if(obj1.name.indexOf(key)>-1){
					order1=order[key];
				}
				if(obj2.name.indexOf(key)>-1){
					order2=order[key];
				}
			}
			return order1-order2;
		});
		return {bundles:bundles}
	}
	function bundles_translate(nodes){
		for(var i=0;i<nodes.length;i++){
			
			var filter=[];
			for(var j=0;j<nodes[i].bundles.length;j++){
				var status=nodes[i].bundles[j].status;
				if(status!='1'){
					filter.push(translate(nodes[i].bundles[j]));	
				}
				
			}
			nodes[i].bundles=filter;
			nodes[i].bundles.sort(function(obj1,obj2){
				return order[obj1.name]-order[obj2.name];
			});
		}
		return {nodes:nodes}
	}
	function translate(bundle){
		// 核心组件
		if(bundle.name.indexOf('os.core')>-1){
			bundle.icon='coreos';
			bundle.color='blue';
			bundle.text='系统内核';
			bundle.hidden=true;
		}
		else if(bundle.name.indexOf('os.network')>-1){
			bundle.icon='network';
			bundle.color='blue';
			bundle.text='调度组件';
		}
		else if(bundle.name.indexOf('os.route')>-1){
			bundle.icon='route';
			bundle.color='blue';
			bundle.text='注册组件';
		}
		else if(bundle.name.indexOf('os.admin')>-1){
			bundle.icon='admin';
			bundle.color='blue';
			bundle.text='管理组件';
		}
		
		// 基础组件
		else if(bundle.name.indexOf('os.moudel.db')>-1){
			bundle.icon='db';
			bundle.color='orange';
			bundle.text='数据组件';
		}
		else if(bundle.name.indexOf('os.moudel.log')>-1){
			bundle.icon='log';
			bundle.color='orange';
			bundle.text='日志组件';
		}
		
		// 业务组件
		else if(bundle.name.indexOf('os.moudel.person')>-1){
			bundle.icon='person';
			bundle.color='orange';
			bundle.text='个人体征';
		}
		else if(bundle.name.indexOf('os.moudel.guard')>-1){
			bundle.icon='guard';
			bundle.color='orange';
			bundle.text='监控组件';
		}
		else if(bundle.name.indexOf('os.moudel.user')>-1){
			bundle.icon='user';
			bundle.color='orange';
			bundle.text='用户管理';
		}
		
		// 应用
		else if(bundle.name.indexOf('os.health')>-1){
			bundle.icon='health';
			bundle.color='green';
			bundle.text='光彩监护';
		}else{
			bundle.icon='other';
			bundle.color='purple';
			bundle.text='其他组件';
		}
		
		if(bundle.status=='2'||bundle.status=='4'){
			bundle.color='gray';
		}
		
		return bundle;
	}
    
}(baseurl);