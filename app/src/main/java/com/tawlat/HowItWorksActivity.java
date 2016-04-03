package com.tawlat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class HowItWorksActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Button mWatchVideoButton;
    private TextView mSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.how_it_works_activity);
        init();


        findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setImmersive();
                return false;
            }
        });
    }

    private void setFullScreen() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setImmersive();

    }

    private void setImmersive() {
        Log.d("HowItWorksActivity", "Setting to immersive");
        View decor_View = getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decor_View.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void init() {
        initReferences();
        initEvents();
        initViewPager();

        mSkip.setTextColor(ContextCompat.getColor(HowItWorksActivity.this, R.color.white));
    }

    private void initReferences() {
        mViewPager = ((ViewPager) findViewById(R.id.how_it_works_activity_view_pager));
        mWatchVideoButton = ((Button) findViewById(R.id.how_it_works_watch_the_video));
        mSkip = ((TextView) findViewById(R.id.how_it_works_skip));
    }

    private void initEvents() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //ignore
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mSkip.setTextColor(ContextCompat.getColor(HowItWorksActivity.this, R.color.pink));
                } else {
                    mSkip.setTextColor(ContextCompat.getColor(HowItWorksActivity.this, R.color.white));
                }

                if (position == mViewPager.getAdapter().getCount() - 1)
                    mSkip.setText(R.string.done_underlined);
                else
                    mSkip.setText(R.string.skip_underlined);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //ignore
            }
        });

        mWatchVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HowItWorksActivity.this, HowItWorksVideoActivity.class));
            }
        });

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViewPager() {
        mViewPager.setAdapter(new PagerAdapter() {
            int[] mItems = {R.drawable.hiw1, R.drawable.hiw2, R.drawable.hiw3, R.drawable.hiw4};

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = LayoutInflater.from(container.getContext()).inflate(
                        R.layout.how_it_work_page, container, false);

                Glide.with(HowItWorksActivity.this)
                        .load(mItems[position])
                        .into((ImageView) v.findViewById(R.id.image));

                container.addView(v);

                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }


            @Override
            public int getCount() {
                return mItems.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

}
