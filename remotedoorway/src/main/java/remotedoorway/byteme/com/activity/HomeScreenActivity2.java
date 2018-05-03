package remotedoorway.byteme.com.activity;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.fragment.DoorListFragment;
import remotedoorway.byteme.com.fragment.LogsFragment;
import remotedoorway.byteme.com.fragment.SettingsFragment;
import remotedoorway.byteme.com.fragment.SharedFragment;

public class HomeScreenActivity2 extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private SensorManager sensorMan;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.setBackgroundColor(getResources().getColor(R.color.background));
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_ic_report_tab);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_adduser);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_settings_black_24dp);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DoorListFragment(), "HOME");
        adapter.addFragment(new LogsFragment(), "Logs");
        adapter.addFragment(new SharedFragment(), "Shared");
        adapter.addFragment(new SettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




}
