package com.example.rssfeeddemo.eventdetailsfragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rssfeeddemo.R;
import com.example.rssfeeddemo.eventdetailsfragments.map_model.model;
import com.example.rssfeeddemo.ui.roadworks.model.RssItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//S1826060 Scott Derek Robertson
public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener {


    int Position;
    List<RssItem> data;
    List<LatLng> latLngList;

    //for view click item in map
    LatLng view_location_at_first;
    //


    //for info_dialog in map marker

    String start_date;
    String end_date;
    List<model> even_name;
    String delay_information1;
    private TextView eventName, location;
    private TextView startDate, endDate, delayInformation;
    private TextView link;
    private TextView pubDate;
    //

    //location

    LatLng LatLang;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private GoogleMap mMap;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

            //for fetch phone location

            fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {


                    //for set all the location of the event

                    for (int i = 0; i < latLngList.size(); i++) {
                        mMap.addMarker(new MarkerOptions()
                                .position(latLngList.get(i))
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_place_black_24dp)));

                    }

                    //for showing info dialog

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }
                        @Override
                        public View getInfoContents(Marker marker) {

                            //for creating custom info dialog

                            View v = getLayoutInflater().inflate(R.layout.map_dialoge, null);
                            eventName = (TextView) v.findViewById(R.id.Event_name);
                            location = (TextView) v.findViewById(R.id.location);
                            startDate = (TextView) v.findViewById(R.id.start_date);
                            endDate = (TextView) v.findViewById(R.id.end_date);
                            link = (TextView) v.findViewById(R.id.link);
                            delayInformation = (TextView) v.findViewById(R.id.delay_information);
                            pubDate = (TextView) v.findViewById(R.id.publication_date);


                            for (int i = 0; i < even_name.size(); i++) {
                                model item = even_name.get(i);
                                if (item.getLatLng().equals(marker.getPosition())) {

                                    eventName.setText(item.getName());
                                    location.setText("Location: " + item.getLocation());
                                    startDate.setText(item.getStart_date());
                                    endDate.setText(item.getEnd_date());
                                    delayInformation.setText(item.getDelay_information());
                                    link.setText("Link: " + item.getLink());
                                    pubDate.setText("Publication Date: " + item.getPubDate());
                                } else {
                                    // eventName.setText("testttt");
                                }
                            }
                            return v;
                        }
                    });


                    LatLng latLng = view_location_at_first;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_place_black_24dp)));
                    marker.showInfoWindow();


                }

            });
        }

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_maps, container, false);
        initLocation();

        latLngList = new ArrayList<>();


        //for getting data from Intent

        if (getActivity().getIntent() != null) {
            String returnStr = getActivity().getIntent().getStringExtra("listItem");
            Gson gson = new Gson();
            Type intentDataType = new TypeToken<List<RssItem>>() {
            }.getType();
            data = gson.fromJson(returnStr, intentDataType);
        }
        if (getActivity().getIntent().getIntExtra("pos", 0) >= 0) {
            Position = getActivity().getIntent().getIntExtra("pos", 0);
        }

        //for listing data
        even_name = new ArrayList<model>();

        for (int i = 0; i < data.size(); i++) {

            //get location data from api and split it into a latitude and longitude


            if (data.get(i).getGeorss_point() != null) {
                String latlng = data.get(i).getGeorss_point();
                String[] splited = latlng.split("\\s+");
                double latitude = Double.parseDouble(splited[0]);
                double longitude = Double.parseDouble(splited[1]);

                LatLang = new LatLng(latitude, longitude);

                latLngList.add(LatLang);

            } else {

                LatLang = new LatLng(0, 0);
            }

            //get description data from api and split it into a start date ,end date and delay information;

            if (data.get(i).getDescription() != null) {
                String description = data.get(i).getDescription();
                String[] split_description = description.split("<br />");


                if (split_description.length < 2) {

                    start_date = split_description[0];
                    end_date = "  ";
                    delay_information1 = "   ";

                } else if (split_description.length < 3) {

                    start_date = split_description[0];
                    end_date = split_description[1];
                    delay_information1 = "   ";


                } else {
                    start_date = split_description[0];
                    end_date = split_description[1];
                    delay_information1 = split_description[2];
                }
            } else {
                start_date = "no information4";
                end_date = "no information5";
                delay_information1 = "no information6";
            }

            model mm = new model(data.get(i).getTitle(), data.get(i).getTitle(), start_date, end_date, delay_information1, data.get(i).getLink()
                    , data.get(i).getAuthor(), data.get(i).getComments(), data.get(i).getPubDate(), LatLang);

            even_name.add(mm);
        }


        //for click item view in map

        RssItem item = data.get(Position);
        if (item.getGeorss_point() != null) {
            String ViewLocation_atFirst = item.getGeorss_point();
            String[] splited = ViewLocation_atFirst.split("\\s+");
            double latitude_first_view = Double.parseDouble(splited[0]);
            double longitude_first_view = Double.parseDouble(splited[1]);

            view_location_at_first = new LatLng(latitude_first_view, longitude_first_view);
        } else
        {
            view_location_at_first = LatLang;
        }
        return root;
    }

    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.event_details_map_show, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_place_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}

