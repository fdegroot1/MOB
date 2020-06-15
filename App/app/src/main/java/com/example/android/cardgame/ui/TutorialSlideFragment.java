package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;

import java.io.Serializable;

public class TutorialSlideFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_BUTTON_FINISH_LISTENER = "finishListener";
    private static final String ARG_BUTTON_NEXT_LISTENER = "nextListener";
    private int layoutId;
    private TutorialFinishListener finishListener;
    private TutorialNextListener nextListener;

    private TutorialSlideFragment() {

    }

    public static TutorialSlideFragment getFirst(TutorialNextListener listener) {
        TutorialSlideFragment fragment = new TutorialSlideFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_LAYOUT, R.layout.fragment_tutorial_first_slide);
        arguments.putSerializable(ARG_BUTTON_NEXT_LISTENER, listener);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static TutorialSlideFragment getSecond(TutorialNextListener listener) {
        TutorialSlideFragment fragment = new TutorialSlideFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_LAYOUT, R.layout.fragment_tutorial_second_slide);
        arguments.putSerializable(ARG_BUTTON_NEXT_LISTENER, listener);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static TutorialSlideFragment getThird(TutorialFinishListener listener) {
        TutorialSlideFragment fragment = new TutorialSlideFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_LAYOUT, R.layout.fragment_tutorial_third_slide);
        arguments.putSerializable(ARG_BUTTON_FINISH_LISTENER, listener);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.layoutId = getArguments().getInt(ARG_LAYOUT, R.layout.fragment_tutorial_first_slide);

            this.finishListener = (TutorialFinishListener) getArguments().getSerializable(ARG_BUTTON_FINISH_LISTENER);
            this.nextListener = (TutorialNextListener) getArguments().getSerializable(ARG_BUTTON_NEXT_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(layoutId, container, false);

        Button finishButton = view.findViewById(R.id.tutorialFinishButton);
        if (finishButton != null && finishListener != null) {
            finishButton.setOnClickListener(v -> finishListener.onFinish());
        }

        Button nextButton = view.findViewById(R.id.tutorialNextButton);
        if (nextButton != null && nextListener != null) {
            nextButton.setOnClickListener(v -> nextListener.onNext());
        }

        return view;
    }

    @FunctionalInterface
    public interface TutorialNextListener extends Serializable {
        void onNext();
    }

    @FunctionalInterface
    public interface TutorialFinishListener extends Serializable {
        void onFinish();
    }
}