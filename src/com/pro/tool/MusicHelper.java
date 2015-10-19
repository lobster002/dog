package com.pro.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pro.voice.Voice;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class MusicHelper {
	public static final String LOG_TAG = "MusicHelper";
	private Context mContext;

	/* 播放器 */
	public MediaPlayer mMediaPlayer;

	/* 音乐播放列表 */
	public List<String> mMusicList;

	/* 当前播放项目 */
	public int currentPlayingItem = 0;

	/* 当前播放位置 */
	public long currentPosition = 0;

	public boolean isPlayingNext = false;

	/* 是否暂停 */
	public boolean isPauseed = false;

	/* 播放路径 */
	private static final String MEDIA_PATH = new String("/sdcard/music");

	private Voice voice = new Voice();
	TextToSpeech mSpeech; // 语音辅佐

	public MusicHelper(Context context) {
		this.mContext = context;
		mMediaPlayer = new MediaPlayer();
		mMusicList = new ArrayList<String>();
		getAllMusicFiles(MEDIA_PATH);
	}

	/**
	 * 根据名称播放音乐
	 * 
	 * @param pathname
	 *            the directory path
	 * @return List the array of all files
	 * */
	public List<String> getAllMusicFiles(String pathname) {
		if (pathname == null || pathname.length() < 1) {
			System.out
					.println("FileHelper.getAllMusicFiles, inavailable path when retrive a path");
			return null;
		}
		File mFile = new File(pathname);
		if (mFile.exists()) {
			if (mFile.isDirectory()) {
				File[] files = mFile.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.isDirectory()) {
							getAllMusicFiles(file.getPath()); // 锟捷癸拷锟斤拷锟斤拷
						} else {
							if (file.getAbsolutePath().endsWith(".mp3")) {
								mMusicList.add(file.getAbsolutePath());
							}
						}
					}
				} else {
					System.out
							.println("there are no any files under the path.");
				}
			} else {
				System.out.println("not a directory.");
			}
		} else {
			System.out.println("file not exists...");
		}
		return mMusicList;
	}

	public void nameplayMusic(String name) {
		int i = 0, j = -1;
		if (mMusicList == null || mMusicList.size() < 1) {
			Toast.makeText(mContext, "sd锟斤拷锟斤拷没锟叫革拷锟斤拷", Toast.LENGTH_SHORT)
					.show();

		} else {
			/**
			 * for(;i<mMusicList.size();i++){ String mname=mMusicList.get(i);
			 * mname = mname.substring(mname.lastIndexOf("/") + 1,
			 * mname.lastIndexOf("."));
			 * if(name.equals(mname)){//锟叫断革拷锟斤拷锟斤拷锟斤拷应锟斤拷锟斤拷锟斤拷锟角凤拷锟斤拷锟� break;
			 * } }
			 */
			while (i < mMusicList.size()) {
				String mname = mMusicList.get(i);
				mname = mname.substring(mname.lastIndexOf("/") + 1,
						mname.lastIndexOf("."));
				if (name.equals(mname)) {
					j = i;
					i = mMusicList.size();
				} else {
					i++;
				}
			}
			if (j >= 0) {// 锟斤拷锟斤拷锟斤拷
				currentPlayingItem = j;
				try {// 锟斤拷始锟斤拷锟斤拷
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(mMusicList
							.get(currentPlayingItem));
					mMediaPlayer.prepare();

					mMediaPlayer.start();

					// 锟皆讹拷锟斤拷锟斤拷锟斤拷一锟斤拷
					mMediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									isPlayingNext = true;
									isPauseed = false;
									nextMusic();
									Intent intent = new Intent(
											"com.unistrong.uniteqlauncher.AUTO_PLAY_NEXT_MUSIC");
									mContext.sendBroadcast(intent);// 锟斤拷锟酵广播通知锟斤拷锟斤拷UI锟斤拷锟斤拷
								}
							});
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(mContext, "锟斤拷锟斤拷锟街诧拷锟斤拷锟斤拷", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	/* 播放音乐 */
	public boolean playMusic() {
		Log.d(LOG_TAG, "playMusic()-->currentPlayingItem=" + currentPlayingItem);
		if (mMusicList == null || mMusicList.size() < 1) {
			Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
			return false;
		}
		try {
			if (!isPauseed) {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(mMusicList.get(currentPlayingItem));
				mMediaPlayer.prepare();
			}
			mMediaPlayer.start();

			// 锟皆讹拷锟斤拷锟斤拷锟斤拷一锟斤拷
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					isPlayingNext = true;
					isPauseed = false;
					nextMusic();
					Intent intent = new Intent(
							"com.unistrong.uniteqlauncher.AUTO_PLAY_NEXT_MUSIC");
					mContext.sendBroadcast(intent);// 锟斤拷锟酵广播通知锟斤拷锟斤拷UI锟斤拷锟斤拷
				}
			});
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/* 锟斤拷一锟斤拷 */
	public void prevMusic() {
		Log.d(LOG_TAG, "prevMusic()-->currentPlayingItem=" + currentPlayingItem);
		isPauseed = false;
		if (mMusicList == null || mMusicList.size() < 1)
			return;
		// 播放下一曲
		if (currentPlayingItem-- <= 0) {
			currentPlayingItem = mMusicList.size() - 1;
		}
		// mSpeech.stop();
		voice.voice(mContext, "当前歌曲为：" + getMusicName());
		mSpeech = voice.mSpeech;
		// 锟斤拷锟斤拷遣锟斤拷锟阶刺�锟斤拷锟皆讹拷锟斤拷锟斤拷锟斤拷一锟斤拷
		if (mMediaPlayer.isPlaying()) {
			playMusic();
		} else if (isPlayingNext) {
			playMusic();
		}
	}

	public void nextMusic() {
		Log.d(LOG_TAG, "nextMusic()-->currentPlayingItem=" + currentPlayingItem);
		isPauseed = false;
		if (mMusicList == null || mMusicList.size() < 1)
			return;
		if (currentPlayingItem++ >= mMusicList.size() - 1) {
			currentPlayingItem = 0;
		}

		// mSpeech.stop();
		voice.voice(mContext, "当前歌曲为：" + getMusicName());
		mSpeech = voice.mSpeech;

		if (mMediaPlayer.isPlaying()) {
			playMusic();
		} else if (isPlayingNext) {
			playMusic();
			isPlayingNext = false;
		}
	}

	public boolean pause() {
		isPauseed = true;
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
		} else {
			mMediaPlayer.start();
		}
		return false;
	}

	public void stop() {
		mMediaPlayer.reset();
	}

	public void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}

	}

	public boolean isPlaying() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}

	/* 锟斤拷取锟斤拷前锟斤拷锟脚碉拷位锟斤拷,锟窖诧拷锟脚的筹拷锟斤拷 */
	public long getCurrentPosition() {
		if (mMediaPlayer != null) {
			Log.d(LOG_TAG,
					"currentPosition=" + mMediaPlayer.getCurrentPosition());
			currentPosition = mMediaPlayer.getCurrentPosition();
			return currentPosition;
		}
		return -1;
	}

	/* 锟斤拷位锟斤拷锟斤拷锟斤拷位锟斤拷,锟斤拷锟节匡拷锟斤拷锟斤拷锟� */
	public void mseekTo(int positon) {
		// long position = getCurrentPosition();
		mMediaPlayer.seekTo(positon);
	}

	public String getMusicName() {
		if (mMusicList == null || mMusicList.size() < 1)
			return "";
		String name = "";
		if (mMusicList != null) {
			name = mMusicList.get(currentPlayingItem);
			name = name.substring(name.lastIndexOf("/") + 1,
					name.lastIndexOf("."));
		}
		return name;
	}

}
