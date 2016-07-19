package com.marktrace.mode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.marktrace.R;
import com.marktrace.activity.CokeReaderActivity;
import com.marktrace.adapter.DataAdapter;
import com.marktrace.bean.CardBean;

public class DataReadActivity extends AbstractCodeReader {

	private TextView startTimeView = null;
	private TextView endTimeView = null;
	private View cleanButton = null;
	private View selectButton = null;
	private ListView infoView = null;

	private Calendar sCalendar = Calendar.getInstance();
	private Calendar eCalendar = Calendar.getInstance();

	private Dialog mDialog = null;
	private DataFile dataFile=null;
	private List<CardBean> resultList = new ArrayList<CardBean>();
	private DataAdapter dataAdapter=null;
	private boolean isStart = false;
	public DataReadActivity(CokeReaderActivity mActivity) {
		this.mActivity = mActivity;
	}

	@Override
	public void bindView() {
		selectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				select();
			}
		});

		cleanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clean();
			}
		});

		startTimeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isStart = true;
				show();
			}
		});

		endTimeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isStart = false;
				show();
			}
		});
	}

	@Override
	public void findView() {
		startTimeView = (TextView) getActivity().findViewById(R.id.dateStart);
		endTimeView = (TextView) getActivity().findViewById(R.id.dateEnd);
		cleanButton = getActivity().findViewById(R.id.empthData);
		selectButton = getActivity().findViewById(R.id.dataSelect);
		infoView = (ListView) getActivity().findViewById(R.id.info3);
		dataAdapter=new DataAdapter(resultList, getActivity());
		infoView.setAdapter(dataAdapter);
		infoView.setDivider(null);
		infoView.setFadingEdgeLength(0);
		infoView.setCacheColorHint(0);
//		TableRow row = new TableRow(getActivity());
//		TextView textView = new TextView(getActivity());
//		textView.setWidth(100);
//		textView.setText(R.string.card_seq);
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
//		textView.setTextColor(Color.BLACK);
//		row.addView(textView);
//		textView = new TextView(getActivity());
//		textView.setWidth(100);
//		textView.setText(R.string.card_no);
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
//		textView.setTextColor(Color.BLACK);
//		row.addView(textView);
//		textView = new TextView(getActivity());
//		textView.setWidth(260);
//		textView.setText(R.string.card_time);
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
//		textView.setTextColor(Color.BLACK);
//		row.addView(textView);
//		textView = new TextView(getActivity());
//		textView.setText(R.string.card_count);
//		textView.setWidth(100);
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
//		textView.setTextColor(Color.BLACK);
//		row.addView(textView);
		TableRow row = new TableRow(getActivity());
		TextView textView = new TextView(getActivity());
		textView.setWidth(160);
		textView.setText(R.string.card_seq);
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTextColor(Color.BLACK);
		row.addView(textView);
		textView = new TextView(getActivity());
		textView.setWidth(120);
		textView.setText(R.string.card_no);
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTextColor(Color.BLACK);
		row.addView(textView);
		textView = new TextView(getActivity());
		textView.setText(R.string.card_time);
		textView.setWidth(200);
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTextColor(Color.BLACK);
		row.addView(textView);
		textView = new TextView(getActivity());
		textView.setWidth(100);
		textView.setText(R.string.card_count);
		textView.setGravity(Gravity.RIGHT);
		textView.setTextColor(Color.BLACK);
		row.addView(textView);
		((TableLayout) getActivity().findViewById(R.id.info_title3)).addView(row);
	}

	@Override
	public void init() {

		startTimeView.setText(getCalendarToString(sCalendar));
		endTimeView.setText(getCalendarToString(eCalendar));
	}


	/**
	 * 
	 */
	private void select() {
		if(dataFile==null){
			dataFile=new DataFile();
		}
		resultList.clear();
		try {

			Calendar c1  = (Calendar)sCalendar.clone() ;
			Calendar c2 = (Calendar)eCalendar.clone() ;
			while(!c1.after(c2)){
				String dName = getCalendarString(c1);
				String fileName = DataFile.fileDirectory+File.separator +dName + ".csv" ;
				Log.i(TAG, fileName);
				dataFile.getInfoFile(fileName, resultList);
				c1.add(Calendar.DATE, 1);
			}
			dataAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onCreate() {
		super.onCreate();	
	}

	private String intToString(int i) {
		if (i < 10) {
			return "0" + i;
		}
		return "" + i;
	}

	private void clean() {
		resultList.clear();
		dataAdapter.notifyDataSetChanged();
	}
	
	private String getCalendarToString(Calendar calendar){
		return  "" + calendar.get(Calendar.YEAR) + "-"
		+ intToString(calendar.get(Calendar.MONTH)+1) + "-"
		+ intToString(calendar.get(Calendar.DATE));
	}
	
	private String getCalendarString(Calendar calendar){
		return  "" + calendar.get(Calendar.YEAR) 
		+ intToString(calendar.get(Calendar.MONTH)+1)
		+ intToString(calendar.get(Calendar.DATE));
	}
	
	private void show(){
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.choose_date);
		final DatePicker dp = new DatePicker(getActivity());
		builder.setView(dp);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (isStart) {
							sCalendar.set(Calendar.YEAR, dp.getYear());
							sCalendar.set(Calendar.MONTH, dp.getMonth());
							sCalendar.set(Calendar.DATE, dp.getDayOfMonth());
							startTimeView.setText(getCalendarToString(sCalendar));
						} else {
							eCalendar.set(Calendar.YEAR, dp.getYear());
							eCalendar.set(Calendar.MONTH, dp.getMonth());
							eCalendar.set(Calendar.DATE, dp.getDayOfMonth());
							endTimeView.setText(getCalendarToString(eCalendar));
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		mDialog = builder.create();
		mDialog.show();
	}

}
