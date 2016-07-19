package com.marktrace.mode;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.marktrace.R;
import com.marktrace.activity.CokeReaderActivity;
import com.marktrace.adapter.DataAdapter;
import com.marktrace.bean.CardBean;
import com.marktrace.sendinterface.ReadTagDataResult;
import com.marktrace.sendinterface.UpdateState;
import com.marktrace.threadmanager.Constants;

public class TagReadActivity extends AbstractCodeReader {

	private Button startReadView = null;
	private Button endReadView = null;
	private Button pickRecodeView = null;
	private Button exportRecodeView = null;
	private ListView infoView = null;
	private List<CardBean> dataList = new ArrayList<CardBean>();
	private DataAdapter dataAdapter = null;
	private Handler myHandler;
	private DataFile dataFile = null;
	private View clearDataView = null;

	public TagReadActivity(CokeReaderActivity mActivity) {
		this.mActivity = mActivity;
	}

	@Override
	public void bindView() {
		startReadView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startReadView.setEnabled(false);
				endReadView.setEnabled(true);
				showToast(R.string.start_read_data);
				start();

			}
		});

		endReadView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startReadView.setEnabled(true);
				endReadView.setEnabled(false);
				stop();
				showToast(R.string.end_read_data);

			}
		});

		pickRecodeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pick();
				pickRecodeView.setGravity(Gravity.CENTER);
				showToast(R.string.start_pick_record);
			}
		});

		exportRecodeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dataList.isEmpty()){
					showToast(R.string.no_data_to_export);
					return ;
				}
				if(export()){
					dataList.clear();
					dataAdapter.notifyDataSetChanged();
					showToast(R.string.export_data_success);
				}else{
					showToast(R.string.export_data_fail);
				}
				exportRecodeView.setGravity(Gravity.CENTER);
			}
		});
		
		clearDataView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle(R.string.warm_titlea);
				builder.setMessage(R.string.message);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataList.clear();
						dataAdapter.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}

	@Override
	public void findView() {
		startReadView = (Button) getActivity().findViewById(R.id.readStart);
		endReadView = (Button) getActivity().findViewById(R.id.readEnd);
		pickRecodeView = (Button) getActivity().findViewById(R.id.pickRecord);
		exportRecodeView = (Button) getActivity().findViewById(
				R.id.exportRecode);
		infoView = (ListView) getActivity().findViewById(R.id.info2);
		dataAdapter = new DataAdapter(dataList, getActivity());
		infoView.setAdapter(dataAdapter);
		infoView.setDivider(null);
		infoView.setFadingEdgeLength(0);
		infoView.setCacheColorHint(0);
		myHandler = new Handler(getActivity().getMainLooper());
		clearDataView = getActivity().findViewById(R.id.clear_data);
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

		((TableLayout) getActivity().findViewById(R.id.info_title2))
				.addView(row);
	}

	public void init() {

	}

	private void start() {
		if (getmDataSelectImple() != null) {
			getmDataSelectImple().start();
			getActivity().getmDataSelectImple().setTagDataResult(
					new TagDataSelectResultImpl());
		} else {
			getActivity().getmDataSelectImple().setUpdateState(
					new UpdateStateImpl());
		}
	}

	private void stop() {
		if (getmDataSelectImple() != null)
			getmDataSelectImple().stop();
	}

	private void pick() {
		if (getmDataSelectImple() != null) {
			getActivity().getmDataSelectImple().setTagDataResult(
					new TagDataSelectResultImpl());
		}
		getmDataSelectImple().pickRecord();
//		showToast(R.string.pick_record_fail);
	}

	private boolean export() {
		if (dataFile == null) {
			dataFile = new DataFile();
		}
		String filePath = DataFile.fileDirectory + dataFile.getFileName();
		try {
			dataFile.infoFile(filePath, dataList);
			showToast(getActivity().getString(R.string.file_save_path) + filePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public class UpdateStateImpl implements UpdateState {

		@Override
		public void updateState(int state) {
			getActivity().getmDataSelectImple().setState(
					Constants.STATE_CONNECT_FAIL);
		}

	}

	public class TagDataSelectResultImpl implements ReadTagDataResult {

		@Override
		public void visble(List<CardBean> data) {
			Log.i(TAG, "record:" + data.size());
			for (final CardBean cb : data) {
				final boolean same = isExist(cb);
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						if (!same) {
							dataList.add(cb);
						}
						dataAdapter.notifyDataSetChanged();
					}
				});
			}

		}

	}

	private boolean isExist(CardBean cb) {
		for (CardBean c : dataList) {
			if (c.getCardNo().equals(cb.getCardNo())) {
				c.setTime(cb.getTime());
				c.setCardCount(c.getCardCount() + 1);
				return true;
			}
		}
		return false;
	}

	
	@Override
	public void setConnect(boolean isConnect) {
		super.setConnect(isConnect);
		if(!isConnect){
			if(!startReadView.isEnabled()){
				startReadView.setEnabled(true);
				endReadView.setEnabled(false);
			}
		}
	}
}
