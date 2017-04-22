package os.admin.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import os.core.api.CoreOS;
import os.core.tools.StringUtil;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;
@RequireWebServerExtender
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class BaseCtrl {
	
	// ��־��¼��
	String LOG_CLASS="os.moudel.log.provider.LogDB";
	
	// �����ʼ���ص�����
	public void init(Map param){
		Object user=this.getSession().getAttribute("user");
		if(user!=null){
			Map map=(Map)user;
			String id=map.get("id").toString();
			String role=map.get("role").toString();
			param.put("userid",id);
			if(role.equals("admin")){
				param.put("cascade","yes");
				// ����Աӵ��ȫ����ѯȨ��
				String method=param.get("invoke_method").toString();
				if(method.equals("query")){
					param.remove("userid");
				}
			}
		}
		
	}
	// �������
	protected HttpServletRequest getRequest(){
		return BaseFilter.local.get();
	}
	// Session����
	protected HttpSession getSession(){
		return BaseFilter.local.get().getSession();
	}
	// �ͻ��˶�IP
	protected String getClientIP(){
		HttpServletRequest request=this.getRequest();
		String ip=request.getHeader("x-forwarded-for");  
        if(StringUtil.isEmpty(ip)||"unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if(StringUtil.isEmpty(ip)||"unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if(StringUtil.isEmpty(ip)||"unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if(StringUtil.isEmpty(ip)||"unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if(StringUtil.isEmpty(ip)||"unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
	}
	// ��¼��־
	protected void log(String level,String info){
		HttpSession session=this.getSession();
		Object user=session.getAttribute("user");
		String username="0:-";
		if(user!=null&&(user instanceof Map)){
			Map account=(Map)user;
			String id=account.get("id").toString();
			username=account.get("username").toString();
			username=id+":"+username;
		}
		try{
			this.getCoreOS().call(LOG_CLASS,"info",level,username,getClientIP(),info);
		}catch(Exception e){};
	}
	public abstract CoreOS getCoreOS();
}
