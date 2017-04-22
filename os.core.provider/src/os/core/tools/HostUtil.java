package os.core.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 主机信息Model
 * @author 尹行欣
 *
 */
public class HostUtil{
	
	public static String hostname(){
		// 从环境变量中读取主机名
		String host=System.getenv().get("OS_HOST");
		
		// 从启动参数中读取主机名
		if(isEmpty(host)){
			host=System.getProperty("os.host");
		}
		if(!isEmpty(host)){
			return host;
		}
		// 通过系统接口读取主机名
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName=inetAddress.getHostName();
	        return hostName;
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}
	public static String address(){
		
		// 从环境变量中读取IP地址
		String addr=System.getenv().get("OS_ADDR");
		
		// 从启动参数中读取IP地址
		if(isEmpty(addr)){
			addr=System.getProperty("os.addr");
		}
		if(!isEmpty(addr)){
			return addr;
		}
		
		// 通过系统接口读取IP地址
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostAddress=inetAddress.getHostAddress();
	        return hostAddress;
		} catch (UnknownHostException e) {
			return "localhost";
		}
		
	}
	private static boolean isEmpty(String str){
		return str==null||str.equals("");
	}
}
