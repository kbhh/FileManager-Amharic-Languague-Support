package com.example.kbd.filemanager;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kbd.filemanager.Files.FilesContent;

public class SearchableActivity extends AppCompatActivity
implements FilesFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();



        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            FilesFragment fg = new FilesFragment(new FilesContent(getBaseContext()).search(query), "SH");

            fragmentTransaction.add(R.id.fragmentContainer, fg);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onListFragmentInteraction(FilesContent.FileItem item) {
    }
}
