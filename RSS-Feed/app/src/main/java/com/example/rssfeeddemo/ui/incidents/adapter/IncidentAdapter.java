package com.example.rssfeeddemo.ui.incidents.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssfeeddemo.R;
import com.example.rssfeeddemo.animation.RecyclerItemAnim;
import com.example.rssfeeddemo.ui.incidents.interfaces.IncidentAdapterListener;
import com.example.rssfeeddemo.ui.incidents.model.RssItem;

import java.util.List;

//S1826060 Scott Derek Robertson
public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IndicatotViewHolder> {

    Context context;
    List<RssItem> filterItems;
    IncidentAdapterListener listener;
    boolean on_attach=true;

    public IncidentAdapter(Context context, List<RssItem> rssItems, List<RssItem> filterItems, IncidentAdapterListener listener){
        this.context = context;
        this.filterItems = filterItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IncidentAdapter.IndicatotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_layout, parent, false);
        return new IndicatotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentAdapter.IndicatotViewHolder holder, int position) {
        holder.locationTV.setText(filterItems.get(position).getTitle());
        holder.dateTv.setText(filterItems.get(position).getPubDate());
        if (position%2==0){
            RecyclerItemAnim.FromRightToLeft(holder.itemView, position, on_attach);
        } else {
            RecyclerItemAnim.FromLeftToRight(holder.itemView, position, on_attach);
        }
    }

    @Override
    public int getItemCount() {
        return filterItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }


    public class IndicatotViewHolder extends RecyclerView.ViewHolder{

        private TextView locationTV;
        private TextView dateTv;
        private ImageView playBtn;
        private ImageView iconImage;
        public IndicatotViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTV = itemView.findViewById(R.id.location_tv);
            dateTv = itemView.findViewById(R.id.textsubHeadDate);
            playBtn = itemView.findViewById(R.id.playBtn);
            iconImage = itemView.findViewById(R.id.traffic_Icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIncidentItemClick(filterItems.get(getAdapterPosition()), getAdapterPosition());
                }
            });

        }
    }
}
