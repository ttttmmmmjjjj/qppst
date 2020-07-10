package com.hsic.nfc;

import android.nfc.tech.NfcV;

import com.hsic.constant.Constant;

import java.io.IOException;

public class NfcVUtil {
	private NfcV mNfcV;
	/** UID������ʽ */
	private byte[] ID;
	private String UID;
	private String DSFID;
	private String AFI;
	/** block�ĸ��� */
	private int blockNumber = 256;
	/** һ��block���� */
	private int oneBlockSize;
	/** ��Ϣ */
	private byte[] infoRmation;
	/**
	 * ��ʼ��
	 * 
	 * @param mNfcV
	 *            NfcV����
	 * @throws IOException
	 */
	public NfcVUtil(NfcV mNfcV) throws IOException {
		this.mNfcV = mNfcV;
		ID = this.mNfcV.getTag().getId();
		byte[] uid = new byte[ID.length];
		int j = 0;
//		Log.e("ID.length", "=" + ID.length);
		for (int i = ID.length - 1; i >= 0; i--) {
//			Log.e("=" + i, String.valueOf(ID[i]));
			uid[j] = ID[i];
			j++;
		}
		this.UID = printHexString(uid);
	}

	public String getUID() {
		return UID;
	}
	public String Read(String deviceType) throws IOException{
		String ret="";
		ret+=readOneBlockByNormal(3,deviceType);
		return ret;
	}
	/**
	 * ��ȡһ��λ����position��block
	 * 
	 * @param position
	 *            Ҫ��ȡ��blockλ��
	 * @return ���������ַ���
	 * @throws IOException
	 */
	public String readOneBlock(int position) throws IOException {
		byte cmd[] = new byte[11];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x2B;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		cmd[10] = (byte) position;
		byte[] res = mNfcV.transceive((new byte[] { 0, 32, (byte) position }));

		if (res[0] == 0x00) {
			byte block[] = new byte[res.length - 1];
			System.arraycopy(res, 1, block, 0, res.length - 1);
			return printHexString(block);
		}
		return null;
	}
	/**
	 * ��ȡ��begin��ʼend��block
	 * begin + count ���ܳ���blockNumber
	 * @param begin block��ʼλ��
	 * @param count ��ȡblock����
	 * @return ���������ַ���
	 * @throws IOException
	 */
	public String readBlocks(int begin, int count,String deviceType) throws IOException{
		if((begin + count) > blockNumber){
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();	
		for(int i = begin; i < count + begin; i++){
			data.append(readOneBlockByNormal(i,deviceType));
		}
		return data.toString(); 
	}
	public String readBlocksNormal(int begin, int count) throws IOException{
		if((begin + count) > blockNumber){
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();	
		for(int i = begin; i < count + begin; i++){
			data.append(readOneBlock(i));				
		}
		return data.toString(); 
	}
	
	// ��ͨ�豸��ȡ��ʽ
	public String readOneBlockByNormal(int position,String deviceType) throws IOException {
		byte cmd[] = new byte[11];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x20;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		cmd[10] = (byte) position;
		byte[] res ;
		if(deviceType.equals(Constant.DeviceType)){
			res = mNfcV.transceive((new byte[] { 0, 32, (byte) position }));
		}else{
			res = mNfcV.transceive(cmd);
		}

		if (res[0] == 0x00) {
			byte block[] = new byte[res.length - 1];
			System.arraycopy(res, 1, block, 0, res.length - 1);
			return printHexString(block);
		}
		return null;
	}
	/**
	 * ��byte[]ת����16�����ַ���
	 * 
	 * @param data
	 *            Ҫת�����ַ������ֽ�����
	 * @return 16�����ַ���
	 */
	private String printHexString(byte[] data) {
		StringBuffer s = new StringBuffer();
		;
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			s.append(hex);
		}
		return s.toString();
	}
	/**
	 * ��ͨ�豸����ȡ
	 * @param begin
	 * @param count
	 * @return
	 * @throws IOException
	 */
	public String ReadBlocks(int begin, int count,String deviceType) throws IOException {
		if ((begin + count) > blockNumber) {
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();

		for (int i = begin; i < count + begin; i++) {
			data.append(readOneBlockByNormal(i,deviceType));
		}
		return data.toString();
	}
}
