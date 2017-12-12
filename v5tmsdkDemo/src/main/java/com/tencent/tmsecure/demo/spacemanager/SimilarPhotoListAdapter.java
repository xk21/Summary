package com.tencent.tmsecure.demo.spacemanager;

import java.util.List;

import tmsdk.fg.module.spacemanager.PhotoSimilarResult;
import tmsdk.fg.module.spacemanager.PhotoSimilarResult.PhotoSimilarBucketItem;
import com.tencent.tmsecure.demo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.TextView;

public class SimilarPhotoListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<PhotoSimilarResult>  mBucketList  = null;
	Context mContext = null;
	
	public SimilarPhotoListAdapter(Context c){
		layoutInflater = LayoutInflater.from(c);
		mContext = c;
	}
	public SimilarPhotoListAdapter(Context c,List<PhotoSimilarResult> bucketList){
		this(c);
		mBucketList = bucketList;
		
	}	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null == mBucketList||mBucketList.size() == 0){
			return 0;
		}
		return mBucketList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(null == mBucketList || position >=getCount()){
			return null;
		}
		return mBucketList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(null == mBucketList || position >=getCount()){
			return null;
		}
		ViewHolder viewHolder = null;
		if(null == convertView){
			convertView = layoutInflater.inflate(R.layout.space_similar_photo_item, null,false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView)convertView.findViewById(R.id.similar_photo_title);
			viewHolder.delete = (TextView)convertView.findViewById(R.id.similar_photo_delete);
			viewHolder.gridLayout = (GridLayout)convertView.findViewById(R.id.similar_photo_grid);
			viewHolder.delete.setTag(viewHolder.gridLayout);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final PhotoSimilarResult data = mBucketList.get(position);

		
		viewHolder.title.setText(data.mTimeString);
		viewHolder.delete.setText("删除所选");
		viewHolder.delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//deleteSelectImages(data,(GridLayout)v.getTag());
				//viewHolder.gridLayout.removeAllViews();
				//generateViews(,data);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
		generateViews(viewHolder.gridLayout,data);

		//item.setTag(data);
		return convertView;
	}
	
//	private void deleteSelectImages(ItemBucket data,GridLayout layout){
//		ArrayList<PhotoItemInBucket> list = data.mItemList;
//		Iterator<PhotoItemInBucket> sListIterator = list.iterator();  
//		while(sListIterator.hasNext()){  
//			PhotoItemInBucket e = sListIterator.next();  
//		    if(e.mSelected){  
//		    	sListIterator.remove();
//		    	new File(e.mPath).delete();  
//		    }  
//		}
//		int count = layout.getChildCount();
//		for(int i =0;i<count;++count){
//			SpaceImageItem item = (SpaceImageItem)layout.getChildAt(i);
//			if(item.mCheckBox.isChecked()){
//				layout.removeViewAt(i);
//			}
//		}
//	}
	private void generateViews(GridLayout gridLayout,PhotoSimilarResult data){
		//gridLayout.removeAllViews();
		int photoCount = data.mItemList.size();
		int gridCount = gridLayout.getChildCount();
		if(gridCount>photoCount){
			gridLayout.removeViews(photoCount, gridCount-photoCount);
		}
		for(int i=0;i<photoCount;++i){
			PhotoSimilarBucketItem imageData = data.mItemList.get(i);
			SpaceImageItem imageitem = null;
			if(i<gridCount){
				imageitem = (SpaceImageItem)gridLayout.getChildAt(i);

			}else{
				imageitem = new SpaceImageItem(mContext,imageData);	
				Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
				Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
				GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
	                rowSpan, colspan);
				gridLayout.addView(imageitem, gridParam);
			}
	        imageitem.mImageView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),imageData.mId, MediaStore.Images.Thumbnails.MINI_KIND, null));
			imageitem.mCheckBox.setChecked(imageData.mSelected);
		}
	}
    private static class ViewHolder
    {
        TextView title;
        TextView delete;
        GridLayout gridLayout;
    }

}
