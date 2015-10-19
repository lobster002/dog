package com.pro.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.pro.phone.R;

//对语音进行封装
public class Voiceshibie {
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog iatDialog;
	private shibiefrist voicedis;
	private SharedPreferences mSharedPreferences;
	@SuppressLint("ShowToast")
	public Activity ac;

	/*
	 * 对语音进行封装 进入一段语音，通过抽象函数得到语音的文本信息
	 */

	public static abstract class shibiefrist {
		public abstract void zhixing(String ss);
	}

	public Voiceshibie(Activity act) {
		ac = act;
		MediaPlayer mediaPlayer01 = new MediaPlayer();
		mediaPlayer01 = MediaPlayer.create(act, R.raw.music);
		mediaPlayer01.start();

		try {
			Thread.sleep(1300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer01.stop();
		mediaPlayer01.release();
		// 初始化识别对象
		mIat = SpeechRecognizer.createRecognizer(ac, mInitListener);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog = new RecognizerDialog(ac, mInitListener);
		mSharedPreferences = ac.getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		setParam();
		iatDialog.setListener(recognizerDialogListener);
		iatDialog.show();
	}

	public void setVoiceListener(shibiefrist listener) {
		voicedis = listener;
	}

	// 听写UI监听器

	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			voicedis.zhixing(text);
		}

		// 识别回调错误
		public void onError(SpeechError error) {
		}

	};

	// 初始化监听器

	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code == ErrorCode.SUCCESS) {
				// findViewById(R.id.iat_recognize).setEnabled(true);
			}
		}
	};

	// 参数设置

	@SuppressLint("SdCardPath")
	public void setParam() {
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		// mIat.setParameter(SpeechConstant.VAD_EOS,
		// mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		// 设置标点符号
		// mIat.setParameter(SpeechConstant.ASR_PTT,
		// mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				"/sdcard/iflytek/wavaudio.pcm");
	}

	/*
	 * 调用示例
	 * 
	 * import com.example.voice.Voiceshibie.shibiefrist;
	 * 
	 * private Voiceshibie vv;
	 * 
	 * private void vvv(){ vv = new Voiceshibie(this); vv.setVoiceListener(new
	 * shibiefrist() {
	 * 
	 * @Override public void zhixing(String ss) { voice.voice(MainActivity.this,
	 * ss); GetWeather(); } });
	 * 
	 * }
	 */
}
