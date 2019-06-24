package shobaky.studientsecretary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class noTouchPager extends ViewPager {
    public noTouchPager(@NonNull Context context) {
        super(context);
        setPageTransformer(true,new VerticalPageTransformer());
    }
    public noTouchPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
        setPageTransformer(true,new VerticalPageTransformer());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 500 /*1 secs*/);
        }
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top
                float yPosition = (position * view.getHeight());
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }


    }


}
