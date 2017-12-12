package com.tencent.tmsecure.demo.urlmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.tmsecure.demo.R;

import tmsdk.common.module.urlcheck.UrlCheckResult;
import tmsdk.common.module.urlcheck.UrlCheckType;
import tmsdk.fg.creator.ManagerCreatorF;
import tmsdk.fg.module.urlcheck.ICheckUrlCallback;
import tmsdk.fg.module.urlcheck.UrlCheckManager;

public class UrlMonitorActivity extends Activity implements OnClickListener {
	private TextView mContentShower;//显示结果
	private EditText mInputUrl;//网址输入
	private Button 	 mCheckButton;//检查按钮
	private UrlCheckManager mUrlCheckManager;//恶意网址检测对象
	private int mUrlIndex = 0;
	private String TAG = "urlCheck";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.urlmonitor_activity);
		
		mContentShower = (TextView) findViewById(R.id.content_shower);
		mInputUrl   = (EditText) findViewById(R.id.input_url);
		mCheckButton = (Button) findViewById(R.id.check_btn);
		findViewById(R.id.btn1).setOnClickListener(this);
		mCheckButton.setOnClickListener(this);
		mUrlCheckManager = ManagerCreatorF.getManager(UrlCheckManager.class);//对象初始化
	}
	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				UrlCheckResult rst = (UrlCheckResult) msg.obj;
				if(null != rst) {
					mContentShower.setText(parseResult(rst));
					Log.i(TAG,"url检测结果:"+parseResult(rst));
				}
			} else if(msg.what == 1) {
				//mContentShower.setText((String)msg.obj);
				UrlCheckResult rst = (UrlCheckResult) msg.obj;
				if(rst != null){
					Log.i(TAG,"url is "+URLS[mUrlIndex]);
					Log.i(TAG,"url检测结果:"+parseResult(rst));
				}
				if(mUrlIndex < URLS.length-1){
					mUrlIndex ++;
					checkUrlTest();
				}
				
			} else if(msg.what == 2) {
				
				mInputUrl.setText((String)msg.obj);
			}
		}
	};
	
	private void checkUrl() {
		new Thread() {
			@Override
			public void run() {
				String host = mInputUrl.getText().toString();//用户输入的网址
				int ret = mUrlCheckManager.checkUrl(host, new ICheckUrlCallback() {
					@Override
					public void onCheckUrlCallback(UrlCheckResult urlCheckResult) {
						mHandler.obtainMessage(0,urlCheckResult).sendToTarget();
					}
				});
				
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.check_btn:
				checkUrl();
				break;
			case R.id.btn1:
				mUrlIndex = 0;
				checkUrlTest();
				break;
		}
	}
	
	/**
	 * V3接口测试
	 */
	private static String[] URLS = {
			"http://9.ertongnet.cn.cname.yunjiasu-cdn.net/you/li",
			"l0086cgyu.com",
			"http://l0086cgyu.com",
			"http://l0086cgyu.com",
			"iigqn.top",
			"http://wap.iigqn.top",
			"bbs.mamaqing.tk",
			"http://qiangsheng0311.com/t2/taobao.php?id=3668#__SOSO_RAW_URL___",
			"http://BBS.MAMAQING.TK/BBS/MAIN.ASP",
			"http://qiangsheng0311.com/t2/taobao.php?id=3667#__SOSO_RAW_URL___",
			"pgfwt.com",
			"pgfxf.com",
			"http://pgfxf.com",
			"http://pgfwt.com",
			"zjzxjt.cn",
			"http://hao8.zjzxjt.cn/gd_1_41.html",
			"http://t.cn/Rqa6DjC",
			"http://t.cn/R54xFOD",
			"http://z0531.rgsybx.cn/tg.jsp?user=1348493827?p=3230313630363031313535353535",
			"http://pay.ndsxxmy.com/goods-66.aspx?webid=WKCCU0",
			"http://xkzyq.bx890pk.top:81/wap/goods.php?id=1",
			"http://tmall.jd.vkweiku-399.xyz:188/mob/detail_2.asp?id=5868&refid=c98b32f5530afe10a2af44846a053115&d=4ac16adae9e567ec&wxsigntext=04b65f425092bc333a3261937c75bf45&_2=1464850718717",
			"http://llrlu.vd3s4x0.top:81/wap/goods.php?id=1&from=singlemessage&isappinstalled=0",
			"http://w0975.pw:89/mob/detail_2.asp?id=5796&refid=09028a4e69251b86b80fdfc2690348d1&s=faaa5f3cbe&wxsigntext=24dd1d644218a8bb9785d8496c8aac99",
			"http://tmall.jd.w1476.xyz:89/mob/detail_2.asp?id=5898&refid=37098a5d19caecffbaf4ac92449a3693&v=572e2d4351ceea9&wxsigntext=8eb6fabfd3f33918ea406c10b9298739&nsukey=b71qh7%2BpP9ccBxWBjg%2BrZ4Sf0gGyYn6PriT2CfNVX1ADMMqo4vl07c0y6RAKZQCzo6f5XC2s%2FnZIRavKc1ygDw%3D%3D&_2=1464850707537",
			"http://xifkj.a9kj5by.top:81/wap/goods.php?id=1",
			"http://tmall.jd.vkweiku-409.xyz:188/mob/detail_2.asp?id=5933&refid=491a7b1314a850fa3d2b8b8c6b19132d&g=6e5647ec4472",
			"http://tmall.jd.vkweiku-416.xyz:188/mob/detail_2.asp?id=5933&refid=2403b5f0d377e3bcb27d9954880ac675&d=b31ce6f9&wxsigntext=83cc06516b44a6c30e16aa75b6712ef8",
			"http://tmall.jd.vkweiku-391.xyz:188/mob/detail_2.asp?id=5933&refid=2403b5f0d377e3bcb27d9954880ac675&d=b31ce6f9&wxsigntext=83cc06516b44a6c30e16aa75b6712ef8&_2=1464850633569",
			"http://tmall.jd.w1476.xyz:89/mob/detail_2.asp?id=5898&refid=37098a5d19caecffbaf4ac92449a3693&v=572e2d4351ceea9&wxsigntext=8eb6fabfd3f33918ea406c10b9298739&nsukey=b71qh7%2BpP9ccBxWBjg%2BrZ4Sf0gGyYn6PriT2CfNVX1ADMMqo4vl07c0y6RAKZQCziZBJYQiqH3rOtjyZfiKvIA%3D%3D&_2=1464850707537",
			"http://dxdown.wannianli365.com/cx/2016052710/187/setup_1890mly1.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/25/setup_0362a1qr.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_0988h30z.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_0448wyJk.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_2123Uywe.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_1655eZ6F.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_0296fZkJ.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/25/setup_1562jums.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_0248sd9n.exe",
			"http://dxdown.wannianli365.com/cx/2016052711/26/setup_0056wnpb.exe",
			"http://hgbet0005.com",
			"http://301.uhen4.top",
			"http://164134.cc",
			"http://www.abbeey.com",
			"http://859.9yu10.win",
			"http://www.huawang333.com",
			"http://cstchina.net",
			"http://www.v88448.com",
			"http://suo.im/h3pbw",
			"http://xpj1592.com",
			"http://www.xintuan.com/dlbjn.html",
			"http://www.120pfzj.cn/sgydm.html",
			"http://soumao.laikeju.top/youku.html",
			"http://erqiang.ihuhuan.com/AV_m007.apk",
			"http://kdyy.cptcn.cn/htmls/pd117mfjy",
			"http://g.pconline.com.cn/dl/wapdl_speed_90982_6922993/qq2012_mobile.apk",
			"http://download.winmdm.com/wuhe/wuhe0105421rel.apk",
			"http://www.xintuan.com/kjuog.html",
			"http://52lidu.com/forum.php?mod=viewthread&tid=6943&extra=page=1&filter=typeid&typeid=55",
			"http://www.my10rl.com",
			"http://www.renren.com/autoLogin?r=http%3A%2F%2Frenren.com.vREvhRdbj.dgyfsd.com%2Fhotvideo%2Fj7qCN.%2Fplay.html%3Fvid%3D6%26type%3D2%26_wv%3D1027%26from%3Dhttp%3A%2F%2Fuser.qzone.qq.com%2F123579%26t%3D1451312320&vid=6&_wv=1027&3yCpfqjKqfrom=http%3A%2F%2Fxw.qq.com%2F&t=1451312320",
			"http://t.cn/RUjyGKe",
			"http://t.cn/RGx3B9j",
			"http://bjs89.com",
			"http://home.88821829.cn/download/QC.php?0facd866460b3631cb5c12fccdca74619c37aa9f",
			"http://dwz.cn/agey408",
			"http://www.renren.com/autoLogin?r=http%3A%2F%2Frenren.com.XwbvOxnNU.dgyfsd.com%2Fhotvideo%2FADZJK.%2Fplay.html%3Fvid%3D6%26type%3D2%26_wv%3D1027%26from%3Dhttp%3A%2F%2Fuser.qzone.qq.com%2F123579%26t%3D1451307416&vid=6&_wv=1027&9VfEDWUnkfrom=http%3A%2F%2Fxw.qq.com%2F&t=1451307416",
			"http://t.cn/RG9EInZ",
			"http://t.cn/R4vwQrt",
			"http://url.cn/2D9RtJX?_wv=1"
	};
	//	private String[] mV3Result = new String[URLS.length];
//	private int mPos = 0;
//	private ICheckUrlCallbackV3 mICheckUrlCallbackV3 = new ICheckUrlCallbackV3() {
//		@Override
//		public void onCheckUrlCallback(UrlCheckResultV3 result) {
//			if(result != null) {
//				String rst = getV3Result(result);
//				mV3Result[mPos] = rst+"\n";
//				Log.v("demo", rst);
//			} else {
//				mV3Result[mPos] = "checkv3 url error!\n";
//				Log.v("demo", "checkv3 url error!");
//			}
//			Log.v("demo", "UrlCheckManagerV3 check:"+mV3Result[mPos]);
//			mPos = ((mPos%URLS.length) == (URLS.length - 1))?0:(mPos+1);
//			mHandler.obtainMessage(1).sendToTarget();
//		}
//	};
	private void checkUrlTest() {
		new Thread() {
			@Override
			public void run() {
				
				int ret = mUrlCheckManager.checkUrl(URLS[mUrlIndex], new ICheckUrlCallback() {
					@Override
					public void onCheckUrlCallback(UrlCheckResult urlCheckResult) {
						mHandler.obtainMessage(1,urlCheckResult).sendToTarget();
					}
				});
				Log.v(TAG, "check end");
			}
		}.start();
	}

//	private String getV3Result(UrlCheckResultV3 result) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(result.url.substring(7, 13));
//		sb.append("\n result level=");
//		sb.append(result.level);
//		sb.append(" linkType=");
//		sb.append(result.linkType);
//		sb.append(" mErrCode=");
//		sb.append(result.mErrCode);
//		sb.append(" riskType=");
//		sb.append(result.riskType);
//		return sb.toString();
//	}


//	private String getResult(UrlCheckResult rst, String url) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(url);
//		sb.append("\n UrlCheckResult result=");
//		sb.append(rst.result);
//		sb.append(" mainHarmId=");
//		sb.append(rst.mainHarmId);
//		return sb.toString();
//	}
	
	private String parseResult(UrlCheckResult rst) {
		StringBuilder sb = new StringBuilder();
		sb.append("result =" + rst.result + ":");
		switch(rst.result) {
			case UrlCheckResult.RESULT_REGULAR:
				sb.append("正常");
				break;
			case UrlCheckResult.RESULT_HARM:
				sb.append("恶意");
				break;
			case UrlCheckResult.RESULT_SHADINESS:
				sb.append("可疑");
				break;
			default:
				sb.append("未知");
				break;
		}
		sb.append("	mainHarmId=" + rst.mainHarmId + ":");
		//具体的描述可以自己发挥
		switch(rst.mainHarmId) {
			case UrlCheckType.REGULAR:
				sb.append("正常，非恶意");
				break;
			case UrlCheckType.COCKHORSE:
				sb.append("挂有木马");
				break;
			case UrlCheckType.DEFAULT_CHEAT:
				sb.append("未归类欺诈");
				break;
			case UrlCheckType.MONEY_CHEAT:
				sb.append("汇款诈骗；告知用户中奖，提供验证码，并要求汇款手续费等来领取。");
				break;
			case UrlCheckType.SP_SERVICE:
				sb.append("SP增值服务；答题赢Q币，个性签名等形式。最终通过恶意SP增值服务实现欺诈。");
				break;
			case UrlCheckType.STEAL_ACCOUNT:
				sb.append("盗号；钓鱼盗号，要求用户输入QQ号码和QQ密码，盗取QQ号。");
				break;
			case UrlCheckType.TIPS_CHEAT:
				sb.append("跳转;通过中奖tips,中奖留言的形式引导用户点击跳到欺诈站点。");
				break;
			case UrlCheckType.TIPS_DEFAULT:
				sb.append("tips;页面中有一个快区域，位置可以自动调整，引诱用户点击。");
				break;
			case UrlCheckType.GAMES_HANG:
				sb.append("外挂;游戏外挂。");
				break;
			case UrlCheckType.MAKE_MONEY:
				sb.append("网挣");
				break;
			case UrlCheckType.SEX:
				sb.append("色情");
				break;
			case UrlCheckType.PRIVATE_SERVER:
				sb.append("私服；游戏私服");
				break;
			case UrlCheckType.MSG_REACTIONARY:
				sb.append("信息安全恶意，反动言论等");
				break;
			case UrlCheckType.MSG_SHADINESS:
				sb.append("可疑跳转服务");
				break;
			case UrlCheckType.MSG_BLOG:
				sb.append("可疑blog");
				break;
			case UrlCheckType.UNKNOWN:
			default:
				sb.append("未知");
				break;
		}
		sb.append("	mEvilType=" + rst.mEvilType + ":");
		//具体的描述可以自己发挥
		switch(rst.mEvilType) {
			case UrlCheckResult.EVIL_CHEAT1:
				sb.append("社工欺诈");
				break;
			case UrlCheckResult.EVIL_CHEAT2:
				sb.append("信息诈骗");
				break;
			case UrlCheckResult.EVIL_FAULTSALES:
				sb.append("虚假销售");
				break;
			case UrlCheckResult.EVIL_MALICEFILE:
				sb.append("恶意文件");
				break;
			case UrlCheckResult.EVIL_LOTTERYWEB:
				sb.append("博彩网站");
				break;
			case UrlCheckResult.EVIL_SEXYWEB:
				sb.append("色情网站");
				break;
			case UrlCheckResult.EVIL_RISKWEB:
				sb.append("风险网站");
				break;
			case UrlCheckResult.EVIL_ILLEGALCONTENT:
				sb.append("非法内容");
				break;
			case UrlCheckResult.EVIL_NOEVIL:
			default:
				sb.append("正常");
				break;
		}
		
		return sb.toString();
	}
}
