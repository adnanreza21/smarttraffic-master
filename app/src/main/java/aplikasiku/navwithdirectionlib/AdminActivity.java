package aplikasiku.navwithdirectionlib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import aplikasiku.navwithdirectionlib.FDBMobel.Area;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;

public class AdminActivity extends AppCompatActivity {

    Button butKontrolerLampu, butInsertTL, butInsertRS;
    Spinner spinnerAdminTLArea;
    RecyclerView recyclerViewTLlist;
    FirebaseDatabase database;

    String uid_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        database = FirebaseDatabase.getInstance();

        butKontrolerLampu = (Button) findViewById(R.id.buttonAdminKontrolLampu);
        butInsertTL = (Button) findViewById(R.id.buttonAdminInsertTL);
        butInsertRS = (Button) findViewById(R.id.buttonAdminInsertRS);

        spinnerAdminTLArea = (Spinner) findViewById(R.id.spinnerAdminTLarea);
        recyclerViewTLlist = (RecyclerView) findViewById(R.id.recyclerview_admin_tllist);
        recyclerViewTLlist.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewTLlist.setLayoutManager(layoutManager);

        butKontrolerLampu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, KontrolerLampuActivity.class));
            }
        });

        butInsertTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, SkemaTLbaruActivity.class));
            }
        });

        butInsertRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, InsertRsFirebase.class));
            }
        });

        spinnerAdminTLArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                database.getReference("area").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot area : dataSnapshot.getChildren()){
                            Area x = area.getValue(Area.class);
                            if(x.area_name.equals(spinnerAdminTLArea.getSelectedItem().toString())){
                                uid_area = area.getKey();
                                initGetListTL();
                                Toast.makeText(getApplicationContext(), "uid_area " + uid_area, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initGetListTL() {
        database.getReference("new-tl").child(uid_area).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NewSkemaTL[] dataTL = new NewSkemaTL[(int) dataSnapshot.getChildrenCount()];
                int count=0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    NewSkemaTL tl = data.getValue(NewSkemaTL.class);
                    dataTL[count] = tl;
                    count++;
                }

                AdminListTLAdapter adapter = new AdminListTLAdapter(dataTL);
                recyclerViewTLlist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
