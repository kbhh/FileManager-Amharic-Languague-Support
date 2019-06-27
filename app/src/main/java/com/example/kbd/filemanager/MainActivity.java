package com.example.kbd.filemanager;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import com.example.kbd.filemanager.Files.FilesContent;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
 implements FilesFragment.OnListFragmentInteractionListener, Home.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabs;
    private SlidePageAdapter adapter;
    private ArrayList<Fragment> list;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = this.getSharedPreferences("com.example.kbd.filemanager", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Lang", "Amharic");

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        mViewPager =  findViewById(R.id.container);

        list = new ArrayList<>();

        list.add(Home.newInstance("One", "Two"));
        list.add(new FilesFragment(new FilesContent(getBaseContext()).getInternal(), "IN"));
        list.add(new  FilesFragment(new FilesContent(getBaseContext()).getSd(), "SD"));


        tabs = findViewById(R.id.nav_tabs);
        tabs.setupWithViewPager(mViewPager);

        Button searchDialog = findViewById(R.id.search_dialog_btn);
        searchDialog.setOnClickListener(view -> super.onSearchRequested());


        adapter = new SlidePageAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);



    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setTitle(getText(R.string.exit))
                .setMessage(getText(R.string.exitStat))
                .setPositiveButton(getText(R.string.yes) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(getText(R.string.no), null)
                .show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id  == android.R.id.home) {
            int currentItem = mViewPager.getCurrentItem();


            if ((currentItem == 0)) {

                list.remove(0);
                list.add(0, Home.newInstance("one", "two"));
                adapter.fragments = list;
                //adapter.getItem(mViewPager.getCurrentItem()).getChildFragmentManager().beginTransaction().replace(R.id.wanaContainer, ps).commit();
                adapter.notifyDataSetChanged();

            }
        } else if (id == R.id.lang) {
            langDailog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void langDailog() {

        ArrayList<CharSequence> langs= new ArrayList<>();
        langs.add(getText(R.string.english));
        langs.add(getText(R.string.amharic));

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.dialog_frame)
                .setTitle(getText(R.string.languge))
                .setMessage("Change Languge")
                .setPositiveButton(getText(R.string.amharic), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setLocal("");
                    }
                })
                .setNegativeButton(getText(R.string.english), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setLocal("am");
                    }
                })
                .show();
    }

    public void setLocal(String local) {

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(local.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    private  class SlidePageAdapter extends FragmentStatePagerAdapter{
        ArrayList<Fragment> fragments;


        public SlidePageAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
            super(fm);
            fragments = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return  getResources().getString(R.string.home);
                case 1:
                    return getResources().getString(R.string.phone_storage);
                case 2:
                    return getResources().getString(R.string.sd_card);
            }

            return super.getPageTitle(position);
        }
    }
    @Override
    public void onListFragmentInteraction(FilesContent.FileItem item) {

    }



    @Override
    public void onFragmentInteraction(String dataType) {


            switch (dataType) {
                case "VD":
                    list.remove(0);
                    list.add(0, new  FilesFragment(new FilesContent(getBaseContext()).getVideos(), "IN"));
                    adapter.fragments = list;
                    adapter.notifyDataSetChanged();
                    break;
                case "IM":
                    list.remove(0);
                    list.add(0, new  FilesFragment(new FilesContent(getBaseContext()).getImages(), "IN"));
                    adapter.fragments = list;
                    adapter.notifyDataSetChanged();
                    break;
                case "MU":
                    list.remove(0);
                    list.add(0, new  FilesFragment(new FilesContent(getBaseContext()).getAudios(), "IN"));
                    adapter.fragments = list;
                    adapter.notifyDataSetChanged();
                    break;
                case "DC":
                    list.remove(0);
                    list.add(0, new  FilesFragment(new FilesContent(getBaseContext()).getFiles(), "IN"));
                    adapter.fragments = list;
                    adapter.notifyDataSetChanged();
                    break;
                case "SD":
                    System.out.print("In......");
                    mViewPager.setCurrentItem(2);
                    break;
                case "ME":
                    mViewPager.setCurrentItem(1);
                    break;
            }
    }



}
