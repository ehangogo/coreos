package os.core.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

import os.core.conf.Config;

/**
 * ������ϢModel
 * @author ������
 *
 */
public class HostUtil{
	
	public static String hostname(){
		
		
		// ��֧���ļ��ж�ȡ����
		String host=Config.get(Config.HOST_NAME);
		if(!isEmpty(host)){
			return host;
		}
		// ͨ��ϵͳ�ӿڶ�ȡ������
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			host=inetAddress.getHostName();
	        return host;
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}
	public static String address(){
		
		// ��֧���ļ��ж�ȡ����
		String address=Config.get(Config.HOST_IP);
		if(!isEmpty(address)){
			return address;
		}
		
		// ͨ��ϵͳ�ӿڶ�ȡIP��ַ
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			address=inetAddress.getHostAddress();
	        return address;
		} catch (UnknownHostException e) {
			return "localhost";
		}
		
	}
	public static String port(){
		String port=Config.get(Config.HOST_PORT);
		if(!isEmpty(port)){
			return port;
		}
		port=System.getProperty("org.osgi.service.http.port","8080");
		return port;
	}
	// Socket����˿�
	public static String socket_port(){
		String port=Config.get(Config.HOST_PORT,"8080");
		int http_port=Integer.parseInt(port);
		int socket_port=0;
		if(http_port<=1000){
			socket_port=http_port+1000;
		}else{
			socket_port=http_port-1000;
		}
		return socket_port+"";
	}
	private static boolean isEmpty(String str){
		return str==null||str.equals("");
	}
}
