package com.example.rssfeeddemo.eventdetailsfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.rssfeeddemo.R;
import com.example.rssfeeddemo.ui.roadworks.model.RssItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

//S1826060 Scott Derek Robertson
public class DetailsFragment extends Fragment {

    private View view;
    private List<RssItem> rssItems;
    private int position;

    //tool bar
    private String titletoolbar;

    private String start_date_from_description;
    private String end_date_from_description;
    private String delay_information1_from_description;
    private String start_date_from_description_final;
    private String end_date_from_description_final;
    private String delay_information1_from_description_final;

    private String incident_reason_from_description_final;
    private String incident_status_from_description_final;
    private String incident_link_from_description_final;


    private String Incident = "incident";
    private String Roadworks = "road";
    private String PlannedWork = "plan";
    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_event_details, container, false);

        //getting data from intent

        if (getActivity().getIntent() != null) {
            String returnStr = getActivity().getIntent().getStringExtra("listItem");
            Gson gson = new Gson();
            Type listOfdoctorType = new TypeToken<List<RssItem>>() {
            }.getType();

            rssItems = gson.fromJson(returnStr, listOfdoctorType);
        }

        //getting item position

        if (getActivity().getIntent().getIntExtra("pos", 0) >= 0) {
            position = getActivity().getIntent().getIntExtra("pos", 0);

        }
        if (getActivity().getIntent().getStringExtra("incidents") != null) {
            Incident = getActivity().getIntent().getStringExtra("incidents");
        }
        if (getActivity().getIntent().getStringExtra("RoadWorks") != null) {
            Roadworks = getActivity().getIntent().getStringExtra("RoadWorks");
        }
        if (getActivity().getIntent().getStringExtra("plannedWork") != null) {
            PlannedWork = getActivity().getIntent().getStringExtra("plannedWork");
        }

        if (rssItems.get(position).getTitle() != null) {

            String title = rssItems.get(position).getTitle();
            String[] splited = title.split("-");
            titletoolbar = splited[0];

            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.eventdetailstoolbar);
            toolbar.setTitle(titletoolbar + " | S1826060");
        }

        //get description data from the api and split it into a start date, end date and delay information;

        if (rssItems.get(position).getDescription() != null) {
            String description = rssItems.get(position).getDescription();
            String[] split_description = description.split("<br />");

            if (split_description.length < 2) {

                start_date_from_description = split_description[0];

                if (start_date_from_description.startsWith("Start")) {
                    start_date_from_description_final = start_date_from_description;
                } else {
                    start_date_from_description_final = "No Information0";
                }
                end_date_from_description = "no information1";
                delay_information1_from_description = "no information2";

            } else if (split_description.length < 3) {

                start_date_from_description = split_description[0];
                end_date_from_description = split_description[1];


                if (start_date_from_description.startsWith("Start")) {
                    start_date_from_description_final = start_date_from_description;
                } else {
                    start_date_from_description_final = "No Information3";
                }

                if (end_date_from_description.startsWith("End")) {
                    end_date_from_description_final = end_date_from_description;
                } else {
                    end_date_from_description_final = "No Information4";
                }

                delay_information1_from_description = "no information5";

            } else {

                start_date_from_description = split_description[0];
                end_date_from_description = split_description[1];
                delay_information1_from_description = split_description[2];

                if (start_date_from_description.startsWith("Start")) {
                    start_date_from_description_final = start_date_from_description;
                } else {
                    start_date_from_description_final = "No Information6";
                }

                if (end_date_from_description.startsWith("End")) {
                    end_date_from_description_final = end_date_from_description;
                } else {
                    end_date_from_description_final = "No Information7";
                }
                if (delay_information1_from_description.startsWith("Delay")) {
                    delay_information1_from_description_final = delay_information1_from_description;
                } else {
                    delay_information1_from_description_final = "No Information8";
                }

            }
        } else {
            start_date_from_description = "no information9";
            end_date_from_description = "no information10";
            delay_information1_from_description = "no information11";
        }

        //get description data from api and split it into a reason,status and link information;

        if (rssItems.get(position).getDescription() != null) {

            incident_reason_from_description_final = rssItems.get(position).getTitle();
            incident_status_from_description_final = rssItems.get(position).getTitle();
            incident_link_from_description_final = rssItems.get(position).getLink();
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //initialize all variable for details

        TextView locationTV = view.findViewById(R.id.location_tv);
        TextView locationTitleTV = view.findViewById(R.id.location_title_tv);
        TextView start_dateTv = view.findViewById(R.id.start_dateTv);
        TextView startTitleTV = view.findViewById(R.id.start_date_title_tv);
        TextView end_dateTV = view.findViewById(R.id.end_date_tv);
        TextView endTitleTV = view.findViewById(R.id.end_title_tv);
        TextView delayTV = view.findViewById(R.id.delay_tv);
        TextView delayTitleTV = view.findViewById(R.id.delay_title_tv);
        TextView laneTV = view.findViewById(R.id.lane_tv);
        TextView laneTitleTV = view.findViewById(R.id.lane_title_tv);

        //for roadworks item details

        if (Roadworks.equals("RoadWork")) {

            locationTV.setText("Location");
            start_dateTv.setText("Start Date");
            end_dateTV.setText("End Date");
            delayTV.setText("Delay Information");
            laneTV.setText("Lane");

            //set data for click item

            locationTitleTV.setText(rssItems.get(position).getTitle());
            startTitleTV.setText(start_date_from_description_final);
            endTitleTV.setText(end_date_from_description_final);
            delayTitleTV.setText(delay_information1_from_description);
        }

        //for incident item details

        if (Incident.equals("Incident")) {
            locationTV.setText("Location");
            start_dateTv.setText("Reason");
            end_dateTV.setText("Status");
            delayTV.setText("Link");
            laneTV.setText("Lane");

            //set data for click item

            locationTitleTV.setText(rssItems.get(position).getTitle());
            startTitleTV.setText(incident_reason_from_description_final);
            endTitleTV.setText(incident_status_from_description_final);
            delayTitleTV.setText(incident_link_from_description_final);

        }
        //for planned work item details

        if (PlannedWork.equals("PlanedRoadWork")) {
            locationTV.setText("Location");
            start_dateTv.setText("Start Date");
            end_dateTV.setText("End Date");
            delayTV.setText("Description");
            laneTV.setText("Lane");

            //set data for click item

            locationTitleTV.setText(rssItems.get(position).getTitle());
            startTitleTV.setText(start_date_from_description_final);
            endTitleTV.setText(end_date_from_description_final);
            delayTitleTV.setText(delay_information1_from_description);

        }
    }

}
