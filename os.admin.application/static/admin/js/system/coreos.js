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
			$('#nodes-container .node[data-type="normal"]').remove();
			$('#coreos_info').tmpl(bundles_translate(nodes)).prependTo($('#nodes-container'));
			coreos_bind();
		});
	}
	
	
	// 执行系统命令
	function execute(action,param){
		param=param||{};
		param.method=action;
		// 系统信息
		$.adminRPC('admin/execute',param).done(function(nodes){
			$('#nodes-container .node[data-type="normal"]').remove();
			$('#coreos_info').tmpl(bundles_translate(nodes)).prependTo($('#nodes-container'));
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
	function repertory_install(bundle,num){
		var action='install';
		var param={location:bundle,start:true,num:num};
		console.info('%s->%s->%s',action,bundle,num);
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
	function repertory_cmd(action,bundle,num){
		var action=action;
		var param={bundle:bundle};
		if(action=='update'){
			param.timer=num;
		}else if(num){
			param.num=num;
		}
		console.info('%s->%s',action,bundle);
		execute(action,param);
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
		
		$('.bundles-container li').smartMenu(bundle_menu,{name:'bundle'});
	}

	// 一键安装
	$('#one_install').on('click',function(){
		var len=$('#bundle-chooser').find(' .node').length;
		var num=len%3+1;
		var param={};
		param['os.health']=num;
		param['os.moudel.user']=num;
		param['os.moudel.person']=num;
		param['os.moudel.guard']=num;
		param['os.moudel.log']=num;
		param['os.moudel.db']=num;
		
		$('#editForm').form(param);
		$('#lay_pop').pop({title:'一键部署',height:'300px',width:'490px'});
	});
	//###########
	// 右键菜单
	//###########
	// 仓库菜单
	var repertory_menu=[
			[{
		        text:'安装',
		        func:function()
		        {
		        	var location=$(this).data('location');
		        	layer.prompt({
						formType:2,
						title:'实例数目',
						value:'1',
						area: ['200px', '30px']
					}, 
					function(value,index){
						layer.close(index);
						repertory_install(location,value);
					});
		        	
		        }
		    },{
		        text:'扩容',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		        	layer.prompt({
						formType:2,
						title:'实例数目',
						value:'1',
						area: ['200px', '30px']
					}, 
					function(value,index){
						layer.close(index);
						repertory_cmd('change',bundle,value);
					});
		        	
		        }
		    },{
		        text:'启动',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('start',bundle);
		        }
		    },{
		        text:'升级',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            var bundle=$(this).data('bundle');
		        	layer.prompt({
						formType:2,
						title:'时间间隔',
						value:'10',
						area: ['200px', '30px']
					}, 
					function(value,index){
						layer.close(index);
						repertory_cmd('update',bundle,value);
					});
		        }
		    },{
		        text:'重启',
		        func:function()
		        {
		        	var bundle=$(this).data('bundle');
		            repertory_cmd('restart',bundle);
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
			        text:'重启',
			        func:function()
			        {
			        	var bundle=$(this).data('bundle');
			            var nodes=$(this).parents('ul').data('node');
			        	bundle_cmd('restart',nodes,bundle);
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
			        text:'升级',
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
	
	
	// 添加和更新表单提交
	$('#btn_submit').on('click',function(){
		// 检查
		if(!$('#editForm').check()){
			return;	
		}
		// 提交
		var param=$('#editForm').form();
		// 系统信息
		$.adminRPC('admin/oneInstall',param).done(function(nodes){
			$('#nodes-container .node[data-type="normal"]').remove();
			$('#coreos_info').tmpl(bundles_translate(nodes)).prependTo($('#nodes-container'));
			coreos_bind();
			$('#lay_pop').close();
		});
	});
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
		nodes.sort(function(obj1,obj2){
			return (obj1.ip+':'+obj1.port).localeCompare(obj2.ip+':'+obj2.port);
		});
		for(var i=0;i<nodes.length;i++){
			
			var filter=[];
			for(var j=0;j<nodes[i].bundles.length;j++){
				var status=nodes[i].bundles[j].status;
				if(status!='1'){
					filter.push(translate(nodes[i].bundles[j]));	
				}
				
			}
			nodes[i].type='normal';
			nodes[i].color='blue';
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
