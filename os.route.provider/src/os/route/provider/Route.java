package os.route.provider;


import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * 路由模块
 */
@Component(name = "os.route")
public class Route extends ZooKeeperServerMain {
	
	private Thread thread;
	private ServerConfig config;

	// 初始匿
	@Activate
	void activate(BundleContext context) {
		
		// 创建路由相关配置
		System.out.println("配置路由...");
		config = new ServerConfig();
		config.parse(new String[]{"6789","D:/route"});
		
		// 启动路由
		System.out.println("启动路由...");
		thread = new Thread(this::config, "os.route");
		thread.start();
		
	}
	
	// 注销
	@Deactivate
	void deactivate() {
		shutdown();
		thread.interrupt();
		System.out.println("路由关停...");
	}
	
	// 配置路由
	public void config() {
		try {
			runFromConfig(config);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("配置失败...");
		}
		System.out.println("路由关停...");
	}
	
}
