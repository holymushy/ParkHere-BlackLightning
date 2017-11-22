package com.blacklightning.parkhere;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListofParkingSpotOwned extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<String> listings;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser currentUser;
    private static DatabaseReference mDB;
    private DataSnapshot parkingSpotList;
    private String userID;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_parking_spot_owned);
        Intent intent = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDB = FirebaseDatabase.getInstance().getReference();
        userID = intent.getStringExtra("userID");

        listView = (ListView) findViewById(R.id.ownedList);

        if(currentUser != null){
            DatabaseReference ref = mDB.child("parkingspot").child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listings = new ArrayList<>();
                    parkingSpotList = dataSnapshot;
                    for(DataSnapshot spaceItem : parkingSpotList.getChildren()){
                        String stAddress = spaceItem.child("stAddress").getValue().toString();
                        String city = spaceItem.child("city").getValue().toString();
                        String state = spaceItem.child("state").getValue().toString();

                        String latlngAddress = stAddress + ", " + city + ", " + state;
                        Context context = ListofParkingSpotOwned.this;
                        System.out.println("Fuck: " + latlngAddress);
                        Geocoder geocoder = new Geocoder(context);

                        try {
                            List<Address> coordinateList = geocoder.getFromLocationName(latlngAddress, 5);
                            if(coordinateList.size()>0) {
                                System.out.println("Length: " + coordinateList.size());
                                Address coor = coordinateList.get(0);
                                listings.add(latlngAddress);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Length listings: " + listings.size());
                    ArrayAdapter<String> listAdapter = new ArrayAdapter<>(ListofParkingSpotOwned.this, android.R.layout.simple_list_item_1, listings);
                    listView.setAdapter(listAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        ListView listView = (ListView) findViewById(R.id.ownedList);


    }
}