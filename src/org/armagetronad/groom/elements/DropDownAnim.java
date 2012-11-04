package org.armagetronad.groom.elements;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class DropDownAnim extends Animation {
    int targetHeight;
    View view;
	int startingHeight;

    public DropDownAnim(View view, int targetHeight) {
        this.view = view;
        boolean down = view.getHeight() == 0;
        this.targetHeight = down ? targetHeight : 0;
        this.startingHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        newHeight =  (int) (((targetHeight -startingHeight) * interpolatedTime) + startingHeight);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
            int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
