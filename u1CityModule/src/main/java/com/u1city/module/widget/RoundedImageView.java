package com.u1city.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.u1city.module.R;
import com.u1city.module.common.Debug;

/***
 * @author joan
 * 2015-03-13 
 * 圆形图片控件
 * 
 * */
public class RoundedImageView extends ImageView {
	private boolean isDrawOutCicle=true;
	private int cicleOutColor=Color.WHITE;
	
	public boolean isDrawOutCicle() {
		return isDrawOutCicle;
	}
	public void setDrawOutCicle(boolean isDrawOutCicle) {
		this.isDrawOutCicle = isDrawOutCicle;
		postInvalidate();
	}
	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    /**
     * get attrs
     * 
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.rounde_image_view);
        isDrawOutCicle = ta.getBoolean(R.styleable.rounde_image_view_isDrowOutCicle, true);
        cicleOutColor=ta.getColor(R.styleable.rounde_image_view_cicleOutStrokeCorlor, Color.WHITE);
        ta.recycle();
    }
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context,attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getAttrs(context,attrs);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Debug.println("...........RoundedImageView...................>"+isDrawOutCicle);

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return; 
		}
		Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		int w = getWidth(), h = getHeight();
		Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0,0, null);
		if(isDrawOutCicle){
			Paint paint=new Paint();
			paint.setAntiAlias(true);
			paint.setColor(cicleOutColor);
			paint.setStrokeWidth(1.5f);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(w/ 2,h/ 2f,
					w / 2-1.0f, paint);
		}
	}
	public void setCicleOutColor(int cicleOutColor) {
		this.cicleOutColor = cicleOutColor;
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0,0, sbmp.getWidth(), sbmp.getHeight());
		paint.setAntiAlias(true);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setFilterBitmap(true);
		paint.setStrokeWidth(0);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2,
				sbmp.getWidth() / 2-3.0f, paint);
		canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2,
				sbmp.getWidth() / 2-3.0f, paint);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		return output;
	}

}
