package com.example.vacationscheduler.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationscheduler.R;
import com.example.vacationscheduler.database.Repository;
import com.example.vacationscheduler.entities.Excursion;
import com.example.vacationscheduler.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    String startDate;
    String endDate;
    int vacationID;
    EditText editTitle;
    EditText editHotel;
    TextView edit_start_date;
    TextView edit_end_date;
    Repository repository;
    Vacation currentVacation;

    int numExcursions;

    DatePickerDialog.OnDateSetListener start_Date;
    DatePickerDialog.OnDateSetListener end_Date;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        FloatingActionButton floatingActionButton2 = findViewById(R.id.floatingActionButton2);

        editTitle = findViewById(R.id.title);
        editHotel = findViewById(R.id.hotel);
        edit_start_date = findViewById(R.id.startDate);
        edit_end_date = findViewById(R.id.endDate);

        vacationID = getIntent().getIntExtra("id", -1);
        title = getIntent().getStringExtra("title");
        hotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("start date");
        endDate = getIntent().getStringExtra("end date");

        editTitle.setText(title);
        editHotel.setText(hotel);
        edit_start_date.setText(startDate);
        edit_end_date.setText(endDate);

        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyDates();
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationID", vacationID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView=findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e:repository.getAllExcursions()){
            if (e.getVacationID()==vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursion(filteredExcursions );

        start_Date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();

            }
        };


        end_Date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();

            }
        };

        edit_start_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date date = new Date();
                String info=edit_start_date.getText().toString();
                //Vacation vacationDate;
                if(info.equals("")) info = date.toString();
                try{
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, start_Date, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edit_end_date.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Date date = new Date();
                String info=edit_end_date.getText().toString();
                //Vacation vacationDate;
                if(info.equals("")) info = date.toString();
                try{
                    myCalendarEnd.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, end_Date, myCalendarEnd
                        .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }





    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit_start_date.setText(sdf.format(myCalendarStart.getTime()));
        edit_end_date.setText(sdf.format(myCalendarEnd.getTime()));

    }






    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;}
        if (item.getItemId()==R.id.vacationsave){
            //verifyDates();
            Vacation vacation;
            if (vacationID == -1){
                if (repository.getmAllVacations().size()==0) {
                    vacationID = 1;
                }
                else vacationID = repository.getmAllVacations().get(repository
                        .getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(),
                        edit_start_date.getText().toString(), edit_end_date.getText().toString());
                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                try {
                    Date vacayStart = sdf.parse(vacation.getStartDate());
                    Date vacayEnd = sdf.parse(vacation.getEndDate());
                    if(vacayEnd.before(vacayStart)) {
                        Toast.makeText(VacationDetails.this, "Vacation end date must be after the vacation start date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else if (vacayStart.after(vacayEnd)) {
                        Toast.makeText(this, "Vacation start date is after end date.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        repository.insert(vacation);
                        this.finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else {
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(),
                        edit_start_date.getText().toString(), edit_end_date.getText().toString());
                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                try {
                    Date vacayStart = sdf.parse(vacation.getStartDate());
                    Date vacayEnd = sdf.parse(vacation.getEndDate());
                    if(vacayEnd.before(vacayStart)) {
                        Toast.makeText(VacationDetails.this, "Vacation end date must be after the vacation start date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else if (vacayStart.after(vacayEnd)) {
                        Toast.makeText(this, "Vacation start date is after end date.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        repository.update(vacation);
                        this.finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        // do for when deleting a vacation
        if (item.getItemId() == R.id.vacationdelete){
            for (Vacation vacation:repository.getmAllVacations()){
                if (vacation.getVacationID()==vacationID)currentVacation = vacation;
            }
            numExcursions=0;
            for (Excursion excursion:repository.getAllExcursions()){
                if (excursion.getVacationID()==vacationID)++numExcursions;
            }
            if (numExcursions==0){
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getTitle() + " was deleted", Toast.LENGTH_LONG).show();
                this.finish();
            }
            else{
                Toast.makeText(this, "Can't delete a vacation with excursions", Toast.LENGTH_SHORT).show();
            }
        }

        if (item.getItemId() == R.id.vacationShare) {
            Intent sentIntent= new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, "Vacation: " + editTitle.getText().toString() + "\n"
                    + "Hotel: " + editHotel.getText().toString()+ "\n" + "Start Date: " + edit_start_date.getText().toString()+ "\n"
                    + "End Date: " + edit_end_date.getText().toString());
            sentIntent.setType("text/plain");
            Intent shareIntent=Intent.createChooser(sentIntent,null);
            startActivity(shareIntent);
            return true;

        }

        if (item.getItemId() == R.id.vacationNotify) {
            String dateFromScreen1 = edit_start_date.getText().toString();
            String dateFromScreen2 = edit_end_date.getText().toString();
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat, Locale.US);
            Date startDate = null;
            Date endDate = null;
            Date nowDate = null;
            try {
                startDate = sdf1.parse(dateFromScreen1);
                endDate = sdf1.parse(dateFromScreen2);
                nowDate = new Date();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert startDate != null;
            long trigger = startDate.getTime();
            assert endDate != null;
            long trigger2 = endDate.getTime();
            long trigger3 = nowDate.getTime();
            if (startDate.equals(nowDate) || nowDate.before(endDate)) {
                Intent intent1 = new Intent(VacationDetails.this, MyReceiver2.class);
                intent1.putExtra("vacation Time", editTitle.getText().toString() + " vacation is starting on  " + startDate);
                PendingIntent sender=PendingIntent.getBroadcast(VacationDetails.this,++MainActivity.numAlert,
                        intent1,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger,sender);
                return true;
            }

            else {
                Intent intent2 = new Intent(VacationDetails.this, MyReceiver2.class);
                intent2.putExtra("vacation Time", editTitle.getText().toString() + " vacation is ending on " + endDate);
                PendingIntent sender2=PendingIntent.getBroadcast(VacationDetails.this,++MainActivity.numAlert,
                        intent2,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager2.set(AlarmManager.RTC_WAKEUP, trigger2,sender2);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView=findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter=new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e:repository.getAllExcursions()){
            if (e.getVacationID()==vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursion(filteredExcursions );
    }



}