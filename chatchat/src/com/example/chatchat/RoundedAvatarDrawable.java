package com.example.chatchat;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class RoundedAvatarDrawable extends Drawable {
	 private final Bitmap mBitmap;
	 private final Paint mPaint;
	 private final RectF mRectF;
	 private final int mBitmapWidth;
	 private final int mBitmapHeight;

	 public RoundedAvatarDrawable(Bitmap bitmap) {
	  mBitmap = bitmap;
	  mRectF = new RectF();
	  mPaint = new Paint();
	  mPaint.setAntiAlias(true); // 곡선을 부드럽게 처리해주는 옵션.
	  mPaint.setDither(true); //이미지보다 장비의 표현력이 떨어질때 이미지의 색상을 낮추어 출력하는 기법.
	  final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
	  mPaint.setShader(shader);

	  mBitmapWidth = mBitmap.getWidth();
	  mBitmapHeight = mBitmap.getHeight();
	 }

	 @Override
	   public void draw(Canvas canvas) {
	  canvas.drawOval(mRectF, mPaint); //둥글게
	  //canvas.drawRoundRect(mRectF, 20, 20, mPaint); //라운딩네모
	   }
	 
	   @Override
	   protected void onBoundsChange(Rect bounds) {
	     super.onBoundsChange(bounds);
	 
	     mRectF.set(bounds);
	   }
	 
	   
	   @Override
	   public void setAlpha(int alpha) {
	     if (mPaint.getAlpha() != alpha) {
	       mPaint.setAlpha(alpha);
	       invalidateSelf();
	     }
	   }
	 
	   @Override
	   public void setColorFilter(ColorFilter cf) {
	     mPaint.setColorFilter(cf);
	   }
	 
	   @Override
	   public int getOpacity() {
	     return PixelFormat.TRANSLUCENT;
	   }
	 
	   @Override
	   public int getIntrinsicWidth() {
	     return mBitmapWidth;
	   }
	 
	   @Override
	   public int getIntrinsicHeight() {
	     return mBitmapHeight;
	   }
	 
	   public void setAntiAlias(boolean aa) {
	     mPaint.setAntiAlias(aa);
	     invalidateSelf();
	   }
	 
	   @Override
	   public void setFilterBitmap(boolean filter) {
	     mPaint.setFilterBitmap(filter);
	     invalidateSelf();
	   }
	 
	   @Override
	   public void setDither(boolean dither) {
	     mPaint.setDither(dither);
	     invalidateSelf();
	   }
	 
	   public Bitmap getBitmap() {
	     return mBitmap;
	   }
	 

	}