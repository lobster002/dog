package com.pro.touch;

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
	        public abstract void OnFlingLong();
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
	       
	 // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发 
	    @Override 
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
	        // 触发条件 ：  
	        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒  
	        final int FLING_MIN_DISTANCE = (int) (screen_width / 20.0f), FLING_MIN_VELOCITY = 20; 
	        float dx = e2.getX()-e1.getX();
	        float dy = e2.getY()-e1.getY();

	  			if(Math.abs(dx)>Math.abs(dy)){
	  				if(dx<=0){
	  					listener_onfling.OnFlingLeft(); 
	  				}else{
	  					listener_onfling.OnFlingRight();
	  				}
	  			}
	  			if(Math.abs(dx)<Math.abs(dy)){
	  				if(dy>=0){
	  					listener_onfling.OnFlingDown();
	  				}else{
	  					listener_onfling.OnFlingUp();
	  				}
	  			}
	  		
	       /* if(e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
	        	listener_onfling.OnFlingUp();
	        }else if(e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
	        	listener_onfling.OnFlingDown();
	        }else if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            listener_onfling.OnFlingLeft();  
	        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {  
	            listener_onfling.OnFlingRight();  
	        }
           */
	        return true;  
	    }  
	       
	 // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发   
	    @Override 
	    public boolean onDown(MotionEvent e) {  
	        return false;  
	    }  
	       
	    
	 // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	    @Override 
	    public void onLongPress(MotionEvent e) {  
	    	listener_onfling.OnFlingLong();
	    }  
	       
	 // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发  
	    @Override 
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
	            float distanceY) {  
	        return false;  
	    }  
	       
	    @Override 
	    public void onShowPress(MotionEvent e) {  
	    }  
	       
	 // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发   
	    @Override 
	    public boolean onSingleTapUp(MotionEvent e) {  
	    	listener_onfling.OnFling();
	        return false;  
	    }  
	    
	    
	    
	  
	        
	       
	    
}

