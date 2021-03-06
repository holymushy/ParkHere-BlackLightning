package com.blacklightning.parkhere;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class BookParkingSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference fBase;
    private FirebaseUser currentUser;

    private String userID, pSpotID, currentSpotID;
    Button bCreate;
    TextView tvTimeStart;
    TextView tvTimeEnd;
    TextView tvDateStart;
    TextView tvDateEnd;
    Calendar mcurrentTime;
    Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_parking_spot);

        Bundle extra = getIntent().getExtras();
        userID = extra.getString("userID");
        pSpotID = extra.getString("parkingID");

        tvTimeStart = (TextView) findViewById(R.id.startTime);
        tvTimeEnd = (TextView)findViewById(R.id.endTime);
        tvDateStart = (TextView) findViewById(R.id.startDate);
        tvDateEnd = (TextView) findViewById(R.id.endDate);
        bCreate = (Button) findViewById(R.id.CreateParkingButton);

        currentSpotID="";
        fBase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        tvTimeStart.setOnClickListener(this);
        tvTimeEnd.setOnClickListener(this);
        tvDateStart.setOnClickListener(this);
        tvDateEnd.setOnClickListener(this);

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean createdBool = createParkingSpot();
                if(createdBool && bookSpot()){
                    finish();
                }
                else{

                }
            }
        });
    }

    private boolean createParkingSpot(){
        if(!checkField()){
            return false;
        }
        currentUser = firebaseAuth.getCurrentUser();
        bookSpot();
        return true;
    }

    public boolean bookSpot() {
        String startDate = tvDateStart.getText().toString().trim();
        String endDate = tvDateEnd.getText().toString().trim();
        String startTime = tvTimeStart.getText().toString().trim();
        String endTime = tvTimeEnd.getText().toString().trim();

        //fBase.child("parkingspot").child(currentUser.getUid()).child(pSpotID).setValue(parkingSpace);

//        fBase.child("parkingspot").child(userID).child(pSpotID).child("counter").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long counter = (long) dataSnapshot.getValue();
//                counter += 1;
//                dataSnapshot.getRef().setValue(counter);
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//            }
//        });

        fBase.child("users").child(currentUser.getUid()).child("rented").child(pSpotID).child("id").setValue(pSpotID);
        fBase.child("users").child(currentUser.getUid()).child("rented").child(pSpotID).child("userId").setValue(userID);
        currentSpotID = pSpotID;
        return true;

//        ParkingSpace parkingSpace = new ParkingSpace(stAddress, City, State, ZipCode, rate,
//                startDate, endDate,startTime,endTime);
//        LatLng test = parkingSpace.getLocationFromAddress(this);
//        if(test == null){
//            Toast.makeText(this, "Address is not REAL",Toast.LENGTH_LONG).show();
//            return false;
//        }
//        else {
//        String parkingID = parkingSpace.getId();
//        fBase.child("parkingspot").child(currentUser.getUid()).child(parkingID).setValue(parkingSpace);
//        fBase.child("parkingspot").child(currentUser.getUid()).child(parkingID).child("counter").setValue(0);
//        currentSpotID=parkingID;
//        }
//        return true;
    }

    /**
     * Checks if Field entries are empty
     * @return true if filled out
     */
    public boolean checkField(){
        boolean clear = true;
        String StartDate = tvDateStart.getText().toString().trim();
        String EndDate = tvDateEnd.getText().toString().trim();
        String StartTime = tvTimeStart.getText().toString().trim();
        String EndTime = tvTimeEnd.getText().toString().trim();

        if(TextUtils.isEmpty(StartDate)){
            clear = false;
            Toast.makeText(BookParkingSpotActivity.this, "Missing Start Date",Toast.LENGTH_LONG ).show();
            return clear;
        }
        if(TextUtils.isEmpty(EndDate)){
            clear = false;
            Toast.makeText(BookParkingSpotActivity.this, "Missing End Date",Toast.LENGTH_LONG ).show();
            return clear;
        }
        if(TextUtils.isEmpty(StartTime)){
            clear = false;
            Toast.makeText(BookParkingSpotActivity.this, "Missing Start Time",Toast.LENGTH_LONG ).show();
            return clear;
        }
        if(TextUtils.isEmpty(EndTime)){
            clear = false;
            Toast.makeText(BookParkingSpotActivity.this, "Missing End Time",Toast.LENGTH_LONG ).show();
            return clear;
        }
        if(startDate.after(endDate)){
            clear = false;
            Toast.makeText(BookParkingSpotActivity.this, "End Time is before Start Time",Toast.LENGTH_LONG ).show();
            return clear;
        }

        return clear;
    }

    @Override
    public void onClick(View v) {
        mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentTime.get(Calendar.MONTH);
        int year = mcurrentTime.get(Calendar.YEAR);
        if(v == tvDateStart){
            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(BookParkingSpotActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    tvDateStart.setText((month+1)+"/"+dayOfMonth+"/"+year);
                    mcurrentTime.set(year, month, dayOfMonth, 0, 0);
                    startDate = mcurrentTime.getTime();
                }
            }, year, month,day);
            mDatePicker.getDatePicker().setMinDate(mcurrentTime.getTimeInMillis()-1000);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
        else if(v == tvDateEnd){
            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(BookParkingSpotActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    tvDateEnd.setText((month+1)+"/"+dayOfMonth+"/"+year);
                    mcurrentTime.set(year,month,dayOfMonth,0,1);
                    endDate = mcurrentTime.getTime();
                }

            }, year, month,day);
            mDatePicker.getDatePicker().setMinDate(mcurrentTime.getTimeInMillis()-1000);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
        else if(v == tvTimeStart){
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(BookParkingSpotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(selectedHour<10 & selectedMinute<10){
                        tvTimeStart.setText("0"+selectedHour + ":" + "0"+selectedMinute);}
                    else if(selectedMinute<10){
                        tvTimeStart.setText(selectedHour + ":" + "0"+selectedMinute);}
                    else if(selectedHour<10){
                        tvTimeStart.setText("0"+selectedHour + ":"+selectedMinute);}
                    else{
                        tvTimeStart.setText(selectedHour + ":" + selectedMinute);}
                    startDate.setHours(selectedHour);
                    startDate.setMinutes(selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        else if(v == tvTimeEnd){
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(BookParkingSpotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(selectedHour<10 & selectedMinute<10){
                        tvTimeEnd.setText("0"+selectedHour + ":" + "0"+selectedMinute);}
                    else if(selectedMinute<10){
                        tvTimeEnd.setText(selectedHour + ":" + "0"+selectedMinute);}
                    else if(selectedHour<10){
                        tvTimeEnd.setText("0"+selectedHour + ":"+selectedMinute);}
                    else{
                        tvTimeEnd.setText(selectedHour + ":" + selectedMinute);}
                    endDate.setHours(selectedHour);
                    endDate.setMinutes(selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }

    }

    public boolean checkCalendarDate(Date startdate, Date enddate){
        return startdate.before(enddate);
    }

}