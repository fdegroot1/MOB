package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.cardgame.CardCatalogueAdapter;
import com.example.android.cardgame.R;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class CatalogueFragment extends Fragment {
    private static final String TAG = "CatalogueFragment";

    private CardCatalogueAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAdapter = new CardCatalogueAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogus, container, false);

        this.mRecyclerView = view.findViewById(R.id.cardCatalogue);
        mRecyclerView.setAdapter(mAdapter);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        mRecyclerView.setLayoutManager(flexboxLayoutManager);

        return view;
    }
}