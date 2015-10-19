package com.pro.tool;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.pro.phone.R;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.web.Web;

public class Tool_joke extends Activity{
		
	private GestureHelper gh;     //手势判定
	Voice voice = new Voice();
	Web web = new Web();
	TextToSpeech mSpeech;
	private int flag = 0; //当前的条数
	private String[] arr;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tool_joke);
		
		 gesture(); //手势
			arr=new String[]{"今天，我开车走在一段收费公路上。靠近一个收费亭的时候车子抛锚了。我只好在冒烟的车里等着，痛哭流涕，眼睁睁看着其他车子呼啸而过。直到一个巡警过来帮我把车子推过了收费站。收费站里的妇女跟我说她很同情我，可是仍然收了我3块钱。",
					         "一群蚂蚁爬上了大象的背，但被摇了下来，只有一只蚂蚁死死地抱着大象的脖子不放，下面的蚂蚁大叫：掐死他，掐死他，小样，还他妈反了！",
					         "一只小狗爬上你的餐桌，向一只烧鸡爬去，你大怒道：你敢对那只烧鸡怎样，我就敢对你怎样，结果小狗舔了一下鸡屁股，你昏倒，小狗乐道：小样看谁狠。",
					          "我花一毛钱发这条短信给你，是为了告诉你——我并不是一个一毛不拔的人。比如这一毛钱的短信就是我送你的生日礼物。",
					          "蚂蚁懒洋洋地躺在土里，伸出一只腿，朋友问你干嘛呢？蚂蚁：待会大象来了，绊他一跟头。"};
		 voice.voice(Tool_joke.this,"笑话娱乐界面");
	    	mSpeech = voice.mSpeech;
	}

	
	//锁死home，back按钮  不添加编辑按钮
    @Override
    public void onAttachedToWindow() {
              this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
            super.onAttachedToWindow();
            
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                    break;
            case KeyEvent.KEYCODE_HOME:
                    break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:        	
            	   break;
       	   // 音量大  电量
       	   case KeyEvent.KEYCODE_VOLUME_UP:
       		      break;
            }
            return false;
    }
	

	//手势
	 private void gesture() {
		 gh = new GestureHelper(this);     
	        gh.setOnFlingListener(new OnFlingListener() {  
	            @Override 
	            public void OnFlingLeft() {  
	            	mSpeech.stop();
	            	voice.voice(Tool_joke.this,"无效动作，如需帮助长按屏幕");
	            	mSpeech = voice.mSpeech;
	            }  
	               
	            @Override 
	            public void OnFlingRight() { 
	            	mSpeech.stop();
	            	voice.voice(Tool_joke.this,"退出");
	            	mSpeech = voice.mSpeech;
					Tool_joke.this.finish();
	            }

				@Override
				public void OnFlingUp() {
					if(flag==1||flag==0) voice.voice(Tool_joke.this, "请等待更新！");
					else{
						if(flag>4) flag=4;
						mSpeech.stop();
						voice.voice(Tool_joke.this,arr[flag]);
		            	mSpeech = voice.mSpeech;
						flag--;
//						web.GetData("http://122.207.182.59:8080/dog/servlet/selectjoke?id="+flag, Tool_joke.this);
					}
				}

				@Override
				public void OnFlingDown() {
					mSpeech.stop();
					if(flag<=4){
						if(flag<0) flag=0;
						voice.voice(Tool_joke.this,arr[flag]);
		            	mSpeech = voice.mSpeech;
		            	flag++;
					}else{
						voice.voice(Tool_joke.this,"当前已经没有更多笑话了。");
		            	mSpeech = voice.mSpeech;
					}
//					web.GetData("http://122.207.182.59:8080/dog/servlet/selectjoke?id="+flag, Tool_joke.this);
				
				}

				@Override
				public void OnFling() {
					mSpeech.stop();
	            	voice.voice(Tool_joke.this,"笑话娱乐界面");
	            	mSpeech = voice.mSpeech;
				}

				@Override
				public void OnFlingLong() {
					mSpeech.stop();
	            	voice.voice(Tool_joke.this,"上下滑动选择，上一条或者下一条笑话，向右滑动退出，其他滑动无效");
	            	mSpeech = voice.mSpeech;
				}  
	               
	        });
		
	}
	 
	 
	 @Override 
	    public boolean onTouchEvent(MotionEvent event) {  
	        return gh.onTouchEvent(event);  
	    }  
	 
}
