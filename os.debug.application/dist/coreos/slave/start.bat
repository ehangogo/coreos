@echo off

:: Ĭ�������˿�
set PORT=8080
:: ��ȡ��ǰĿ¼λ��
set "COREOS_HOME=%cd%"

:: ����ű��ĵ�һ��������Ϊ��,������PORT�˿ں�
if not "%1" == "" set "PORT=%1"

:: ͨ��java -jar ��������,��ָ��ռ�õĶ˿ںź������ļ���λ��
java -Dorg.osgi.service.http.port=%PORT% -Dos.home=%COREOS_HOME% -Dos.conf=%COREOS_HOME%\config.properties -jar slave.jar