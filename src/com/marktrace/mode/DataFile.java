package com.marktrace.mode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.marktrace.bean.CardBean;


public class DataFile {
	private   SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	public static final String fileDirectory=android.os.Environment.getExternalStorageDirectory()+"/CokeReader";
	public DataFile(){
		File file=new File(fileDirectory);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	public  String getFileName(){
		return "/"+sdf.format(new Date())+".csv";
	}
	public void infoFile(String filePath,List<CardBean> lCardBeans) throws Exception{
		File file=new File(filePath);
		if(!file.exists()){
			if(!file.createNewFile()){
				throw new Exception();
			}
		}
		FileWriter fWriter = new FileWriter(file,true);
		for(CardBean cb: lCardBeans){
			StringBuffer sb = new StringBuffer();
			sb.append(cb.getCardNo()).append(",").append(cb.getTime()).append(",").append(cb.getCardCount()).append("\n");
			fWriter.write(sb.toString());
		}
		fWriter.flush();
		fWriter.close();
	}
	public void getInfoFile(String filePath,List<CardBean> lBeans)throws Exception{
		CardBean cardBean=null;
		File file=new File(filePath);
		if(!file.exists()){
			return;
		}
		FileInputStream fis=new FileInputStream(file);
		String sb="";
		String[] sCard=null;
		BufferedReader bReader=new BufferedReader(new InputStreamReader(fis));
		while((sb=bReader.readLine())!=null){
			sCard=sb.split(",");
			if(sCard.length==3){
				cardBean=new CardBean();
				cardBean.setCardNo(sCard[0]);
				cardBean.setTime(sCard[1]);
				cardBean.setCardCount(Integer.parseInt(sCard[2]));
				lBeans.add(cardBean);
			}
		}
		bReader.close();
		fis.close();
	}
	
	public int stringToInt(String num){
		int result = 0 ;
		for(int i = 0 ; i < num.length() ; i++){
			result *= 10 ;
			result += num.charAt(i) - '0' ;
		}
		return result ;
	}
}
