package com.example.android.cardgame.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.cardgame.CardCatalogueAdapter;
import com.example.android.cardgame.R;

public class CatalogueFragment extends Fragment {
    private static final String TAG = "CatalogueFragment";
    private static final double MAX_CARD_WIDTH = 200.0;

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

        Configuration configuration = getResources().getConfiguration();
        int columns = (int) Math.ceil(configuration.screenWidthDp / MAX_CARD_WIDTH);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }
}