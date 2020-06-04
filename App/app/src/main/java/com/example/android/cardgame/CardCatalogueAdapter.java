package com.example.android.cardgame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mob.sdk.cards.Card;
import mob.sdk.cards.CardRepository;

public class CardCatalogueAdapter extends RecyclerView.Adapter<CardCatalogueAdapter.CardViewHolder> {
    private final Context mContext;
    private final List<String> mCardIdList;
    private final LayoutInflater mInflater;

    public CardCatalogueAdapter(Context context) {
        this.mContext = context;
        this.mCardIdList = SavedCardSettings.INSTANCE.loadCards();
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.layout_catalogue_card, parent, false);

        return new CardViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        String cardId = mCardIdList.get(position);
        Card card = CardRepository.INSTANCE.getCard(cardId);
        holder.mTitleTextView.setText(card.getName());
//        holder.mImageView.setImageResource(R.drawable.);
    }

    @Override
    public int getItemCount() {
        return mCardIdList.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTitleTextView;
        public final ImageView mImageView;

        private final CardCatalogueAdapter mAdapter;

        public CardViewHolder(@NonNull View itemView, CardCatalogueAdapter cardCatalogueAdapter) {
            super(itemView);
            this.mAdapter = cardCatalogueAdapter;
            this.mTitleTextView = itemView.findViewById(R.id.cardTitle);
            this.mImageView = itemView.findViewById(R.id.cardImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(getClass().getSimpleName(), "Test");

            int position = getLayoutPosition();

            String cardId = mCardIdList.get(position);

            Intent intent = new Intent(mContext, CardDetailActivity.class);
            intent.putExtra(CardDetailActivity.EXTRA_CARD_ID, cardId);
            mContext.startActivity(intent);
        }
    }
}
