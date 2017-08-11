package com.aserbao.homepager.customview.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class HorizationFlowLayout extends ViewGroup
{

	private static final String TAG = "FlowLayout";


	public HorizationFlowLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context mContext) {
		for (int i = 0; i < 150; i++) {
			Button button = new Button(mContext);
			button.setText(String.valueOf(i));
			button.setWidth(100);
			button.setHeight(60);
			this.addView(button);
		}
	}

	@Override
	protected LayoutParams generateLayoutParams(
			LayoutParams p)
	{
		return new MarginLayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams()
	{
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
//		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得它的父容器为它设置的测量模式和大小
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		Log.e(TAG, sizeWidth + "," + sizeHeight);

		// 如果是warp_content情况下，记录宽和高
		int width = 0;
		int height = 0;
		/**
		 * 记录每一行的宽度，width不断取最大宽度
		 */
		int lineWidth = 0;
		/**
		 * 每一行的高度，累加至height
		 */
		int lineHeight = 0;

		int cCount = getChildCount();

		// 遍历每个子元素
		for (int i = 0; i < cCount; i++)
		{
			View child = getChildAt(i);
			// 测量每一个child的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到child的lp
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			// 当前子空间实际占据的宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			// 当前子空间实际占据的高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;
			/**
			 * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
			 */
			if (lineHeight + childHeight > sizeHeight)
			{
				height = Math.max(lineHeight, childHeight);// 取最大的
				lineHeight = childHeight; // 重新开启新行，开始记录
				// 叠加当前高度，
				width += lineWidth;
				// 开启记录下一行的高度
				lineWidth = childWidth;
			} else
			// 否则累加值lineWidth,lineHeight取最大高度
			{
				lineHeight += childHeight;
				lineWidth = Math.max(lineWidth, childWidth);
			}
			// 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
			if (i == cCount - 1)
			{
				height = Math.max(height, lineHeight);
				width += lineWidth;
			}

		}
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
				: width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
				: height);

	}
	/**
	 * 存储所有的View，按行记录
	 */
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 记录每一行的最大高度
	 */
	private List<Integer> mLineWidth = new ArrayList<Integer>();
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		mAllViews.clear();
		mLineWidth.clear();

		int width = getWidth();
		int height = getHeight();

		int lineWidth = 0;
		int lineHeight = 0;
		// 存储每一行所有的childView
		List<View> lineViews = new ArrayList<View>();
		int cCount = getChildCount();
		// 遍历所有的孩子
		for (int i = 0; i < cCount; i++)
		{
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 如果已经需要换行
			if (childHeight + lp.topMargin + lp.bottomMargin + lineHeight > height)
			{
				// 记录这一行所有的View以及最大宽度
				mLineWidth.add(lineWidth);
				// 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
				mAllViews.add(lineViews);
				lineHeight = 0;// 重置行宽
				lineViews = new ArrayList<View>();
			}
			/**
			 * 如果不需要换行，则累加
			 */
			lineHeight += childHeight + lp.topMargin + lp.bottomMargin;
			lineWidth = Math.max(lineWidth, childWidth + lp.leftMargin
					+ lp.rightMargin);
			lineViews.add(child);
		}
		// 记录最后一行
		mLineWidth.add(lineWidth);
		mAllViews.add(lineViews);

		int left = 0;
		int top = 0;
		// 得到总行数
		int columnNums = mAllViews.size();
		for (int i = 0; i < columnNums; i++)
		{
			// 每一行的所有的views
			lineViews = mAllViews.get(i);
			// 当前行的最大高度
			lineWidth = mLineWidth.get(i);

			Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
			Log.e(TAG, "第" + i + "行， ：" + lineHeight);

			// 遍历当前行所有的View
			for (int j = 0; j < lineViews.size(); j++)
			{
				View child = lineViews.get(j);
				if (child.getVisibility() == View.GONE)
				{
					continue;
				}
				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();

				//计算childView的left,top,right,bottom
				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc =lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();

				Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
						+ rc + " , b = " + bc);

				child.layout(lc, tc, rc, bc);
				
				top += child.getMeasuredHeight() + lp.topMargin
						+ lp.bottomMargin;
			}
			top = 0;
			left += lineWidth;
		}

	}
}