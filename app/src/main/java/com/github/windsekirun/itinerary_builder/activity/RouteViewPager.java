package com.github.windsekirun.itinerary_builder.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.fragment.RouteViewFragment;
import com.github.windsekirun.itinerary_builder.parser.Route;
import com.github.windsekirun.itinerary_builder.utils.PostServiceExtraUtils;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Pyxis on 2016. 11. 17..
 */
@SuppressWarnings("ConstantConditions")
public class RouteViewPager extends AppCompatActivity implements Constants {
    ViewPager viewPager;
    CircleIndicator indicator;
    DymanicFragmentPageAdapter fragmentPageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        viewPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        Route route = (Route) PostServiceExtraUtils.getInstance().getItem();

        if (route == null)
            finish();

        List<Fragment> fragmentList = new ArrayList<>();
        int size = route.getLegs().size();

        for (int i = 0; i < size; i++) {
            Bundle b = new Bundle();
            b.putInt(PAGING, i);
            fragmentList.add(Fragment.instantiate(this, RouteViewFragment.class.getName(), b));
        }

        fragmentPageAdapter = new DymanicFragmentPageAdapter(this,getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPageAdapter);
        indicator.setViewPager(viewPager);

        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DymanicFragmentPageAdapter extends FragmentPagerAdapter {
        public static int pos = 0;

        List<Fragment> myFragments;
        Context context;

        public DymanicFragmentPageAdapter(Context c, FragmentManager fragmentManager, List<Fragment> myFrags) {
            super(fragmentManager);
            myFragments = myFrags;
            this.context = c;
        }

        @Override
        public Fragment getItem(int position) {
            return myFragments.get(position);
        }

        @Override
        public int getCount() {
            return myFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            setPos(position);
            return "";
        }

        public static int getPos() {
            return pos;
        }

        public static void setPos(int pos) {
            DymanicFragmentPageAdapter.pos = pos;
        }
    }
}
