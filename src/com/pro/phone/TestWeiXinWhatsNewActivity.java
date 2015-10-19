package com.pro.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TestWeiXinWhatsNewActivity extends Activity implements
		OnViewChangeListener {

	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private Button startBtn, helpBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private LinearLayout animLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
		setContentView(R.layout.main);
		initView();
	}

	private void initView() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (Button) findViewById(R.id.startBtn);
		helpBtn = (Button) findViewById(R.id.helpBtn);
		startBtn.setOnClickListener(onClick);
		helpBtn.setOnClickListener(onClick);
		animLayout = (LinearLayout) findViewById(R.id.animLayout);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		rightLayout = (LinearLayout) findViewById(R.id.rightLayout);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			// 查询数据库，如果数据库的数据表中有电话号码就直接进入主界面，如果没有就弹出对话框进行输入号码
			case R.id.startBtn:
				Intent it = new Intent(TestWeiXinWhatsNewActivity.this,Login.class);
				startActivity(it);
				/*
				LayoutInflater inflater = LayoutInflater
						.from(TestWeiXinWhatsNewActivity.this);
				final View dlView = inflater.inflate(R.layout.dialogview, null);
				Dialog dialog = new AlertDialog.Builder(
						TestWeiXinWhatsNewActivity.this).setTitle("用于呼救短信接受")
						.setCancelable(false)
						.setPositiveButton("确定", new OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								phone_edit = (EditText) dlView
										.findViewById(R.id.phone_edit);
								// 获取电话号码
								chakan(phone_edit.getText().toString());
								phonenumber = phone_edit.getText().toString();
								gotomainView();
							}
						}).setView(dlView).create();
				dialog.show(); 
				 	以前的电话号输入框			*/
				break;
			case R.id.helpBtn:
				Intent intent = new Intent(TestWeiXinWhatsNewActivity.this,
						HelpActivity.class);
				TestWeiXinWhatsNewActivity.this.startActivity(intent);
			}
		}
	};

	// 创建进入主界面的方法
	private void gotomainView() {
		mScrollLayout.setVisibility(View.GONE);
		pointLLayout.setVisibility(View.GONE);
		animLayout.setVisibility(View.VISIBLE);
		mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
		Animation leftOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_left);
		Animation rightOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_right);
		// Animation leftOutAnimation =
		// AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.fadedout_to_left_down);
		// Animation rightOutAnimation =
		// AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.fadedout_to_right_down);
		leftLayout.setAnimation(leftOutAnimation);
		rightLayout.setAnimation(rightOutAnimation);
		leftOutAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mainRLayout.setBackgroundColor(R.color.bgColor);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				leftLayout.setVisibility(View.GONE);
				rightLayout.setVisibility(View.GONE);
				Intent intent = new Intent(TestWeiXinWhatsNewActivity.this,
						OtherOrTool.class);
				TestWeiXinWhatsNewActivity.this.startActivity(intent);
				TestWeiXinWhatsNewActivity.this.finish();
				overridePendingTransition(R.anim.zoom_out_enter,
						R.anim.zoom_out_exit);
			}
		});
	}

	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	/*
	// 联系人数据库
	public void chakan(String st) {
		if (st.equals(""))
			st = "abc";
		Editor sharedata = getSharedPreferences("yijianhujiu", 0).edit();
		sharedata.putString("name", st);
		sharedata.commit();

	}
	*/

}