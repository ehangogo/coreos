package os.core.model;

import org.osgi.dto.DTO;

/**
 * 主机信息Model
 * @author 尹行欣
 *
 */
public class HostInfo extends DTO{

	// 主机名
	public String hostname;
	// 主机IP
	public String ip;
	// 主机通讯端口
	public String port;
	// 主机状态
	public String status;
	
}
