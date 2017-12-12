package com.tencent.tmsecure.demo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tmsdk.common.TMSDKContext;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DemoMainActivity extends ListActivity {
	private static final String action = "android.intent.action.SDK";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("tmsdk "+TMSDKContext.getSDKVersionInfo());
		Intent intent = getIntent();
		String path = intent.getStringExtra("com.example.android.sdk.Path");

		if (path == null) {
			path = "";
		}

		setListAdapter(new SimpleAdapter(this, getData(path), android.R.layout.simple_list_item_1, new String[] { "title" }, new int[] { android.R.id.text1 }));
		getListView().setTextFilterEnabled(true);
		
		if(!DemoApplication.mBresult) {
			//tmsdk初始化失败 ,可能是TMSDK通用so加载失败。
			new AlertDialog.Builder(this).setTitle("TMSDK提示")
										.setMessage("tmsdk初始化失败 ,可能是TMSDK通用so加载失败！")
										.setPositiveButton(android.R.string.ok, null)
										.setOnDismissListener(new OnDismissListener() {
											
											@Override
											public void onDismiss(DialogInterface dialog) {
												DemoMainActivity.this.finish();
											}
										})
										.show();
			
		}
	}

	protected List<Map<String, Object>> getData(String prefix) {
 		List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

		Intent customIntent = new Intent(action, null);
		customIntent.setPackage(this.getApplicationContext().getPackageName());
		customIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);

		PackageManager pm = getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(customIntent, 0);

		if (null == list)
			return myData;

		
		//去除不存在的类
		ResolveInfo[] array2 = new ResolveInfo[list.size()];
		array2 = list.toArray(array2);
		for(ResolveInfo info : array2) {
			String clsname = info.activityInfo.name;
			try {
				Class.forName(clsname);
			} catch (ClassNotFoundException e) {
				list.remove(info);
			}
		}
		
		String[] prefixPath;

		if (prefix.equals("")) {
			prefixPath = null;
		} else {
			prefixPath = prefix.split("/");
		}

		int len = list.size();

		Map<String, Boolean> entries = new HashMap<String, Boolean>();

		for (int i = 0; i < len; i++) {
			ResolveInfo info = list.get(i);
			CharSequence labelSeq = info.loadLabel(pm);
			String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;

			if (prefix.length() == 0 || label.startsWith(prefix)) {

				String[] labelPath = label.split("/");

				String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

				if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
					addItem(myData, nextLabel, activityIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
				} else {
					if (entries.get(nextLabel) == null) {
						addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
						entries.put(nextLabel, true);
					}
				}
			}
		}

		Collections.sort(myData, sDisplayNameComparator);
		return myData;
	}

	private final static Comparator<Map<String, Object>> sDisplayNameComparator = new Comparator<Map<String, Object>>() {
		private final Collator collator = Collator.getInstance();

		public int compare(Map<String, Object> map1, Map<String, Object> map2) {
			return collator.compare(map1.get("title"), map2.get("title"));
		}
	};

	protected Intent activityIntent(String pkg, String componentName) {
		Intent result = new Intent();
		result.setClassName(pkg, componentName);
		return result;
	}

	protected Intent browseIntent(String path) {
		Intent result = new Intent();
		result.setClass(this, DemoMainActivity.class);
		result.putExtra("com.example.android.sdk.Path", path);
		return result;
	}

	protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Object item = l.getItemAtPosition(position);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) item;

		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}

}