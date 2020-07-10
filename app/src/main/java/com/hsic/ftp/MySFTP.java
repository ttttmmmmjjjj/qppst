package com.hsic.ftp;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hsic.bll.GetBasicInfo;
import com.hsic.tmj.qppst.R;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class MySFTP {
	private String host;
	private int time;
	private int port;
	private String username;
	private String password;
	private String fport;
	private String fTime;
//	private SharedPreferences companyInfo;
	String CompanyCode;
	GetBasicInfo getBasicInfo;
	public MySFTP(Context context) {
		String defaultFtpServerString = context.getResources().getString(
				R.string.ftp_default);
		String defaultFtpPortString = context.getResources().getString(
				R.string.ftp_port_default);
		String defaultFtpTime = context.getResources().getString(
				R.string.outTime);

		this.host = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("FTPServer", defaultFtpServerString);
		this.fport = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("FTPPort", defaultFtpPortString);
		this.fTime = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("ftpServerTime", defaultFtpTime);
		try {
			this.port = Integer.parseInt(fport);
		} catch (Exception ep) {
			this.port = 22;
		}
		try {
			this.time = Integer.parseInt(fTime);
		} catch (Exception e) {
			this.time = 30;
		}
		this.username="hsiclpg";
		this.password="hsiclpg8888";;
		/**
		 * 根据公司设置用户名和密码
		 */
		getBasicInfo=new GetBasicInfo(context);
		CompanyCode=getBasicInfo.getCompanyCode();
		if(CompanyCode.equals("1910")){
			this.username="hslpg";
			this.password="hslpg1910";;
		}
		if(CompanyCode.equals("1607")){
			this.username="jslpg";
			this.password="jslpg1607";;
		}
		if(CompanyCode.equals("1707")){
			this.username="zxlpg";
			this.password="zxlpg1707";;
		}
		if(CompanyCode.equals("2010")){
			this.username="fjlpg";
			this.password="fjlpg2010";;
		}
	}
	/**
	 * ����sftp������
	 * 
	 * @param host
	 *            ����
	 * @param port
	 *            �˿�
	 * @param username
	 *            �û���
	 * @param password
	 *            ����
	 * @return
	 */
	private SftpProgressMonitor sftpProgressMonitor = null;

	public ChannelSftp connect() {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			System.out.println("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setTimeout(time * 1000);
			// sshSession.setServerAliveInterval(92000);
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			System.out.println("Session connected.");
			System.out.println("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			System.out.println("Connected to " + host + ".");
		} catch (Exception e) {
			Log.e("FTP", e.toString());
			e.printStackTrace();
			System.out.println("00"+e.toString());
		}
		return sftp;
	}

	public ChannelSftp connect(SftpProgressMonitor monitor) {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.setTimeout(time * 1000);
			// sshSession.setServerAliveInterval(92000);
			sshSession.connect();
			// System.out.println("Session connected.");
			// System.out.println("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			this.sftpProgressMonitor = monitor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sftp;
	}

	public boolean addDirs(String directory, List<String> dirList,
			ChannelSftp sftp) {
		if (dirList == null || dirList.size() <= 0)
			return false;

		String rootString = directory;

		for (int i = 0; i < dirList.size(); i++) {
			if (mkdir(rootString, dirList.get(i).toString(), sftp)) {
				rootString = rootString + dirList.get(i).toString() + "/";
				Log.i("i=" + i, rootString);
			}
		}

		return true;
	}
	public boolean addDirs2(String directory, List<String> dirList,Context context,ChannelSftp sftp) {
		if (dirList == null || dirList.size() <= 0)
			return false;
		String rootString = directory;
		for (int i = 0; i < dirList.size(); i++) {
			if (mkdir(rootString, dirList.get(i).toString(), sftp)) {
				rootString = rootString + dirList.get(i).toString() + "/";
			}
		}

		return true;
	}
	public boolean mkdir(String directory, String dir, ChannelSftp sftp) {
		try {
			sftp.mkdir(directory + dir);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 
	 * @param directory
	 * @param uploadFile
	 * @param sftp
	 */
	public boolean upload(String directory, String uploadFile, ChannelSftp sftp) {

		try {
			sftp.cd(directory);
			File file = new File(uploadFile);
			FileInputStream fis = new FileInputStream(file);
			if (null != sftpProgressMonitor) {
				sftp.put(fis, file.getName(), sftpProgressMonitor);
			} else {
				sftp.put(fis, file.getName());

			}
			fis.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param directory
	 *            ����Ŀ¼
	 * @param downloadFile
	 *            ���ص��ļ�
	 * @param saveFile
	 *            ���ڱ��ص�·��
	 * @param sftp
	 */
	public boolean download(String directory, String downloadFile,
			String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			if (null != sftpProgressMonitor) {
				sftp.get(downloadFile, new FileOutputStream(file),
						sftpProgressMonitor);
			} else {
				sftp.get(downloadFile, new FileOutputStream(file));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param directory
	 *            Ҫɾ���ļ�����Ŀ¼
	 * @param deleteFile
	 *            Ҫɾ�����ļ�
	 * @param sftp
	 */
	public void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �г�Ŀ¼�µ��ļ�
	 * 
	 * @param directory
	 *            Ҫ�г���Ŀ¼
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	@SuppressWarnings("rawtypes")
	public Vector listFiles(String directory, ChannelSftp sftp)
			throws SftpException {
		return sftp.ls(directory);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getfTime() {
		return fTime;
	}

	public void setfTime(String fTime) {
		this.fTime = fTime;
	}


}