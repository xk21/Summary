package com.tencent.tmsecure.demo.numbermark;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import tmsdk.common.ErrorCode;
import tmsdk.common.creator.ManagerCreatorC;
import tmsdk.common.module.numbermarker.INumQueryRetListener;
import tmsdk.common.module.numbermarker.NumMarkerManager;
import tmsdk.common.module.numbermarker.NumQueryReq;
import tmsdk.common.module.numbermarker.NumQueryRet;
import tmsdk.common.module.numbermarker.NumberMarkEntity;
import tmsdk.common.module.numbermarker.OnNumMarkReportFinish;
/**
 1. 号码标记显示为什么有500以上这个数值？
 本地库数据标记量最大值是500,产品设计这边认为，大于500的就在UI显示500以上就好了。
 从云端查号码的这个接口得到的数据，没有500这个限制
 2. 号码标记接口的使用方式？
 "来电标记的信息接口有两个：一个是使用本地库的查找，一个通过云端查找。
 现在的流程是先通过第一个接口查找，如果找不到再用第二个接口查。"
 3. 对于用户标记的号码怎么处理？如何查找？
 号码标记本机用户标记的内容处理，手机管家自定义了一个本地库储存，以便很好的响应用户标记的号码查询同时方便用户使用自己独特的个性化号码标记。号码查询时是先查本地用户标记数据库，再查tmsdk的本地号码标记库，最后查云端。当然这个数据是上报的。
 4. 上报号码使用方式?
 -- 用户标记的数据建议report到云端的同时缓存到本地，查询号码时可以首先查这个用户标记数据。自定义标记可以可以放到本地这个库中。
 5. 本地号码标记库本地文件号码数量为多少？
 本地号码标记库大小为500k，数量约7W；
 6. 号码标记库中是只支持移动号码，还是同时支持固话号码？
 同时支持移动和固定号码
 7. 拨打电话的时候有些手机网络不通，所以无法云查。
 腾讯手机管家是这样处理的：首先，已经云查过的号码本地有缓存，当不能云查时缓存也可以起作用。其次，通话中无法联网情况的，通话结束后联网查询缓存本地。用户的来电号码重复几率很高，这样可以提高性能
 8. 号码标记如何支持虚拟运营商的？
 号码标记的云查接口从TMSDK5.6.5开始：
 支持返回虚拟运营商的归属地等信息。
 不管有没有tag都会返回信息，对象存在可能没有内容
 返回的结果中归属地信息，只有虚拟运营商号码才有内容，其它号码现在暂时没有内容。
 */
public class NumberMarkActivity extends Activity implements OnClickListener {
	
	static final String TAG = "demo";
	//检查号码
	private static final int BUTTON_CHECK_CALLING = 0x10;
	private static final int BUTTON_CHECK_CALLED = 0x11;
	private static final int BUTTON_CHECK_COMM = 0x12;
	private static final int BUTTON_CHECK_YELLOWPAGE = 0x13;
	//删除本地标记号码
	private static final int BUTTON_DEL_LOCAL_NUMBER =0x20;
	//举报号码
	private static final int BUTTON_REPORT = 2;
	
	private EditText mEditText;
	private TextView mTextView;
	private NumMarkerManager mNumMarkerManager;
	private String mInputNumber;
	private Handler myHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case 0:
					List<NumQueryRet> retList = (List<NumQueryRet>)msg.obj;
					StringBuilder strBuilder = new StringBuilder("云查结果(" + msg.arg1 + ")\n");
					if(retList != null){
						for(NumQueryRet item : retList){
							strBuilder.append(disCloudResult(item) + "\n");
						}
					}
					mTextView.setText(strBuilder.toString());
					Log.v(TAG, strBuilder.toString());
					break;
				case 1:
					int error = msg.arg1;
					String number = (String) msg.obj;
					if(error == ErrorCode.ERR_NONE) {
						mTextView.setText(number + "上报成功！");
					} else {
						mTextView.setText(number + "上报错误！");
					}
					break;
				case 2:
					int _errorCode = msg.arg1;
					String _number = (String) msg.obj;
					if(_errorCode == ErrorCode.ERR_NONE) {
						mTextView.setText(_number + "删除成功！");
					} else {
						mTextView.setText(_number + "删除失败！");
					}
					break;
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		
		mNumMarkerManager = ManagerCreatorC.getManager(NumMarkerManager.class);
		LinkedHashMap<Integer, String> ne = mNumMarkerManager.getTagNameMap();
		for(Integer n:ne.keySet()) {
			Log.v(TAG, "numbermark map :"+n+" = "+ne.get(n));
		}
		
	}
	
	private void initView() {
		RelativeLayout rootView = new RelativeLayout(this);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		rootView.setLayoutParams(lp);
		
		LinearLayout linearLayout = new LinearLayout(this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		linearLayout.setLayoutParams(lp);
		
		Button buttonCalling = new Button(this);
		buttonCalling.setId(BUTTON_CHECK_CALLING);
		buttonCalling.setText("主叫检查");
		buttonCalling.setOnClickListener(this);
		
		Button buttonCalled = new Button(this);
		buttonCalled.setId(BUTTON_CHECK_CALLED);
		buttonCalled.setText("被叫检查");
		buttonCalled.setOnClickListener(this);
		
		Button buttonComm = new Button(this);
		buttonComm.setId(BUTTON_CHECK_COMM);
		buttonComm.setText("通用检查");
		buttonComm.setOnClickListener(this);
		
		Button buttonReport = new Button(this);
		buttonReport.setId(BUTTON_REPORT);
		buttonReport.setText("举报号码");
		buttonReport.setOnClickListener(this);
		
		Button buttonYellowPage = new Button(this);
		buttonYellowPage.setId(BUTTON_CHECK_YELLOWPAGE);
		buttonYellowPage.setText("检查黄页");
		buttonYellowPage.setOnClickListener(this);
		
		Button buttonDelLocalNumber = new Button(this);
		buttonDelLocalNumber.setId(BUTTON_DEL_LOCAL_NUMBER);
		buttonDelLocalNumber.setText("删除本地号码");
		buttonDelLocalNumber.setOnClickListener(this);
		
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		llp.weight = 1;
		buttonCalling.setLayoutParams(llp);
		buttonCalled.setLayoutParams(llp);
		buttonComm.setLayoutParams(llp);
		buttonReport.setLayoutParams(llp);
		buttonYellowPage.setLayoutParams(llp);
		buttonDelLocalNumber.setLayoutParams(llp);
		
		linearLayout.addView(buttonCalling);
		linearLayout.addView(buttonCalled);
		linearLayout.addView(buttonComm);
		linearLayout.addView(buttonReport);
		linearLayout.addView(buttonYellowPage);
		
		linearLayout.addView(buttonDelLocalNumber);
		
		mEditText = new EditText(this);
		mEditText.setId(0x10000001);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mEditText.setLayoutParams(lp);
		mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
		
		mTextView = new TextView(this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, mEditText.getId());
		mTextView.setLayoutParams(lp);
		
		rootView.addView(linearLayout);
		rootView.addView(mEditText);
		rootView.addView(mTextView);
		
		setContentView(rootView);
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		//		NumQueryReq.TYPE_Common	    //不区分
		//		NumQueryReq.TYPE_Calling	//主叫
		// 		NumQueryReq.TYPE_Called, 	//被叫
		
		switch (id) {
			case BUTTON_CHECK_CALLING:
				doCheck(NumQueryReq.TYPE_Calling, mEditText.getText().toString());
				break;
			case BUTTON_CHECK_CALLED:
				doCheck(NumQueryReq.TYPE_Called, mEditText.getText().toString());
				break;
			case BUTTON_CHECK_COMM:
				doCheck(NumQueryReq.TYPE_Common, mEditText.getText().toString());
				break;
			case BUTTON_CHECK_YELLOWPAGE:
				doCheckYellowPage( mEditText.getText().toString());
				break;
			case BUTTON_REPORT:
				mInputNumber = mEditText.getText().toString();
				doReport();
				break;
			case BUTTON_DEL_LOCAL_NUMBER:
				final String _InputNumber = mEditText.getText().toString();
				new Thread(){
					public void run(){
						if(delLocalNumber(_InputNumber))
							myHandler.obtainMessage(2, 0, 0, _InputNumber).sendToTarget();
						else
							myHandler.obtainMessage(2, 1, 0, _InputNumber).sendToTarget();
					}
				}.start();
				
				break;
		}
	}
	
	// 检查号码
	private void doCheck(final int type, String number) {
		if(TextUtils.isEmpty(number)) {
			return;
		}
		mTextView.setText("");
		final String numberF = number;
		// 本地查
		Log.v(TAG, "localFetchNumberInfo--inputNumber:[" + numberF + "]");
		NumQueryRet item = mNumMarkerManager.localFetchNumberInfoUserMark(numberF);
		if (null == item) {
			item = mNumMarkerManager.localYellowPageInfo(number);
		}
		if (null == item) {
			item = mNumMarkerManager.localFetchNumberInfo(numberF);
		}
		if (item == null) {
			Log.i(TAG, "localFetchNumberInfo() " + "num:" + numberF + "   null");
			// 云查
			new Thread() {
				@Override
				public void run() {
					
					List<NumQueryReq> numbers = new ArrayList<NumQueryReq>();
					// 01062671188 01053821599 075595555
					//NumQueryReq queryInfo = new NumQueryReq(numberF, type);
//					NumQueryReq queryInfo = new NumQueryReq(numberF, NumQueryReq.TYPE_Common);
//					numbers.add(queryInfo);
//					queryInfo = new NumQueryReq(numberF, NumQueryReq.TYPE_Calling);
//					numbers.add(queryInfo);
					NumQueryReq queryInfo = new NumQueryReq(numberF, NumQueryReq.TYPE_Called);
					numbers.add(queryInfo);

//					queryInfo = new NumQueryReq("01053821599", NumQueryReq.TYPE_Called);
//					numbers.add(queryInfo);
//					queryInfo = new NumQueryReq("01062671188", NumQueryReq.TYPE_Called);
//					numbers.add(queryInfo);
					
					mNumMarkerManager.cloudFetchNumberInfo(numbers,new INumQueryRetListener(){
						@Override
						public void onResult(int arg0, List<NumQueryRet> arg1) {
							myHandler.obtainMessage(0, arg0, 0, arg1).sendToTarget();
						}
					});
				}
			}.start();
		} else {
			StringBuilder strBuilder = new StringBuilder("本地查找结果:\n");
			strBuilder.append(item.toString());
//			strBuilder.append("property:[" + item.property + "]\n"
//					+ "number[" + item.number + "]\n"
//					+ "name[" + item.name + "]\n"
//					+ "tagType[" + item.tagType + "]\n"
//					+ "类型描述[" + mNumMarkerManager.getTagName(item.tagType) + "]\n"
//					+ "tagCount[" + item.tagCount + "]\n"
//					+ "warning[" + item.warning + "]\n"
//					+ "usedFor[" + item.usedFor + "]\n");
			Log.v(TAG, strBuilder.toString());
			mTextView.setText(strBuilder.toString());
		}
	}
	private void doCheckYellowPage(String number) {
		if(TextUtils.isEmpty(number)){
			mTextView.setText("请输入号码\n");
			return;
		}
		
		NumQueryRet item = mNumMarkerManager.localYellowPageInfo(number);//.localFetchNumberInfo(numberF);
		if(null == item){
			mTextView.setText("本地黄页库无此号码："+number+"\n");
			return;
		}
		StringBuilder strBuilder = new StringBuilder("本地查找结果:\n");
		strBuilder.append(item.toString());
		Log.v(TAG, strBuilder.toString());
		mTextView.setText(strBuilder.toString());
	}
	private void doReport() {
		final List<NumberMarkEntity> numberMarkEntityList = new ArrayList<NumberMarkEntity>(1);
		NumberMarkEntity entity = new NumberMarkEntity();
		Log.v("demo", "上报号码标记002");
		final String number = mInputNumber;//"12345678902";
		/**
		 ******** 以下为必填内容 **************
		 */
		entity.phonenum = number;//电话号码
		entity.tagtype = NumberMarkEntity.TAG_TYPE_SELF_TAG;//用户自定义标签，详情见javadoc
		entity.userDefineName = "自定义标签";//这个应该是展示给用户标签中的一个自定义标签
		/**
		 ******** 以下为可选内容**************
		 */
		entity.calltime = (int)(System.currentTimeMillis()/1000);//电话开始时间,单位s
		entity.talktime = 60;//通话时间，单位s
		entity.localTagType = NumberMarkEntity.TAG_TYPE_SALES;//用户标记前看到的类型。应该是从本地查或者云查结果中得到的。这里的值为例值。
		entity.originName = "原始标签";//用户标记前看到的标签名或黄页名。应该是从本地查或者云查结果中得到的。这里的值为例值。
		numberMarkEntityList.add(entity);
		new Thread() {
			@Override
			public void run() {
				
				//先进行本地保存用户主动标记，方便来电时查询
				mNumMarkerManager.saveNumberInfoUserMark(numberMarkEntityList);
				boolean error = mNumMarkerManager.cloudReportPhoneNum(numberMarkEntityList, new OnNumMarkReportFinish() {
					
					@Override
					public void onReportFinish(int result) {
						myHandler.obtainMessage(1, result, 0, number).sendToTarget();
					}
				});
				if(!error) {//接口调用失败
					myHandler.obtainMessage(1, -1, 0, number).sendToTarget();
				}
			}
			
		}.start();
		
	}
	
	/**
	 * 云查结果显示
	 * @param nResult
	 * @return
	 */
	
	private String disCloudResult(NumQueryRet nResult) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("property:");
		strBuilder.append(nResult.property);
		if(nResult.property == NumQueryRet.PROP_Tag){
			strBuilder.append("=标记\n");
		}else if(nResult.property == NumQueryRet.PROP_Yellow){
			strBuilder.append("=黄页\n");
		}else if(nResult.property == NumQueryRet.PROP_Tag_Yellow){
			strBuilder.append("=标记黄页\n");
		}else {
			strBuilder.append("\n");
		}
		strBuilder.append("号码:[" + nResult.number + "]\n");
		strBuilder.append("名称:[" + nResult.name + "]\n");
		strBuilder.append("标记类型:[" + nResult.tagType+"="+mNumMarkerManager.getTagName(nResult.tagType) + "]\n");//tagtype从mNumMarkerManager.getTagName()获取名字字符串
		strBuilder.append("标记数量:[" + nResult.tagCount + "]\n");
		strBuilder.append("警告信息:[" + nResult.warning + "]\n");
		strBuilder.append("usedFor:");
		strBuilder.append(nResult.usedFor);
		if(nResult.usedFor == NumQueryRet.USED_FOR_Common){
			strBuilder.append("=通用\n");
		}else if(nResult.usedFor == NumQueryRet.USED_FOR_Calling){
			strBuilder.append("=主叫\n");
		}else if(nResult.usedFor == NumQueryRet.USED_FOR_Called){
			strBuilder.append("=被叫\n");
		}else {
			strBuilder.append("\n");
		}
		strBuilder.append("归属地:[" + nResult.location + "]\n");
		strBuilder.append("虚拟运营商:[" + nResult.eOperator + "]\n");
		
		return strBuilder.toString();
	}
	
	private boolean delLocalNumber(String aNumber){
		Set<String> _numbers = new HashSet<String>();
		_numbers.add(aNumber);
		return  mNumMarkerManager.delLocalList(_numbers);
	}
}



