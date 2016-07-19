package com.marktrace.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.marktrace.R;
import com.marktrace.bean.CardBean;

public class DataAdapter extends BaseAdapter {
	
	private List<CardBean> dataList = null ;
	private LayoutInflater mLayoutInflater = null ;
	
	public DataAdapter(List<CardBean> dataList,Context mContext){
		this.dataList = dataList ;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if(dataList == null){
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		CardBean cBean = dataList.get(arg0);
		ViewHolder vHolder=null;
		if(arg1 == null){
			vHolder=new ViewHolder();
			arg1 = mLayoutInflater.inflate(R.layout.data_item, null);
			vHolder.tv1 = (TextView)arg1.findViewById(R.id.data1);
			vHolder.tv2 = (TextView)arg1.findViewById(R.id.data2);
			vHolder.tv3 = (TextView)arg1.findViewById(R.id.data3);
			vHolder.tv4 = (TextView)arg1.findViewById(R.id.data4);
			
			arg1.setTag(vHolder);
		}else {
			vHolder=(ViewHolder)arg1.getTag();
		}
		vHolder.tv1.setText("" + (arg0+ 1));
		vHolder.tv2.setText("" +cBean.getCardNo());
		vHolder.tv3.setText("" +cBean.getTime());
		vHolder.tv4.setText("" +cBean.getCardCount());
		vHolder.tv1.setVisibility(View.VISIBLE);
		vHolder.tv2.setVisibility(View.VISIBLE);
		vHolder.tv3.setVisibility(View.VISIBLE);
		vHolder.tv4.setVisibility(View.VISIBLE);
		return arg1;
	}
	
	private class ViewHolder {
		private	TextView tv1;
		private	TextView tv2;
		private	TextView tv3;
		private	TextView tv4;
	}

}
