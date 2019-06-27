package com.example.kbd.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kbd.filemanager.Files.FilesContent.FileItem;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FilesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public  List<FileItem> filesList;
    public FilesAdpater adapter;
    public String fname;



    public FilesFragment() {}

    @SuppressLint("ValidFragment")
    public FilesFragment(List<FileItem> files, String name) {
        filesList = files;
        fname = name;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FilesFragment newInstance(int columnCount) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.adapter.backPressed(getContext());
                return true;
            case R.id.create_file:
                this.adapter.newFolder(getContext(), fname);
                return true;
            case R.id.sort_size:
                this.adapter.sortSize(getContext());
                return  true;
            case R.id.sort_name:
                this.adapter.sortName(getContext());
                return true;
            default:
                return true;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                adapter = new FilesAdpater(filesList, mListener, getActivity());

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                adapter = new FilesAdpater(filesList, mListener, getActivity());
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);
        }
        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FileItem item);
    }
}
