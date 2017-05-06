package os.core.api;

import java.io.InputStream;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import os.core.model.BundleInfo;
import os.core.model.ConfigInfo;
import os.core.model.ServiceInfo;

/**
 * �ں˽ӿ�
 */
public interface CoreOS {
	
	// ����ӿ�
	/**
	 * ��װ���
	 * @param location ���·��
	 * @return         ���ʵ��
	 * @throws BundleException
	 */
	public Bundle install(String location) throws BundleException;
	/**
	 * ж�����
	 * @param  id or name:version
	 * @return ���ʵ��
	 * @throws BundleException
	 */
	public Bundle uninstall(String nameVersion) throws BundleException;
	/**
	 * �������
	 * @param  id or name:version
	 * @return ���ʵ��
	 * @throws BundleException
	 */
	public Bundle start(String nameVersion) throws BundleException;
	/**
	 * ֹͣ���
	 * @param  id or name:version
	 * @return ���ʵ��
	 * @throws BundleException
	 */
	public Bundle stop(String nameVersion) throws BundleException;
	/**
	 * �������
	 * @param  id or name:version
	 * @return ���ʵ��
	 * @throws BundleException
	 */
	public Bundle update(String nameVersion) throws BundleException;
	
	// ��ѯ�ӿ�
	/**
	 * ����б�
	 * @return
	 */
	public List<BundleInfo> getBundles();
	/**
	 * �ֿ�����б�
	 * @return
	 */
	public List<BundleInfo> getRepertories();
	/**
	 * �����б�
	 * @return
	 */
	public List<ServiceInfo> getServices();
	
	// ������ýӿ�
	/**
	 * �������
	 * @param namespace ��·��
	 * @param method    ������
	 * @param args      ����
	 * @return          ����ֵ
	 */
	public <T> T call(String namespace, String method, Object... args);
	
	// �����ӿ�
	/**
	 * ��ȡ��ǰ�����Ļ���
	 * @return
	 */
	public BundleContext getContext();
	/**
	 * ��ȡϵͳ���ýӿ�
	 * @param key
	 * @param def
	 * @return
	 */
	public String setConf(String key,String val);
	public String getConf(String key,String def);
	public List<ConfigInfo> listConf();
	
	/**
	 * ��ȡĳ������
	 * @return
	 */
	public Object getService(String namespace);
	
	/**
	 * ˢ�����
	 * eg:
	 * refresh
	 * refresh name:verison name:verison
	 * @return
	 */
	public List<Bundle> refresh(String... args);
	/**
	 * �����������
	 * eg:
	 * resolve
	 * resolve name:verison name:verison
	 */
	public List<Bundle> resolve(String... args);
	/**
	 * ��ȡ������ϵͳ��������
	 * eg:
	 * startLevel
	 * startLevel 0
	 */
	public String startLevel(String... args);
	/** 
	 * ��ȡ��������������м���
	 * eg:
	 * bundleLevel id
 	 * bundleLevel name:version
	 * bundleLevel name:version 2
	 * bundleLevel -i  2
	 */
	public String bundleLevel(String... args);
	/**
	 * ��װ���
	 * @param location ���λ��
	 * @param input    ������
	 * @return         ���ʵ��
	 * @throws BundleException
	 */
	public Bundle install(String location, InputStream input)throws BundleException;
}
