package os.admin.job;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.admin.application.CmdApp;

@Component
public class CheckJob{

	// �쳣�ָ�
	public static Map<String,Long> checktab=new HashMap<>();
		
	private boolean finish=true;
	CmdApp cmdApp=null;
	@Reference void setCmdApp(CmdApp cmdApp){
		this.cmdApp=cmdApp;
	}
	@Activate void start() {
		new Thread(new Runnable(){
			public void run(){
				while(true){
					// ˯15s
					try{
						Thread.sleep(15000);
					}catch(Exception e){}
					if(finish==true){
						finish=false;
						cmdApp.check();
						finish=true;
					}
				}
			}
		}).start();
	}
}
