package com.example.vacationscheduler.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    String startDate;
    String endDate;
    int vacationID;
    EditText editTitle;
    EditText editHotel;
    EditText edit_start_date;
    EditText edit_end_date;
    Repository repository;

    Vacation currentVacation;

    int numExcursions;

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
        endDate = getIntent().getStringExtra("start date");
        startDate = getIntent().getStringExtra("end date");
        editTitle.setText(title);
        editHotel.setText(hotel);
        edit_start_date.setText(startDate);
        edit_end_date.setText(endDate);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView=findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter.setExcursion(repository.getAllExcursions());
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==R.id.vacationsave){
            Vacation vacation;
            if (vacationID == -1){
                if (repository.getmAllVacations().size()==0) {
                    vacationID = 1;
                }
                else vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(),
                        edit_start_date.getText().toString(), edit_end_date.getText().toString());
                repository.insert(vacation);
                this.finish();

            }
            else {
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(),
                        edit_start_date.getText().toString(), edit_end_date.getText().toString());
                repository.update(vacation);
                this.finish();
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
                VacationDetails.this.finish();
            }
            else{
                Toast.makeText(this, "Can't delete a vacation with excursions", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
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
        excursionAdapter.setExcursion(filteredExcursions);
    }

}