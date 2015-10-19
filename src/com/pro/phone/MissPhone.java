package com.pro.phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;


public class MissPhone extends Activity {

	private TextToSpeech mSpeech;
	private Voice voice = new Voice();
	
	private String[] mess,phone;
	private GestureHelper gh;     //手势判定
	
	private int phonesum;
	private int messsum;
	private String strnumber="";
	
    protected void onCreate(Bundle savedInstanceState) {
    	 zhixing();
		super.onCreate(savedInstanceState);	
		//取消状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		                  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//取消标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.missphone);
  
        String read = "";
        if(phone.length>1) read += phone.length-1+"个未读电话,";
        if(mess.length>1) read += mess.length-1+"条未读短信,";
       
        read+="上下滑动，查阅相关内容";
        
        voice.voice(MissPhone.this, read);
        mSpeech = voice.mSpeech;
        
        gesture(); //手势
        phonesum = 1;
        messsum = 1;
     	}
   /* public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			unsolve_img=(ImageView)findViewById(R.id.unsolve_img);
			unsolve_img.setBackgroundResource(R.anim.unsolve_anim);
			unsolve_anim=(AnimationDrawable)  unsolve_img.getBackground();
			unsolve_anim.start();
			
		}
	}*/
     		
    
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
            }
            return false;
    }
	
   
  //手势
  	 private void gesture() {
  		 gh = new GestureHelper(this);     
  	        gh.setOnFlingListener(new OnFlingListener() {  
  	            @Override 
  	            public void OnFlingLeft() { //左边
  	            	mSpeech.stop();
  	            	if(!strnumber.equals("")){
  	            		call(strnumber);
  	            	}
  	            }  
  	               
  	            @Override 
  	            public void OnFlingRight() {  //右边
  	            	mSpeech.stop();
  	            	voice.voice(MissPhone.this, "无效动作，如需帮助，请长按屏幕。");
  	    			mSpeech = voice.mSpeech;
  					   
  	            }

  				@Override
  				public void OnFlingUp() { //上
  					mSpeech.stop();
  					read();
  					
  				}

  				

				@Override
  				public void OnFlingDown() { //下
  					mSpeech.stop();
  					read();
  				}

  				@Override
  				public void OnFling() {
  					mSpeech.stop();
  					voice.voice(MissPhone.this, "未接电话，未读短信处理界面");
  	    			mSpeech = voice.mSpeech;
  				}

				@Override
				public void OnFlingLong() {
					mSpeech.stop();
  					voice.voice(MissPhone.this, "上下滑动，处理下一条，向左滑动回拨当前联系人，处理完毕，自动跳转至主页面");
  	    			mSpeech = voice.mSpeech;
				}  
  	               
  	        });
  		
  	}
  	 

  	 
  	 
  	 @Override 
  	    public boolean onTouchEvent(MotionEvent event) {  
  	        return gh.onTouchEvent(event);  
  	    }  

    
    
  	//拨打电话
		private void  call(String Phonenumber ){	
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + Phonenumber));
	        startActivity(myIntentDial);
	       Intent intent =new Intent(MissPhone.this,OtherOrTool.class);
			startActivity(intent);
		}
		
  	 
  	 //读
  	private void read() {
  		
  		if(phone.length > phonesum){
  			String  readstr ="";
  			readstr += "第"+phonesum+"个未接电话，，";
  			readstr +="于"+phone[phonesum].split(",")[2];
  			readstr += "，来自"+phone[phonesum].split(",")[0];
  			readstr += "回拨请向左滑动";
  			voice.voice(MissPhone.this, readstr);
  			mSpeech = voice.mSpeech;
  			strnumber = phone[phonesum].split(",")[1];
  			
  			phonesum++;
  		}else if(mess.length > messsum){
  			String  readstr ="";
  			readstr += "第"+messsum+"条未读短信，，";
  			readstr +="于"+mess[messsum].split(",")[2];
  			readstr += "，来自"+mess[messsum].split(",")[0];
  			readstr += "，内容为,"+mess[messsum].split(",")[3];
  			readstr += ",回拨请向左滑动";
  			voice.voice(MissPhone.this, readstr);
  			mSpeech = voice.mSpeech;
  			strnumber = mess[messsum].split(",")[1];
  			
  			messsum++;
  		}
  		else{
  			mSpeech.stop();
  			Intent intent = new Intent(MissPhone.this,OtherOrTool.class);
  			startActivity(intent);
  		}
		
	}
  	 
  	 
    
    //获取短信和电话数组
    private void zhixing(){
    	SharedPreferences sharedata = getSharedPreferences("miss", 0);
		String strmess = sharedata.getString("missmessages", null);
		String strphone = sharedata.getString("missphone", null);

		System.out.println(strphone);
		mess = strmess.split("%");
		phone = strphone.split("%");
		
		Editor sharedata1 = getSharedPreferences("miss", 0).edit();  
		sharedata1.putString("missmessages","");
		sharedata1.putString("missphone","");
		sharedata1.commit(); 
		
    }
    
    
}
