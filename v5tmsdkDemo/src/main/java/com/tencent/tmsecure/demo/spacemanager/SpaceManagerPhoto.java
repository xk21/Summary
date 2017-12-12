package com.tencent.tmsecure.demo.spacemanager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import tmsdk.fg.creator.ManagerCreatorF;
import tmsdk.fg.module.spacemanager.ISpaceScanListener;
import tmsdk.fg.module.spacemanager.PhotoScanResult;
import tmsdk.fg.module.spacemanager.PhotoScanResult.PhotoItem;
import tmsdk.fg.module.spacemanager.SpaceManager;
public class SpaceManagerPhoto extends Activity {
	//丑陋的数据共享方式
	public static PhotoScanResult result;
	public static SpaceManager mSpaceManager;
	Button allPhotoButton;
	Button screenShotButton;
	Button similarButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_manager_photo);
		mSpaceManager = ManagerCreatorF.getManager(SpaceManager.class);
		allPhotoButton = (Button)findViewById(R.id.all_photo_button);
		screenShotButton = (Button)findViewById(R.id.screen_shot_button);
		similarButton = (Button)findViewById(R.id.similar_photo_button);
		
		//mEditPath = (EditText)findViewById(R.id.similar_photo_by_path_edit);
		
		similarButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SpaceManagerPhoto.this, SimilarPhotoActivity.class);
				startActivity(intent);
			}
		});
		
		
		allPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(SpaceManagerPhoto.this);
				builder.setMessage("使用列表展示所有图片,demo未实现")
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                //do things
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		screenShotButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(SpaceManagerPhoto.this);
				builder.setMessage("使用列表展示所有截图,demo未实现")
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                //do things
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		final ProgressDialog dialog = new ProgressDialog(this,ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	
		//writMediaItemToFile(imageItems);
		final ISpaceScanListener scanListener = new ISpaceScanListener() {
			int lastPercent=0;
			boolean isRefresh = false;
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","scan on start");
			}
			
			@Override
			public void onProgressChanged(int percent) {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","scan Progress:"+percent);
				if(percent>lastPercent){
					isRefresh = true;
					dialog.setProgress(percent);
				}
			}
			
			@Override
			public void onFound(Object obj) {
				// TODO Auto-generated method stub
				if(isRefresh){
					android.util.Log.e("fgt","scan found :"+obj);
					isRefresh = false;
				}
				
				//dialog.setMessage((String)obj);
			}
			
			@Override
			public void onFinish(int aErrorCode, Object obj) {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","scan finish error code:"+aErrorCode);
				String sAgeFormat = getResources().getString(R.string.space_all_photo);
				long stopTime = System.currentTimeMillis();
				if(aErrorCode == SpaceManager.ERROR_CODE_OK){
					result = (PhotoScanResult)obj;
					allPhotoButton.setText(String.format(sAgeFormat, result.mPhotoCountAndSize.first,result.mPhotoCountAndSize.second));
					sAgeFormat = getResources().getString(R.string.space_screen_shot);
					screenShotButton.setText(String.format(sAgeFormat,result.mScreenShotCountAndSize.first,result.mScreenShotCountAndSize.second));
					similarButton.setText(getResources().getString(R.string.space_similar_photo));
					android.util.Log.e("fgt","result.mInnerPicSize:"+result.mInnerPicSize
				+"  result.mOutPicSize:"+result.mOutPicSize
				+"  AllPhotoCount:"+result.mPhotoCountAndSize.first
				+"  AllPhotoSize:"+result.mPhotoCountAndSize.second
				+"  ScreenShotsCount:"+result.mScreenShotCountAndSize.first
				+"  ScreenShotsSize:"+result.mScreenShotCountAndSize.second);
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							writMediaItemToFile(result.mResultList);
						}
					}).start();
					
					
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(SpaceManagerPhoto.this);
					
					if(aErrorCode == SpaceManager.ERROR_CODE_IMG_NOT_FOUND){
						builder.setMessage("没有在手机中找到图片")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					}else{
						builder.setMessage("查找图片文件失败,请查看log")
					       .setCancelable(false)
					       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                //do things
					           }
					       });
					}
		
					AlertDialog alert = builder.create();
					alert.show();
					allPhotoButton.setText("error");
					screenShotButton.setText("error");
					similarButton.setText("error");
					allPhotoButton.setEnabled(false);
					screenShotButton.setEnabled(false);
					similarButton.setEnabled(false);
				}
				dialog.dismiss();
			}
			
			@Override
			public void onCancelFinished() {
				// TODO Auto-generated method stub
				
			}
			
		};

		dialog.setTitle ("图片扫描中");
		dialog.setMax(100);
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        mSpaceManager.stopPhotoScan();
				allPhotoButton.setText("canceled");
				screenShotButton.setText("canceled");
				similarButton.setText("canceled");
				allPhotoButton.setEnabled(false);
				screenShotButton.setEnabled(false);
				similarButton.setEnabled(false);
		    }
		});
		dialog.show();

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				/*
				 * add by lindseyge 16-5-23
				 * 通过intent获得路径输入，并判断是否为null来使用不同的接口
				 */
				/*String[] paths = getIntent().getStringArrayExtra("SCAN_PATH");
				if(paths==null)
					mSpaceManager.photoScan(scanListener);
				else
					mSpaceManager.photoScanByPath(scanListener, paths);*/
				mSpaceManager.photoScan(scanListener);
				//缩略图扫描
				//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			}
		}).start();
		
		super.onResume();
	}

	static private void writMediaItemToFile(ArrayList<PhotoItem> items){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("/sdcard/tmsdkimage.txt", "UTF-8");//new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(PhotoItem item:items){
			writer.println(item.mPath);
		}
		writer.close();
	}
}
