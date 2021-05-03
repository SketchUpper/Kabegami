package org.xtimms.kabegami.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class AspectRatioImageView extends AppCompatImageView {

    protected final double mAspectRatio = 19f / 13f;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = (int) (originalWidth * mAspectRatio);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY)
        );
    }
}
