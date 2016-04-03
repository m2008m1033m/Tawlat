package com.tawlat;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GalleryActivity extends AppCompatActivity {
    public final static String VENUE_NAME = "venue_name";
    public final static String PHOTOS = "photos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String venueName = getIntent().getStringExtra(VENUE_NAME);
        final ArrayList<String> photos = getIntent().getStringArrayListExtra(PHOTOS);

        if (venueName == null || photos == null || photos.size() == 0) {
            finish();
            return;
        }

        setContentView(R.layout.gallery_activity);

        // make a transparent status bar in case of kitkat and lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            (findViewById(R.id.wrapper)).setPadding(0,
                    (int) getResources()
                            .getDimension(R.dimen.actionbar_padding), 0, 0);
        }

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }
        setTitle(venueName);

        ViewPager viewPager = ((ViewPager) findViewById(R.id.viewpager));
        viewPager.setAdapter(new PagerAdapter() {


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = LayoutInflater.from(container.getContext()).inflate(
                        R.layout.gallery_photo_page, container, false);

                final ImageView iv = (ImageView) v.findViewById(R.id.photo);
                final ProgressBar pb = (ProgressBar) v
                        .findViewById(R.id.progress);

                pb.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);

                Picasso.with(TawlatApplication.getContext())
                        .load(photos.get(position))
                        .into(iv, new Callback() {
                            @Override
                            public void onSuccess() {
                                pb.setVisibility(View.GONE);
                                iv.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });


                container.addView(v);
                return v;

            }

            @Override
            public int getCount() {
                return photos.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

        });

        final TextView indicator = ((TextView) findViewById(R.id.indicator));
        final int totalPhotos = photos.size();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int photoNumber = position + 1;
                indicator.setText(getString(R.string.d_of_d, photoNumber, totalPhotos));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        indicator.setText(getString(R.string.d_of_d, 1, totalPhotos));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
