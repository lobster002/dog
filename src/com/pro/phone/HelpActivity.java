package com.pro.phone;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HelpActivity extends Activity implements OnViewChangeListener {
	private MyScrollLayout helpScrollLayout;
	private ImageView[] img;
	private int count1;
	private int currentItem1;
	private Button backBtn;
	private LinearLayout helpLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_help);
		initlayout();
	}
	private void initlayout() {
		helpScrollLayout  = (MyScrollLayout) findViewById(R.id.helpScrollL);
		helpLayout= (LinearLayout) findViewById(R.id.helplayout);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(onClickListener);
		count1 = helpScrollLayout.getChildCount();
		img = new ImageView[count1];
		for(int i = 0; i< count1;i++) {
			img[i] = (ImageView)helpLayout.getChildAt(i);
			img[i].setEnabled(true);
			img[i].setTag(i);
		}
		currentItem1 = 0;
		img[currentItem1].setEnabled(false);
		helpScrollLayout.SetOnViewChangeListener(this);
	}
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.backBtn:
				HelpActivity.this.finish();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if(position < 0 || position > count1 -1 || currentItem1== position) {
			return;
		}
		img[currentItem1].setEnabled(true);
		img[position].setEnabled(false);
		currentItem1 = position;
	}

}
