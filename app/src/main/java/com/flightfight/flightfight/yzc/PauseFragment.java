package com.flightfight.flightfight.yzc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flightfight.flightfight.MainActivity;
import com.flightfight.flightfight.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PauseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PauseFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView quit;
    private TextView continueBtn;
    private String mParam1;
    private String mParam2;
    private Activity mActivity;
    private HideFragMent hide;
    public PauseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PauseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PauseFragment newInstance(String param1, String param2) {
        PauseFragment fragment = new PauseFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pause, container, false);
        quit = view.findViewById(R.id.pause_bck_menu);
        continueBtn = view.findViewById(R.id.pause_continue_game);
        continueBtn.setOnClickListener(v -> hide.setHitde());
        quit.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }


    public interface HideFragMent{
        void setHitde();
    }

    public void setHide(HideFragMent hide) {
        this.hide = hide;
    }
}
