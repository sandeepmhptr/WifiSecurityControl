package com.example.root.securityalert;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
*/

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//ImplementiOnPageChangeListenerng the interface OnTabSelectedListener to our MainActivity
//This interface would help in swiping views
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    //static final int NUM_ITEMS = 3;

    //MyAdapter mAdapter;
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("clock"));
        tabLayout.addTab(tabLayout.newTab().setText("settings"));
        tabLayout.addTab(tabLayout.newTab().setText("lost devices"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        //mAdapter = new MyAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
        //viewPager.setOnPageChangeListener(this);
        viewPager.setOnPageChangeListener(myOnPageChangeListener);
    }
    ViewPager.OnPageChangeListener myOnPageChangeListener =
            new ViewPager.OnPageChangeListener(){

                @Override
                public void onPageScrollStateChanged(int state) {
                    //Called when the scroll state changes.
                }

                @Override
                public void onPageScrolled(int position,
                                           float positionOffset, int positionOffsetPixels) {
                    //This method will be invoked when the current page is scrolled,
                    //either as part of a programmatically initiated smooth scroll
                    //or a user initiated touch scroll.
                }

                @Override
                public void onPageSelected(int position) {
                    TabLayout.Tab tab = tabLayout.getTabAt(position);
                    if (tab != null) {
                        tab.select();
                    }
                }};

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    /*public static class MyAdapter extends FragmentPagerAdapter {
        int num;
        public MyAdapter(FragmentManager fm,int numPages) {
            super(fm);
            num = numPages;
        }

        @Override
        public int getCount() {
            return num;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }
    }*/

   /* public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
       /* static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }*/

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
       /* @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }*/

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        /*@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("Fragment #" + mNum);
            return v;
        }

        String[] title = {
                "Abundance",
                "Anxiety",
                "Bruxism",
                "Discipline",
                "Drug Addiction"
        };

        List<String> mStrings = new ArrayList<String>();


        String[] strings = new String[mStrings.size()];


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            for(int i=0;i<title.length;i++) {
                mStrings.add(title[i]);

            }
            strings = mStrings.toArray(strings);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    mStrings));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }*/
}