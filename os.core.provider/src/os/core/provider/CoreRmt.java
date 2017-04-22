package os.core.provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import os.core.api.CoreOS;
import os.core.rmt.TelnetServer;

/**
 * 远程管理接口
 */
@Component(name="os.rmt",service=CoreRmt.class,immediate=true)
public class CoreRmt {
	
	CoreOS coreos=null;
	TelnetServer server=null;
	
	@Reference void setCoreOS(CoreOS coreos){
		this.coreos=coreos;
	}
	
	@Activate void start() {
		String port=System.getProperty("org.osgi.service.http.port","8080");
		int http_port=Integer.parseInt(port);
		int telnet_port=0;
		if(http_port<=1000){
			telnet_port=http_port+1000;
		}else{
			telnet_port=http_port-1000;
		}
		this.server=new TelnetServer(telnet_port,coreos);
		this.server.start();
	}

	@Deactivate void close() {
		try {
			this.server.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
