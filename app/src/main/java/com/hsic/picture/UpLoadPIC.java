package com.hsic.picture;

import android.content.Context;

import com.hsic.bean.HsicMessage;
import com.hsic.ftp.MySFTP;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;
import com.jcraft.jsch.ChannelSftp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 20180507
 * @author Administrator
 *
 */
public class UpLoadPIC {
	public HsicMessage upPicture(Context context, String deviceid ) {
		int flag = 0;
		HsicMessage hm = new HsicMessage();
		hm.setRespCode(8);
		try {
			MySFTP mysftp = new MySFTP(context);
			ChannelSftp connect = mysftp.connect();
			if (connect == null) {
				hm.setRespCode(2);
			} else {
				//创建文件夹
				String filePath = "";
				filePath = PathUtil.getImagePath();
				File file = new File(filePath);
				String[] path = file.list();
				int length = path.length;
				if (path != null && length <= 0) {
					hm.setRespCode(0);
				} else if (path != null && length > 0) {
					String ImageFile = "";
					for (int i = 0; i < path.length; i++) {
						ImageFile = path[i];
						int index = path[i].indexOf("_");
						String relationId = "";
						relationId = path[i].substring(0, index);
						/**
						 * 20170824 修改
						 * 只上传三天前的照片
						 * 修改人:tmj
						 */
						SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
						String[] pictureName=path[i].split("_");
						String pictureTime=pictureName[1];//照片拍摄时间
						String NowString = TimeUtils.getTime("yyyyMMddHHmmss");	//当前时间
						long days = TimeUtils.getDaySub(sdf,pictureTime,NowString); //时间差
						if((int)days>3){
							//本地删除照片
							flag++;
							File file1 = new File(file, path[i]);
							if (file1.exists()) {
								file1.delete();
							}
						}else{
							//上传
							//“/PIC/20171110/”
							List<String> dirList;
							Date date = new Date();
							String pattern = "yyyyMMdd";
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
									Locale.getDefault());
							String FileName = simpleDateFormat.format(date);// 获取安检日期

							dirList = new ArrayList<String>();
							dirList.add(FileName);
							mysftp.addDirs2("/PIC/",dirList,context,connect);
							boolean upload = mysftp.upload("/PIC/"+FileName+"/", file.getPath()
									+ "/" + path[i], connect);
							if (upload) {
								//返回结果不等于1照片都删掉
								if (hm.getRespCode() != 1) {
									flag++;
									File file1 = new File(file, path[i]);
									if (file1.exists()) {
										file1.delete();
									}
								}
							}
						}

					}
					if (flag < path.length) {
						hm.setRespCode(12);
					}
				}
			}
		} catch (Exception ex) {
			hm.setRespCode(9);
			ex.printStackTrace();
		}

		return hm;
	}
}
