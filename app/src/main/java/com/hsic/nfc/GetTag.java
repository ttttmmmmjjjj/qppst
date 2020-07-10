package com.hsic.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;

public class GetTag {
	Tag tagFromIntent;
//	public String  getTag(Intent intent,String DeviceType){
//		String Ret="";
//		try{
//			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//			try{
//				NfcV nfcV = NfcV.get(tagFromIntent);
//				nfcV.connect();
//				NfcVUtil mNfcVutil = new NfcVUtil(nfcV);
//				mNfcVutil.getUID();
//				String uid = mNfcVutil.getUID();
//				Ret=mNfcVutil.readOneBlockByNormal(3,DeviceType);
//
//			}catch(Exception ex){
//				ex.toString();
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return Ret;
//	}
	public String[]  getTag2(Intent intent,String  deviceType){
		String[] ret=new String[3];
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		try{
			NfcV nfcV = NfcV.get(tagFromIntent);
			nfcV.connect();
			NfcVUtil mNfcVutil = new NfcVUtil(nfcV);
			mNfcVutil.getUID();
			ret[0] = mNfcVutil.getUID();
			ret[1]=mNfcVutil.ReadBlocks(0, 45,deviceType);
			
		}catch(Exception ex){
			
		}
		return ret;
	}
	/**
	 * 
	 * @param intent
	 * @param startBlock ��ʼ��
	 * @param Count	����
	 * @return
	 */
	public String[]  getTagAndUID(Intent intent,int startBlock,int Count ,String deviceType){
		String[] ret=new String[3];
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		try{
			NfcV nfcV = NfcV.get(tagFromIntent);
			nfcV.connect();
			NfcVUtil mNfcVutil = new NfcVUtil(nfcV);
			mNfcVutil.getUID();
			ret[0] = mNfcVutil.getUID();
			ret[1]=mNfcVutil.ReadBlocks(startBlock, Count,deviceType);
			
		}catch(Exception ex){
			ret=null;
		}
		return ret;
	}
	public String[] GetStuffID(Intent intent,String deviceType){
		String[] ret=new String[2];
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		try{
			NfcV nfcV = NfcV.get(tagFromIntent);
			nfcV.connect();
			NfcVUtil mNfcVutil = new NfcVUtil(nfcV);
			mNfcVutil.getUID();
			ret[0] = mNfcVutil.getUID();
			ret[1]=mNfcVutil.ReadBlocks(0, 3,deviceType);
			
		}catch(Exception ex){
			
		}
		return ret;
	}
	public String[]  getTag(Intent intent ,int begin, int count,String deviceType){
		String[] ret=new String[2];
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		try{
			NfcV nfcV = NfcV.get(tagFromIntent);
			nfcV.connect();
			NfcVUtil mNfcVutil = new NfcVUtil(nfcV);
			mNfcVutil.getUID();
			ret[0] = mNfcVutil.getUID();
			ret[1]=mNfcVutil.ReadBlocks(begin, count,deviceType);

		}catch(Exception ex){

		}
		return ret;
	}
}
