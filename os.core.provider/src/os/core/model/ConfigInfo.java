package os.core.model;

import org.osgi.dto.DTO;

/**
 * ������Ϣ
 * @author ������
 *
 */
public class ConfigInfo extends DTO{
	public String key;
	public String value;
	public ConfigInfo(){};
	public ConfigInfo(String key,String value){
		this.key=key;
		this.value=value;
	}
}
