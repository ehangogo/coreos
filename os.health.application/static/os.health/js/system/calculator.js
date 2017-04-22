 //@ sourceURL=calculator.js
+function(baseurl){
	
  	
  	//check();
    //setInterval(check,5000);
    
    // 定时检测服务是否可用
   function check(){
		$.JsonRPC('services').done(function(result){
			
			disable();
			for(var i in result){
				var name=result[i].name;
				if(name.indexOf('PlusImpl')>-1){
					$("#plus").html("+");
					$('#MPlus').html("M+");
				}
				if(name.indexOf('SubImpl')>-1){
					$("#sub").html("-");
					$('#MSub').html("M-");
				}
				if(name.indexOf('MulImpl')>-1){
					$("#mul").html("*");
				}
				if(name.indexOf('DivImpl')>-1){
					$("#div").html("\÷");
				}
				
			}
		});
		
		function disable(){
		   $("#plus").html('--');
		   $("#MPlus").html('--');
		   $("#sub").html('--');
		   $("#MSub").html('--');
		   $("#div").html('--');
		   $("#mul").html('--');
		 }
   }
   

	// 初始函数
	var res=0;	
 	var text='';
 	var END=false;
    $("button.calc-button").on('click',function(){
    	var input=$(this).text();
    	
    	// 操作符号
    	if(/^[\+|\-|\*|÷]$/.test(input)){
    		text=text.replace(/[.]$/ig,'');
    		if(/.*?[+|\-|\*|÷]/.test(text)){
    			console.error('本计算器暂只支持二则运算');
    			return;
    		}
    	}else if(/=/.test(input)){
    		var val1=0;
    		var val2=0;
    		var opt=null;
    		
    		var flag=false;
    		text.replace(/^(\d+[.]?\d*)([+|\-|\*|÷])(\d+[.]?\d*)/ig,function(match,v1,o,v2){
    			if(v1.indexOf('.')>-1){
    				val1=parseFloat(v1,10);
    			}else{
    				val1=parseInt(v1,10);
    			}
    			if(v2.indexOf('.')>-1){
    				val2=parseFloat(v2,10);
    			}else{
    				val2=parseInt(v2,10);
    			}
    			opt=o;
    			flag=true;
    		});
    		if(flag==false){return}
    		res=0;
    		
    		if(opt=='+'){
    			res=val1+val2;
    		}
    		if(opt=='-'){
    			res=val1-val2;
    		}
    		if(opt=='*'){
    			res=val1*val2;
    		}
    		if(opt=='÷'){
    			res=val1/val2;
    		} 
    		input="";
    		text="";
    		$(".calc-display-input").val(val1+opt+val2+"="+res);
    		END=true;
//    		input="";
//    		
//    		var method='';
//    		var namespace='';
//    		if(opt=='+'){
//    			namespace="os.moudel.plus.provider.PlusImpl";
//    			method="plus";
//    		}
//    		if(opt=='-'){
//    			namespace="os.moudel.sub.provider.SubImpl";
//    			method="sub";
//    		}
//			if(opt=='*'){
//				namespace="os.moudel.mul.provider.MulImpl";
//				method="mul";
//			}
//			if(opt=='÷'){
//				namespace="os.moudel.div.provider.DivImpl";
//				method="div";
//			}
//			
//
//    		$.JsonRPC('call',namespace,method,val1,val2).done(function(res){
//    			text="";
//        		$(".calc-display-input").val(res);
//        		END=true;
//    		});
    		
    		
    	// 输出结果
    	}else if(/(M\-|M\+|MC)/.test(input)){
    		END=false;
    		text=res;
    		if(input=="M-"){
    			input="-";
    		}
    		if(input=="M+"){
    			input="+";
    		}
    		if(input=="MC"){
    			text="0";
    			input="";
    			res="0";
    		}
    		
    	// 清除输入
    	}else if(/C/.test(input)){
    		if(text.length<=1){
    			text="0";
    		}else{
    			text=text.substring(0,text.length-1);
    		}
    		$(".calc-display-input").val(text);
    		return;
    	}else if(/[0-9]/.test(input)){
    		if(END){END=false;res=input;}
    		if(text.indexOf('.')<0){
    			text=text.replace(/^0+/,'');
    		}
    	}else if(/[.]/.test(input)){
    		if(END){END=false}
    		if(/[\.]$/.test(text)){
    			return;
    		}
    		if(text==""||/[+|\-|\*|÷]$/.test(text)){
    			text+="0";
    		}
    	}else{
    		return;
    	}
    	if(END==false){
	    	text+=input;
	    	$(".calc-display-input").val(text);
    	}
    	
		    });
}(baseurl);
