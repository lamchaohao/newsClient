package net.togogo.newsclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.togogo.newsclient.R;

public class BxqFragment extends Fragment {

    public BxqFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BxqFragment newInstance(String param1, String param2) {
        BxqFragment fragment = new BxqFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bxq, container, false);
    }


}
