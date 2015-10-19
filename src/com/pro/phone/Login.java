package com.pro.phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	EditText username = null;
	EditText password = null;
	EditText phone_number = null;
	Button btn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		username = (EditText) super.findViewById(R.id.username);
		password = (EditText) super.findViewById(R.id.password);
		phone_number =(EditText) super.findViewById(R.id.phone_numer);
		username.getBackground().setAlpha(128);//设置半透明
		password.getBackground().setAlpha(128);
		phone_number.getBackground().setAlpha(128);
		btn = (Button) super.findViewById(R.id.login_button);
		btn.setOnClickListener(new Listener());
	}

	private class Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Editor sharedata = getSharedPreferences("yijianhujiu", 0).edit();
			sharedata.putString("name", phone_number.getText().toString());
			//存储一键联系人
			sharedata.commit();//提交更改
			
			Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
			// 显示登录信息
			Intent intent = new Intent(Login.this,OtherOrTool.class);
			startActivity(intent);
			Login.this.finish();
			overridePendingTransition(R.anim.zoom_out_enter,
					R.anim.zoom_out_exit);
		}
	}
}


