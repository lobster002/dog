package com.pro.voice;

import java.util.Locale;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

//对语音部分进行封装
public class Voice {

	public TextToSpeech mSpeech;

	// 语音输出
	public void voice(Context mContext, final String text) {

		mSpeech = new TextToSpeech(mContext, new OnInitListener() {

			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int result = mSpeech.setLanguage(Locale.CHINESE);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
					} else {
						mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
		});

	}

}
