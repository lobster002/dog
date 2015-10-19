package com.pro.web;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.pro.voice.Voice;

public class Web {

	private Voice voice = new Voice();
	Activity act;

	public void GetData(String ss, Activity act) {
		this.act = act;
		connServerForResult(ss);
	}

	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			String va = data.getString("Result");
			// 使用json解析
			parseJson(va);
		}
	};

	// 获取数据
	private void connServerForResult(final String strUrl) {
		// 重新开线程 获取网络数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// HttpGet对象
					HttpGet httpRequest = new HttpGet(strUrl);
					// HttpClient对象
					HttpClient httpClient = new DefaultHttpClient();
					// 获得HttpResponse对象
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// 取得返回的数据
						String Result = EntityUtils.toString(httpResponse
								.getEntity());
						// 延迟等待 用handler
						Message msg = new Message();
						Bundle date = new Bundle();
						date.putString("Result", Result);
						msg.setData(date);
						msg.what = 0;
						handle.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	// 解析数据

	// 一个Json数据解析
	private void parseJson(String strResult) {
		try {
			System.out.println(strResult);
			JSONObject jsonObj = new JSONObject(strResult);
			String ss = jsonObj.getString("cont");
			voice.voice(act, ss);
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}
}
