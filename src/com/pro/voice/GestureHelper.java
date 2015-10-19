package com.pro.voice;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

//对手势部分进行封装
public class GestureHelper implements OnGestureListener {  
	    private GestureDetector gesture_detector;  
	    private int screen_width;  
	    private OnFlingListener listener_onfling;  
	       
	    public static abstract class OnFlingListener {  
	        public abstract void OnFlingLeft();  
	        public abstract void OnFlingRight(); 
	        public abstract void OnFlingUp();  
	        public abstract void OnFlingDown();
	        public abstract void OnFling();
	    }  
	       
	    public GestureHelper(Context context) {  
	        DisplayMetrics dm = context.getResources().getDisplayMetrics();  
	        screen_width = dm.widthPixels;  
	           
	        gesture_detector = new GestureDetector(context, this);  
	    }  
	       
	    public void setOnFlingListener(OnFlingListener listener) {  
	        listener_onfling = listener;  
	    }  
	       
	    public boolean onTouchEvent(MotionEvent event) {  
	        return gesture_detector.onTouchEvent(event);  
	    }  
	       
	    @Override 
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
	        // 触发条件 ：  
	        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒  
	        final int FLING_MIN_DISTANCE = (int) (screen_width / 10.0f), FLING_MIN_VELOCITY = 50; 
	        
	        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            listener_onfling.OnFlingLeft();  
	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            listener_onfling.OnFlingRight();  
	        }else if(e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
	        	listener_onfling.OnFlingUp();
	        }else if(e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
	        	listener_onfling.OnFlingDown();
	        }
	        else{
	        	listener_onfling.OnFling();
	        }
	   
	        return true;  
	    }  
	       
	    @Override 
	    public boolean onDown(MotionEvent e) {  
	        return false;  
	    }  
	       
	    @Override 
	    public void onLongPress(MotionEvent e) {  
	    }  
	       
	    @Override 
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
	            float distanceY) {  
	        return false;  
	    }  
	       
	    @Override 
	    public void onShowPress(MotionEvent e) {  
	    }  
	       
	    @Override 
	    public boolean onSingleTapUp(MotionEvent e) {  
	        return false;  
	    }  
	}

