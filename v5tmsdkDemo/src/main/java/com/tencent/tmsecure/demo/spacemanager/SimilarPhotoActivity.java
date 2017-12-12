package com.tencent.tmsecure.demo.spacemanager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import tmsdk.fg.module.spacemanager.ISpaceScanListener;
import tmsdk.fg.module.spacemanager.PhotoSimilarResult;
import tmsdk.fg.module.spacemanager.SpaceManager;
import tmsdk.fg.module.spacemanager.PhotoScanResult.PhotoItem;
import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SimilarPhotoActivity extends Activity {
	
	private TextView mTextView;
	private ListView mListView;
	private Button mDeleteAllButton;
	private SimilarPhotoListAdapter mAdapter;
	private ArrayList<PhotoItem> mMediaItem;
	private SpaceManager mPhotoManager;
	
	private  ProgressDialog dialog;// = new ProgressDialog(this,ProgressDialog.STYLE_HORIZONTAL);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_similar_activity);
		mTextView = (TextView) findViewById(R.id.similar_photo_summary);
		mListView = (ListView)findViewById(R.id.similar_photo_listview);
		mDeleteAllButton = (Button)findViewById(R.id.similar_photo_delete_all);
		mDeleteAllButton.setText("删除所有已选");
		mDeleteAllButton.setEnabled(false);
		mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(SimilarPhotoActivity.this);
				builder.setMessage("时间关系,该功能在demo中未实现")
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
		mPhotoManager = SpaceManagerPhoto.mSpaceManager;
		mMediaItem = SpaceManagerPhoto.result.mResultList;
		dialog = new ProgressDialog(this,ProgressDialog.STYLE_HORIZONTAL);
		//
		
		final ISpaceScanListener listener = new ISpaceScanListener() {
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","similar on start");
			}
			
			@Override
			public void onProgressChanged(int percent) {
				// TODO Auto-generated method stub
				dialog.setProgress(percent);
				android.util.Log.e("fgt","similar on Progress:"+percent);
				
			}
			
			@Override
			public void onFound(Object obj) {
				// TODO Auto-generated method stub
				//obj actual type is List<PhotoSimilarResult>
				android.util.Log.e("fgt","similar on fonud:");
				if (obj instanceof ArrayList) { 
                    ArrayList list = (ArrayList) obj; 
                    if (list.size() > 0 
                            && list.get(0) instanceof PhotoSimilarResult) { 
                        ArrayList<PhotoSimilarResult> photoSimilarResultList = (ArrayList<PhotoSimilarResult>) obj; 
                        if (photoSimilarResultList != null 
                                && photoSimilarResultList.size() > 0) { 
                            for (PhotoSimilarResult  sq : photoSimilarResultList) { 
                                if (sq.mItemList != null 
                                        && sq.mItemList.size() > 0) { 
                                    for (PhotoSimilarResult.PhotoSimilarBucketItem a : sq.mItemList) { 
                                    	android.util.Log.e("fgt","similar on fonud: size:"+a.mFileSize);
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
			}
			
			@Override
			public void onFinish(int aErrorCode, Object obj) {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","similar on finish error code:"+aErrorCode);
		
				if(aErrorCode == SpaceManager.ERROR_CODE_OK){
					final List<PhotoSimilarResult> result = (List<PhotoSimilarResult>)obj;
					mAdapter = new SimilarPhotoListAdapter(SimilarPhotoActivity.this,result);
					mListView.setAdapter(mAdapter);
					mDeleteAllButton.setEnabled(true);
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							outputItemBuckets(result);
						}
					}).start();
				}else{
					mDeleteAllButton.setEnabled(false);
				}
				dialog.dismiss();
			}
			
			@Override
			public void onCancelFinished() {
				// TODO Auto-generated method stub
				android.util.Log.e("fgt","similar on cancel");
				AlertDialog.Builder builder = new AlertDialog.Builder(SimilarPhotoActivity.this);
				builder.setMessage("取消识别图片成功")
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                //do things
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
			
		};
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle ("图片识别中");
		dialog.setMax(100);
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        mPhotoManager.stopPhotoSimilarCategorise();
		        dialog.dismiss();
		        mTextView.setText("图片识别已取消");
		        
		    }
		});
		dialog.show();
		if(null != mPhotoManager&& null != mMediaItem){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mPhotoManager.photoSimilarCategorise(listener, mMediaItem);
				}
			}).start();
			
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(SimilarPhotoActivity.this);
			builder.setMessage("获取待识别的数据失败,请查看log")
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //do things
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}
	static private void outputItemBuckets(List<PhotoSimilarResult> items){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("/sdcard/tmsdkbucket.txt", "UTF-8");//new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(PhotoSimilarResult item:items){
			writer.println(item.mTimeString);
			for(PhotoSimilarResult.PhotoSimilarBucketItem index: item.mItemList){
				writer.println('\t'+index.mPath+'\t'+index.mFileSize);
			}
		}
		writer.close();
	}
}
