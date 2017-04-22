package os.admin.base;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import os.core.tools.ReflectUtil;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.jsonrpc.dto.JSON.JSONRPCError;
import osgi.enroute.jsonrpc.dto.JSON.Response;
import aQute.lib.json.JSONCodec;

/**
 * JSONRCP�����
 * �������󲢽����ת��controller
 * 
 * @author ������
 */
@Component(
		service=Servlet.class,
		name="osgi.web.jsonrpc",
		property={
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/coreos/*" // ����·��
		})
public class BaseServlet extends HttpServlet {
	
	static final long serialVersionUID = 1L;
	
	// ��־��
	@Reference
	LogService log;
		
	// JSONת��
	static JSONCodec codec=new JSONCodec();

	// ����������
	ConcurrentHashMap<String, JSONRPC> controllers = new ConcurrentHashMap<>();
	
	// ���������
	public void service(HttpServletRequest rq, HttpServletResponse rsp) throws IOException {
		
		// ��ȡ����·��
		String path = rq.getPathInfo();
		if (path == null) {
			rsp.getWriter().println("Missing Controller name in " + rq.getRequestURI());
			rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		path=path.replaceAll("^/|/$","").replace(".json","");
		
		try {
			// ͨ������URL��λ��ͷ���
			String clz=path.split("/")[0];
			String method=path.split("/")[1];
			try {
				log.log(LogService.LOG_INFO,"Request " + rq);
				Object ctrl=controllers.get(clz);
				if (ctrl==null) {
					rsp.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				// �������
				Map<String,Object> params=new HashMap<>();
				rq.getParameterMap().forEach((key,value)->{
					if(value.length==1){
						params.put(key,value[0]);
					}else if(value.length>1){
						params.put(key,value);
					}else{
						params.put(key,null);
					}
				});
				
				// ��ʼ������
				params.put("invoke_method",method);
				ReflectUtil.invoke(ctrl,"init",params);
				
				// ִ��Ŀ�귽��
				Response result = execute(ctrl,method,params);
				
				log.log(LogService.LOG_INFO,"Result " + result);

				// ���ؽ��
				OutputStream out = rsp.getOutputStream();
				if(result!=null){
					rsp.setContentType("application/json;charset=UTF-8");
					codec.enc().writeDefaults().to(out).put(result);
				}

				out.close();
			} catch (Exception e) {
				rsp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} catch (Exception e) {
			rsp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

	public Response execute(Object target,String method,Map<String,Object> params) throws Exception {
		Response response = new Response();
		try {
			Method m=ReflectUtil.search(target.getClass(),method, params);
			if(m!=null){
				response.result=ReflectUtil.invoke(target,m,params);
				return response;
			}else{
				response.error = new JSONRPCError();
				response.error.message = "No such method " + method;
				return response;
			}
		} catch (Exception e) {
			log.log(LogService.LOG_ERROR, "JSONRPC exec error on " + params.get("request").toString(), e);
			response.error = new JSONRPCError();
			response.error.message = e.getMessage();
			return response;
		}
	}
	
	// ���Controller�仯
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC)
	public synchronized void addEndpoint(osgi.enroute.jsonrpc.api.JSONRPC resourceManager, Map<String, Object> map) {
		String name = (String) map.get(JSONRPC.ENDPOINT);
		controllers.put(name, resourceManager);
	}
	public synchronized void removeEndpoint(JSONRPC resourceManager, Map<String, Object> map) {
		String name = (String) map.get(JSONRPC.ENDPOINT);
		controllers.remove(name);
	}
}
