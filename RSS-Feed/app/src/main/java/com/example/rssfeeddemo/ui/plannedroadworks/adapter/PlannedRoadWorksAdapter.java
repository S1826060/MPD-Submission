package com.example.rssfeeddemo.ui.plannedroadworks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssfeeddemo.R;
import com.example.rssfeeddemo.animation.RecyclerItemAnim;
import com.example.rssfeeddemo.helper.AdapterHelper;
import com.example.rssfeeddemo.ui.plannedroadworks.interfaces.PlannedRoadWorkAdapterListener;
import com.example.rssfeeddemo.ui.plannedroadworks.model.RssItem;

import java.util.List;

//S1826060 Scott Derek Robertson
public class PlannedRoadWorksAdapter extends RecyclerView.Adapter<PlannedRoadWorksAdapter.EventViewHolder> {

    Context context;
    List<RssItem> filterItems;
    PlannedRoadWorkAdapterListener listener;
    boolean on_attach=true;
    String startDate, endDate;

    public PlannedRoadWorksAdapter(Context context, List<RssItem> rssItems, List<RssItem> filterItems, PlannedRoadWorkAdapterListener listener){
        this.context = context;
        this.filterItems = filterItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlannedRoadWorksAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedRoadWorksAdapter.EventViewHolder holder, int position) {
        holder.locationTV.setText(filterItems.get(position).getTitle());
        holder.dateTv.setText(filterItems.get(position).getPubDate());
        if (position%2==0){
            RecyclerItemAnim.FromRightToLeft(holder.itemView, position, on_attach);
        } else {
            RecyclerItemAnim.FromLeftToRight(holder.itemView, position, on_attach);
        }

        String description = filterItems.get(position).getDescription();
        String[] spliteDesc = description.split("<br />");
        if (spliteDesc.length > 0) {
            startDate = spliteDesc[0];
            endDate = spliteDesc[1];

            String[] startDateSplit = startDate.split(",");
            String[] endDateSplit = endDate.split(",");

            String[] startDateF = startDateSplit[1].split("-");
            String[] endDateF = endDateSplit[1].split("-");

            String[] startDateSingle = startDateF[0].trim().split(" ");
            String[] endDateSingle = endDateF[0].trim().split(" ");

            int month1 = AdapterHelper.getMonthNumber(startDateSingle[1]);
            int month2 = AdapterHelper.getMonthNumber(endDateSingle[1]);

            String finalStartDate = startDateSingle[0]+"/"+month1+"/"+startDateSingle[2];
            String finalEndDate = endDateSingle[0]+"/"+month2+"/"+endDateSingle[2];

            int day = AdapterHelper.days(finalStartDate, finalEndDate);

            if(day<2){
                holder.playBtn.setImageTintList(context.getResources().getColorStateList(R.color.color_green));
            } else if(day>=2 && day<7) {
                holder.playBtn.setImageTintList(context.getResources().getColorStateList(R.color.color_orange));
            } else if(day>=7){
                holder.playBtn.setImageTintList(context.getResources().getColorStateList(R.color.color_red));
            }

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

    public class EventViewHolder extends RecyclerView.ViewHolder{

        private TextView locationTV;
        private TextView dateTv;
        private ImageView playBtn;
        private ImageView iconImage;
        LinearLayout itemLL;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTV = itemView.findViewById(R.id.location_tv);
            dateTv = itemView.findViewById(R.id.textsubHeadDate);
            playBtn = itemView.findViewById(R.id.playBtn);
            iconImage = itemView.findViewById(R.id.traffic_Icon);
            itemLL = itemView.findViewById(R.id.item_ll);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlannedRoadWorkItemClick(filterItems.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}
