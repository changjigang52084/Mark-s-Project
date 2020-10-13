package com.lzkj.ui.fragment.view;

import java.util.concurrent.atomic.AtomicBoolean;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * Property animation方式滚动字幕（播放时间可控，播放速度可控可设置）
 * @author chenercai
 *
 */
public class TextAnimationView extends SurfaceView implements Callback, Runnable {
	
	private static final LogTag TAG = LogUtils.getLogTag(TextAnimationView.class.getSimpleName(), false);
	/** 1秒 */
	private static final long SECOND = 1000l;
	/** char数组每次添加几个字符 */
	private static final int CHAR_OFFSET = 1;
	
	private AtomicBoolean isRunning =  new AtomicBoolean(true);
	
	private Bitmap bitmap;
	private Canvas bitmapCanvas;
	private Canvas mCanvas;
	
	/** Area对象，包含View大小 */
	private Area area;
	/** View宽 */
	private int viewWidth;
	/** drawText Y坐标 */
	private float drawTextY;
	/** 文本字体大小 */
	private float textSize;
	/** 待显示字符串 */
	private String text;
	/** 背景色 */
	private int backgroundColor;
	/** 字符移动速度，移动步长 */
	private int textSpeed;
	/** draw text paint*/
	private Paint paint;
	/** 动画切换更新值，默认从0到measure text的长度 */
	private float value = 0;
	/** string toCharArray后的char数组 */
	private char[] total;
	/** 当前drawText的X坐标 */
	private float currentX;
	/** 计算drawText的X坐标所用的offset值 */
	private float currentXoffset;
	/** paint measure text的文本总宽度 */
	private float totalWidth;
	/** 当前字体大小的空格字符宽度（空格字符占位最少） */
	private float perCharWidth;
	/** 当前drawText的char数组长度（from index to count） */
	private int arrayLength;
	/** paint measure 当前char数组的宽度 */
	private float currentArrayWidth;
	/** paint measure 当前char数组的头char数组宽度 */
	private float headArrayWidthOffset;
	/** 当前显示的（from 0开始）char数组宽度 */
	private float currentTotalArrayWidth;
	/** 文本滚动时间 */
	private long duration;
	/** draw text char数组from */
	private int index;
	/** draw text char数组count */
	private int count;
	/** 过度动画切换类 */
	private ValueAnimator anim;
	
	private TextRoundListener listener;
	
	private HandlerThread thread;
	private Handler handler;
	private SurfaceHolder mHolder;

	public TextAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextAnimationView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		LogUtils.d(TAG, "init", "init thread");
		mHolder = getHolder();
		mHolder.addCallback(this);
		// 支持滚动字幕透明
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
	}
	
	/**
	 * 设置字体颜色
	 * @param textColor 字体颜色
	 */
	public void setTextColor(int textColor) {
		if (paint == null) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		
		paint.setColor(textColor);
	}
	
	/**
	 * 设置字体大小
	 * @param value 字体大小 float 值
	 */
	public void setTextSize(float value) {
		if (paint == null) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		paint.setTextSize(value);
		textSize = value;
	}
	
	/**
	 * 设置背景色
	 * @param color 背景色 int 值
	 */
	public void setBackgroundColor(int color) {
		this.backgroundColor = color;
	}
	
	/**
	 * 设置字符串
	 * @param text String字符串
	 */
	public void setText(String text) {
		this.text = text;
		LogUtils.d(TAG, "setText", text);
	}
	
	/**
	 * 设置背景透明度
	 */
	public void setAlpha(final float alpha) {
		post(new Runnable() {

			@Override
			public void run() {
				TextAnimationView.super.setAlpha(alpha);
			}
		});
	}

	private void updateOnBitmap() {
		if (bitmapCanvas != null) {
			bitmap.eraseColor(backgroundColor);
//			if (backgroundColor == Color.TRANSPARENT) {
////				canvas.drawColor(backgroundColor, Mode.CLEAR);
//			} else {
//				bitmapCanvas.drawColor(backgroundColor);
//			}
			
			try {
				if (total != null && index < total.length && index + count <= total.length && paint != null) {
					bitmapCanvas.drawText(total, index, count, currentX, drawTextY, paint);
				}	
			} catch (Exception e) {
				LogUtils.e(TAG, "updateOnBitmap", e);
			}
		}
		
		draw();
	}
	
	/**
	 * 设置Area对象
	 * @param area Area实例
	 */
	public void setArea(Area area) {
		this.area = area;
		if (area != null) {
			viewWidth = area.getW();
		}
	}
	
	protected void draw() {
//		if (backgroundColor == Color.TRANSPARENT) {
//			canvas.drawColor(backgroundColor, Mode.CLEAR);
//		} else {
//			canvas.drawColor(backgroundColor);
//		}
//		
//		try {
//			if (total != null && index < total.length && index + count <= total.length && paint != null) {
//				canvas.drawText(total, index, count, currentX, drawTextY, paint);
//			}	
//		} catch (Exception e) {
//			LogUtils.e(TAG, "onDraw", e);
//		}
		try {			
			mCanvas = mHolder.lockCanvas();

			synchronized (mHolder) {
				try {
					if (mCanvas != null) {
						mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
						if (bitmap != null && !bitmap.isRecycled()) {
//							mCanvas.drawBitmap(bitmap, 0, 0, null);
							RectF src = new RectF(0.5f,0.5f,viewWidth - 0.501f,area.getH() - 0.501f);						
							mCanvas.drawBitmap(bitmap, null, src, null);
						}
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		
	}
	
	/**
	 * 设置字幕滚动速度
	 * @param textSpeed
	 */
	public void setTextSpeed(int textSpeed) {
		this.textSpeed = textSpeed;
	}
	
	long lStart;
	long lEnd;
	/**
	 * 开始滚动
	 */
	public void startScrolling() {

		if (paint == null) {
			LogUtils.e(TAG, "startScrolling", "paint cannot be null!");
			return;
		}
		LogUtils.i(TAG, "startScrolling", "char  : " + paint.measureText(" "));
		LogUtils.i(TAG, "startScrolling", "char 1: " + paint.measureText("1"));
		LogUtils.i(TAG, "startScrolling", "char a: " + paint.measureText("a"));
		LogUtils.i(TAG, "startScrolling", "char A: " + paint.measureText("A"));
		LogUtils.i(TAG, "startScrolling", "char .: " + paint.measureText("."));
		LogUtils.i(TAG, "startScrolling", "char |: " + paint.measureText("|"));
		LogUtils.i(TAG, "startScrolling", "char 。: " + paint.measureText("。"));
		LogUtils.i(TAG, "startScrolling", "char 国: " + paint.measureText("国"));
		
		long l1 = SystemClock.uptimeMillis();
		
		if (TextUtils.isEmpty(text)) {
			LogUtils.e(TAG, "startScrolling", "text cannot be null!");
			return;
		}
		
		if (area == null) {
			LogUtils.e(TAG, "startScrolling", "Area cannot be null!");
			return;
		}
		
		try {
			drawTextY = textSize;
			total = text.trim().toCharArray();
			totalWidth = paint.measureText(text) + viewWidth;
			perCharWidth = paint.measureText(" ");
			// 每一段数组包含空格字符数
			arrayLength = (int) (viewWidth / perCharWidth);
			// 每秒移动3个空格位置所需要的时间，单位秒
			duration = (long) (totalWidth / (perCharWidth * textSpeed));

//			LogUtils.d(TAG, "startScrolling", "(" + area.getX() + ", " + area.getY() + ", " + area.getWidth() + ", " + area.getHeight() + "), " + textSize + ", " + textSpeed);
			LogUtils.d(TAG, "startScrolling", "per char width: " + perCharWidth);
			LogUtils.d(TAG, "startScrolling", "char array length: " + arrayLength + ", total char array length: " + total.length);
			LogUtils.d(TAG, "startScrolling", "text width: " + (totalWidth - viewWidth) + ", total width: " + totalWidth);
			LogUtils.d(TAG, "startScrolling", "duration: " + duration + " s");
		} catch (Exception e) {
			LogUtils.e(TAG, "startScrolling", e);
		}
		
		long l2 = SystemClock.uptimeMillis();
		LogUtils.w(TAG, "startScrolling", l2 - l1 + " ms--measure text");

		if (anim == null) {
			anim = ObjectAnimator.ofFloat(0, totalWidth);
			
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					lEnd = System.currentTimeMillis();
					LogUtils.w(TAG, "onAnimationEnd", "----------------------- " + (lEnd - lStart));
					if (listener != null && anim.isRunning() && isRunning.get()) {
						if (isRunning.compareAndSet(true, false)) {
							listener.textRoundFinished();
						}
					}
				}
				
				@Override
				public void onAnimationStart(Animator animation) {
					super.onAnimationStart(animation);
					LogUtils.w(TAG, "onAnimationStart", "----------------------- " + animation.getDuration());
					lStart =  System.currentTimeMillis();
					try {
						// index 从0开始
						index = 0;
						// count 从CHAR_OFFSET开始，每次添加字符数组单位也是CHAR_OFFSET
						count = CHAR_OFFSET;
						// 从右往左滑，从View右边开始，即 VIEW_WIDTH
						currentX = viewWidth;
						currentXoffset = 0;
						// 初始化第一个char数组长度
						// 初始化当前需要展示char数组长度
						// 初始化当前已显示（从0开始）char数组长度
						headArrayWidthOffset = currentTotalArrayWidth = currentArrayWidth = paint.measureText(total, index, count);
						LogUtils.d(TAG, "", "current array width: " + currentArrayWidth);
					} catch (Exception e) {
						LogUtils.e(TAG, "onAnimationStart", e);
					}

				}
			});
			anim.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					if (!isRunning.get()) {
						return;
					}
					
					long l1 = SystemClock.uptimeMillis();
					try {
						// 当前Animation值
						value = (Float) animation.getAnimatedValue();
			
						// 计算 drawText 的 x 坐标，currentXoffset值初始化为0
						// View宽度 - 当前Animation Value值 + offset
						currentX = viewWidth - value + currentXoffset;
						
						// 当前 value 大于 当前已显示char数组宽度，该往char数组后面添char了
						if (value > currentTotalArrayWidth) {
							// 往后添加CHAR_OFFSET个char
							count += CHAR_OFFSET;
							
							// 到char数组末尾
							if (index + count > total.length) {
								count = total.length - index;
								// 重新计算当前显示（from 0）char数组的宽度
								currentTotalArrayWidth = paint.measureText(total, 0, index + count) + viewWidth;
							} else {
								// 重新计算当前显示（from 0）char数组的宽度
								currentTotalArrayWidth = paint.measureText(total, 0, index + count);
							}
							// 重新计算当前显示（from index，不是0）char数组的宽度
							currentArrayWidth = paint.measureText(total, index, count);
						
							LogUtils.e(TAG, "onAnimationUpdate", "update count: " + count + ", currentArrayWidth: " + currentArrayWidth + ", currentTotalArrayWidth: " + currentTotalArrayWidth);
						}
						
						float offsetLeft = headArrayWidthOffset + currentX;
						// offsetLeft 小于0，表示当前currentX在0左边，而且大于headArrayWidthOffset，第一个char数组已经全播放完
						if (offsetLeft <= 0) {
							int offset = CHAR_OFFSET;
							// index 往前移CHAR_OFFSET
							index += CHAR_OFFSET;
							
							// 到char数组末尾
							if (index >= total.length ) {
								index -= CHAR_OFFSET;
							}
							
							if (index + count >= total.length) {
								count = total.length - index;
								if (count <= offset) {
									offset = count;
								}
							}
							
							// currentXoffset 往前移headArrayWidthOffset
							currentXoffset += headArrayWidthOffset;
							currentX = viewWidth - value + currentXoffset;
							
							// 重新计算当前显示（from index，不是0）char数组的宽度
							currentArrayWidth = paint.measureText(total, index, count);
							// 重新计算当前显示（from 0）char数组的宽度
							currentTotalArrayWidth = paint.measureText(total, 0, index + count);
							LogUtils.e(TAG, "onAnimationUpdate", "update index: " + index + ", count: " + count + ", offset:, " + offset + ", total length: " + total.length);

							headArrayWidthOffset = paint.measureText(total, index, offset);
							LogUtils.e(TAG, "onAnimationUpdate", "update index: " + index + ", count: " + count + ", currentXoffset: " + currentXoffset + ", headArrayWidthOffset: " + headArrayWidthOffset);
						}
						long l2 = SystemClock.uptimeMillis();
						LogUtils.v(TAG, "onAnimationUpdate", "index: " + index + ", count: " + count + ", currentX: " + currentX + ", value: " + value + ", arrayWidth: " + currentArrayWidth + ", " + ", currentTotalArrayWidth: " + currentTotalArrayWidth + ", " + (l2 - l1) + " ms");
//						invalidate();		
						updateOnBitmap();

					} catch (Exception e) {
						LogUtils.e(TAG, "onAnimationUpdate", e);
					}
				}
			});
		} else {
			if (anim.isRunning()) {
				anim.cancel();
			}
			anim.setFloatValues(0, totalWidth);
		}
		
		anim.setDuration(duration * SECOND * ConfigSettings.ANIMATION_COMPATIBLE_OFFSET);
		anim.setInterpolator(new android.view.animation.LinearInterpolator());

		isRunning.set(true);
		LogUtils.d(TAG, "startScrolling", "begin----------------");
		if (handler != null) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					if (anim != null) {
						anim.start();
					}
				}
			});
		}

	}
	
	public long getDuration() {
		return duration * SECOND;
	}

	/**
	 * 停止滚动
	 */
	public void stopScrolling() {
		if (handler != null) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					if (anim != null && anim.isRunning()) {
						isRunning.set(false);
						anim.cancel();
						anim = null;
					}
				}
			});
		}
	}
	
	/**
	 * 设置回调
	 * @param listener 回调接口
	 */
	public void setTextRoundListener(TextRoundListener listener) {
		this.listener = listener;
	}
	
	public Bitmap getScreenshot() {
		return bitmap;
	}
	
	public interface TextRoundListener {
		/**
		 * This method will be called when a text has completed a full cycle of
		 * the text area.
		 */
		public void textRoundFinished();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtils.d(TAG, "surfaceCreated", "-----------------------");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		LogUtils.d(TAG, "surfaceChanged", "-----------------------");
		bitmap = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		bitmapCanvas = new Canvas(bitmap);	
		thread = new HandlerThread(TextAnimationView.class.getSimpleName());
		thread.start();
		handler = new Handler(thread.getLooper());
		
		if (isRunning.get() && anim != null && !anim.isRunning()) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (anim != null) {
						anim.start();
					}
				}
			});
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtils.d(TAG, "surfaceDestroyed", "-----------------------");
		stopScrolling();

		isRunning.set(false);

		if (thread != null && thread.isAlive()) {
			thread.quit();
			handler = null;
			thread = null;

		}
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmapCanvas = null;
		}
		total = null;
		text = null;
		paint = null;
	}

	@Override
	public void run() {
		LogUtils.d(TAG, "run", "start scrolling1? " + isRunning.get());

		if (Looper.myLooper() == null) {
			Looper.prepare();
			handler = new Handler();
			Looper.loop();
		} else {
			handler = new Handler();
		}
		
		LogUtils.d(TAG, "run", "start scrolling2? " + isRunning.get());

		if (isRunning.get() && anim != null && !anim.isRunning()) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					if (anim != null) {
						anim.start();
					}
				}
			});
		}
	}

}
