package com.tencent.tmsecure.demo.spacemanager;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;

import com.tencent.tmsecure.demo.LogUtil;
import com.tencent.tmsecure.demo.R;

import tmsdk.fg.creator.ManagerCreatorF;
import tmsdk.fg.module.spacemanager.FileInfo;
import tmsdk.fg.module.spacemanager.FileMedia;
import tmsdk.fg.module.spacemanager.FileScanResult;
import tmsdk.fg.module.spacemanager.ISpaceScanListener;
import tmsdk.fg.module.spacemanager.SpaceManager;

public class
SpaceManagerActivity extends Activity  {

	static final String TAG ="SpaceManagerActivity";
	private Button mBigfileStopScan, mBigfileScan,mBigfileDel;
	private Button mPhotoScan;
	SpaceManager mSpaceManager;
	
	private LinearLayout mPathsLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_manager_activity);
		mBigfileScan = (Button) this.findViewById(R.id.scan_bigfile);
		mBigfileStopScan = (Button) this.findViewById(R.id.stop_bigfilescan);
		mBigfileStopScan.setEnabled(false);
		mBigfileDel = (Button) this.findViewById(R.id.delete_bg);


		mPhotoScan = (Button) this.findViewById(R.id.scan_photo_button);
		
		mSpaceManager = ManagerCreatorF.getManager(SpaceManager.class);
		
		mSpaceManager.appendWhitePath("/1");
		mSpaceManager.appendCustomSdcardRoots("/storage/emulated/0/1");
		
		mBigfileScan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSpaceManager.bigFileScan(mBigFileListener)){
					mBigfileStopScan.setEnabled(true);
					mBigfileScan.setEnabled(false);
				}
			}
		});
		
		mBigfileStopScan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSpaceManager.stopBigFileScan()){
					mBigfileStopScan.setEnabled(false);
					mBigfileScan.setEnabled(true);
				}
			}
		});
		
		mBigfileDel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(null!=mFileScanResult){
					new Thread() {
						public void run() {
							//计算删除百分比
							int fileCnt = mFileScanResult.mBigFiles.size()+mFileScanResult.mRadioFiles.size()+mFileScanResult.mVideoFiles.size();
							int delFileCnt = 0;
							for(FileInfo _file:mFileScanResult.mBigFiles){
								_file.delFile();
								delFileCnt++;
							}
							for(FileInfo _file:mFileScanResult.mRadioFiles){
								_file.delFile();
								delFileCnt++;
							}
							for(FileInfo _file:mFileScanResult.mVideoFiles){
								_file.delFile();
								delFileCnt++;
							}
						}
					}.start();
				}
			}
		});
		
		
		
		mPhotoScan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SpaceManagerActivity.this, SpaceManagerPhoto.class);
				startActivity(intent);
			}
		});
		
		/*
		 * add by lindseyge 16-5-23
		 * 通过edittext更改指定的路径，并将输入参数通过intent传到图片控制的activity以便在那个activity启动时就进行图片扫描
		 */
		/*mEditPath = (EditText)findViewById(R.id.scan_photo_by_path_edit);
		findViewById(R.id.scan_photo_button_by_path).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SpaceManagerActivity.this, SpaceManagerPhoto.class);
				//List<String> paths = new ArrayList<String>();
				String[] paths = new String[mPathsLayout.getChildCount()+1];
				String scanPath = mEditPath.getText().toString();
				if(scanPath.length()>=1 && scanPath.charAt(0)=='/')
					paths[0] =  Environment.getExternalStorageDirectory()+scanPath;
				else
					paths[0] =  Environment.getExternalStorageDirectory()+"/"+scanPath;
				
				for(int i=1; i<paths.length; i++){
					scanPath = ((EditText)((LinearLayout)mPathsLayout.getChildAt(i-1)).getChildAt(0)).getEditableText().toString();
					if(scanPath.length()>=1 && scanPath.charAt(0)=='/')
						paths[i] = Environment.getExternalStorageDirectory()+scanPath;
					else
						paths[i] = Environment.getExternalStorageDirectory()+"/"+scanPath;
				}
				intent.putExtra("SCAN_PATH", paths);
				
				startActivity(intent);
			}
			
		});
		mPathsLayout = (LinearLayout)findViewById(R.id.scan_photo_by_path_linearlayout);
		findViewById(R.id.add_path_button).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText ed = new EditText(SpaceManagerActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = 14;
				ed.setLayoutParams(params);
				mPathsScroll.addView(ed);
				addPathView();
			}
			
		});*/
	}
	
	private void addPathView(){
		LinearLayout ll = new LinearLayout(SpaceManagerActivity.this);
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    ll.setLayoutParams(p1);
	    ll.setOrientation(LinearLayout.VERTICAL);
		
		EditText ed = new EditText(SpaceManagerActivity.this);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		p2.topMargin = 14;
		//p2.weight = 1;
		ed.setLayoutParams(p2);
		
		Button bt = new Button(SpaceManagerActivity.this);
		LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		p3.topMargin = 14;
		//p3.weight = 0.3f;
		bt.setLayoutParams(p3);
		bt.setText("删除");
		
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPathsLayout.removeView((LinearLayout)v.getParent());
			}
			
		});
		
		ll.addView(ed);
		ll.addView(bt);

		mPathsLayout.addView(ll);
	}

	//大文件扫描结果。
	FileScanResult mFileScanResult;
	ISpaceScanListener mBigFileListener = new ISpaceScanListener(){
		@Override
		public void onFound(Object obj) {
			// TODO Auto-generated method stub
			Log.i(TAG,"onFound:"+obj.toString());
			FileInfo fileInfo = (FileInfo) obj;
			if (fileInfo.type == fileInfo.TYPE_BIGFILE){
				Log.i(TAG,"onaaaaa:"+obj.toString());
			}
		}

		@Override
		public void onFinish(int aErrorCode,Object obj) {
			// 回调在主线程中，不是工作线程。所以直改弹alert没关系，不会ANR等错误
			Log.i(TAG,"onFinish");
			if(obj instanceof FileScanResult){
				mFileScanResult = (FileScanResult)obj;
				for(FileInfo _file:mFileScanResult.mBigFiles){
					Log.i(TAG,"bigfile::  "+_file.mSrcName+" ::"+_file.mPath);
					LogUtil.writeToDefaultFile("bigfile::  "+_file.mSrcName+" ::"+_file.mPath);
				}
				for(FileInfo _file:mFileScanResult.mRadioFiles){
					Log.i(TAG,"radio::  "+_file.mSrcName+" ::"+_file.mPath);
					FileMedia _media = (FileMedia)_file;
					LogUtil.writeToDefaultFile("radio::  "+_file.mSrcName+" ::"+_file.mPath+" ::"+_media.title);
				}
				for(FileInfo _file:mFileScanResult.mVideoFiles){
					Log.i(TAG,"video::  "+_file.mSrcName+" ::"+_file.mPath);
					FileMedia _media = (FileMedia)_file;
					LogUtil.writeToDefaultFile("video::  "+_file.mSrcName+" ::"+_file.mPath+" ::"+_media.title);
				}
			}
			
			String mBigFileResultTips = String.format("%d 大文件  %d 音频 %d 视频", mFileScanResult.mBigFiles.size(),mFileScanResult.mRadioFiles.size(),mFileScanResult.mVideoFiles.size());
			AlertDialog.Builder builder = new AlertDialog.Builder(SpaceManagerActivity.this);
			builder.setMessage("扫描结果,"+mBigFileResultTips+",请查看log")
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //do things
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onProgressChanged(int percent) {
			// TODO Auto-generated method stub
			Log.i(TAG,"onProgressChanged  ::  "+percent);
		}

		@Override
		public void onCancelFinished() {
			// TODO Auto-generated method stub
			Log.i(TAG,"onCancelFinished");
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			Log.i(TAG,"onScanStart");
		}

	};

 
}
