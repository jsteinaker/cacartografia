package com.jsteinaker.cacartografia;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class DUALCInfoWindow extends LinearLayout {

	private static final int DEFAULT_MAX_HEIGHT_RATIO = 6;
	
	private int maxHeight;
	private int maxHeightRatio;

	public DUALCInfoWindow(Context context) {
		super(context);
	}

	public DUALCInfoWindow(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DUALCInfoWindow);
		maxHeightRatio = ta.getInt(R.styleable.DUALCInfoWindow_maxHeightRatio, DEFAULT_MAX_HEIGHT_RATIO);

		ta.recycle();
	}

	public DUALCInfoWindow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DUALCInfoWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setMaxHeight(int parentHeight) {
		maxHeight = parentHeight * maxHeightRatio / 10; 
	}
}
