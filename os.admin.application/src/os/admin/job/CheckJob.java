package os.admin.job;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import os.admin.application.CmdApp;
import osgi.enroute.scheduler.api.CronJob;

@Component(property=CronJob.CRON+"=*/5 * * * * ?")
public class CheckJob implements CronJob<Object> {

	// �쳣�ָ�
	public static Map<String,Long> checktab=new HashMap<>();
		
	CmdApp cmdApp=null;
	@Reference void setCmdApp(CmdApp cmdApp){
		this.cmdApp=cmdApp;
	}
	public void run(Object object) {
		cmdApp.check();
	}
}
