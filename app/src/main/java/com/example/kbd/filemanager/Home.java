package com.example.kbd.filemanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.kbd.filemanager.Files.FilesContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
    }



    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Inflate the layout for this fragment

        Button musicBtn = getActivity().findViewById(R.id.music_btn);
        Button videoBtn = getActivity().findViewById(R.id.movies_btn);
        Button imgBtn = getActivity().findViewById(R.id.image_button);
        Button docBtn = getActivity().findViewById(R.id.doc_btn);

        LinearLayout sdBtn = getActivity().findViewById(R.id.sd_card_mview);
        LinearLayout meBtn = getActivity().findViewById(R.id.memory_btn_view);

        sdBtn.setOnClickListener(view -> {
            System.out.print("......... clicked");
           mListener.onFragmentInteraction("SD");
        });



        meBtn.setOnClickListener(view -> {
           mListener.onFragmentInteraction("ME");
        });

        musicBtn.setOnClickListener(view -> {

            mListener.onFragmentInteraction("MU");
        });

        videoBtn.setOnClickListener(view -> {
            mListener.onFragmentInteraction("VD");
        });

        imgBtn.setOnClickListener(view -> {
            mListener.onFragmentInteraction("IM");
        });

        docBtn.setOnClickListener(view -> {
            mListener.onFragmentInteraction("DC");

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String dataType);
    }
}
