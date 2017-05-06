package os.network.api;

import java.util.List;

import os.core.model.BundleInfo;
import os.core.model.HostInfo;
import os.core.model.ServiceInfo;

/**
 * ��������ӿ�
 * @author ������
 *
 */
public interface Network {
	/**
	 * ִ��Զ������
	 * @param namespace
	 * @param method
	 * @param args
	 * @return
	 */
	public <T> T call(String namespace,String method,Object... args);
	/**
	 * ��ȡ���������ṩ�Ĺ���
	 * @param service
	 * @return
	 */
	public List<ServiceInfo> getServices();
	/**
	 * ��ȡ������װ�����
	 * @param service
	 * @return
	 */
	public List<BundleInfo> getBundles();
	/**
	 * ��ȡ������IP��ͨѶ�˿ڵ���Ϣ
	 * @param service
	 * @return
	 */
	public HostInfo getHostInfo();
	
	/**
	 * ��ȡ������������������
	 * @return
	 */
	public List<Network> getRoutes();
}
