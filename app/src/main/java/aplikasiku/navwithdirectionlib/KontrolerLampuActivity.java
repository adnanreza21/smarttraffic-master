package aplikasiku.navwithdirectionlib;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import aplikasiku.navwithdirectionlib.FDBMobel.Area;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;

public class KontrolerLampuActivity extends AppCompatActivity {

    Switch switchB, switchT, switchU, switchS;
    Switch switchPlaySound;
    RadioButton radioManual, radioAuto;
    RadioButton radioBR, radioBY, radioBG;
    RadioButton radioTR, radioTY, radioTG;
    RadioButton radioUR, radioUY, radioUG;
    RadioButton radioSR, radioSY, radioSG;

    FirebaseDatabase database;
    ProgressDialog pd1;

    KontrolerListTLAdapter.DataTL[] dataTL;
    KontrolerListTLAdapter adapter;
    RecyclerView rvListTL;

    Spinner spinnerTLarea;

    String uid_area, uid_tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontroler_lampu);

        database = FirebaseDatabase.getInstance();

        switchB = (Switch) findViewById(R.id.switchLampuBarat);
        switchT = (Switch) findViewById(R.id.switchLampuTimur);
        switchU = (Switch) findViewById(R.id.switchLampuUtara);
        switchS = (Switch) findViewById(R.id.switchLampuSelatan);
        switchPlaySound = (Switch) findViewById(R.id.switchTLplaysound);

        radioManual = (RadioButton) findViewById(R.id.radioKontrolerManual);
        radioAuto = (RadioButton) findViewById(R.id.radioKontrolerOtomatis);

        radioBR = (RadioButton) findViewById(R.id.radioLampuBaratRed);
        radioBY = (RadioButton) findViewById(R.id.radioLampuBaratYellow);
        radioBG = (RadioButton) findViewById(R.id.radioLampuBaratGreen);

        radioTR = (RadioButton) findViewById(R.id.radioLampuTimurRed);
        radioTY = (RadioButton) findViewById(R.id.radioLampuTimurYellow);
        radioTG = (RadioButton) findViewById(R.id.radioLampuTimurGreen);

        radioUR = (RadioButton) findViewById(R.id.radioLampuUtaraRed);
        radioUY = (RadioButton) findViewById(R.id.radioLampuUtaraYellow);
        radioUG = (RadioButton) findViewById(R.id.radioLampuUtaraGreen);

        radioSR = (RadioButton) findViewById(R.id.radioLampuSelatanRed);
        radioSY = (RadioButton) findViewById(R.id.radioLampuSelatanYellow);
        radioSG = (RadioButton) findViewById(R.id.radioLampuSelatanGreen);

        rvListTL = (RecyclerView) findViewById(R.id.recyclerViewListLampuKontroler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvListTL.setHasFixedSize(true);
        rvListTL.setLayoutManager(layoutManager);

        spinnerTLarea = (Spinner) findViewById(R.id.spinnerKontrolerTLarea);
        spinnerTLarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setComponentEnable(false);
                radioManual.setEnabled(false);
                radioAuto.setEnabled(false);

                database.getReference("area").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot area : dataSnapshot.getChildren()){
                            Area x = area.getValue(Area.class);
                            if(x.area_name.equals(spinnerTLarea.getSelectedItem().toString())){
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

        initListener();
        initListenerRadioRYG();
    }

    private void initGetListTL() {
        database.getReference("new-tl").child(uid_area).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataTL = new KontrolerListTLAdapter.DataTL[(int) dataSnapshot.getChildrenCount()];
                int count=0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    NewSkemaTL tl = data.getValue(NewSkemaTL.class);
                    dataTL[count] = new KontrolerListTLAdapter.DataTL();
                    dataTL[count].TLname = tl.TLname;
                    dataTL[count].TLloc = tl.TLloc;
                    dataTL[count].uid_tl = data.getKey();
                    count++;
                }

                adapter = new KontrolerListTLAdapter(dataTL, new KontrolerListTLAdapter.ViewHolderClickListener() {
                    @Override
                    public void onViewHolderClick(View v, int position) {
                        for(int i=0;i<dataTL.length;i++){
                            dataTL[i].isSelected = false;
                        }
                        dataTL[position].isSelected = true;
                        adapter.setDataTL(dataTL);
                        rvListTL.setAdapter(adapter);
                        uid_tl = dataTL[position].uid_tl;
                        Toast.makeText(getApplicationContext(), "uid_tl " + uid_tl, Toast.LENGTH_SHORT).show();
                        dataControlerListener();
                    }
                });
                rvListTL.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initListenerRadioRYG() {
        radioBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioBR.isChecked()){
                    radioBY.setChecked(false);
                    radioBG.setChecked(false);
                    chooseLampu("b","r");
                }
            }
        });
        radioBY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioBY.isChecked()){
                    radioBR.setChecked(false);
                    radioBG.setChecked(false);
                    chooseLampu("b","y");
                }
            }
        });
        radioBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioBG.isChecked()){
                    radioBY.setChecked(false);
                    radioBR.setChecked(false);
                    chooseLampu("b","g");
                }
            }
        });
        //===============================================//

        radioTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioTR.isChecked()){
                    radioTY.setChecked(false);
                    radioTG.setChecked(false);
                    chooseLampu("t","r");
                }
            }
        });
        radioTY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioTY.isChecked()){
                    radioTR.setChecked(false);
                    radioTG.setChecked(false);
                    chooseLampu("t","y");
                }
            }
        });
        radioTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioTG.isChecked()){
                    radioTY.setChecked(false);
                    radioTR.setChecked(false);
                    chooseLampu("t","g");
                }
            }
        });
        //=======================================//

        radioUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioUR.isChecked()){
                    radioUY.setChecked(false);
                    radioUG.setChecked(false);
                    chooseLampu("u","r");
                }
            }
        });
        radioUY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioUY.isChecked()){
                    radioUR.setChecked(false);
                    radioUG.setChecked(false);
                    chooseLampu("u","y");
                }
            }
        });
        radioUG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioUG.isChecked()){
                    radioUY.setChecked(false);
                    radioUR.setChecked(false);
                    chooseLampu("u","g");
                }
            }
        });
        //==============================//

        radioSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioSR.isChecked()){
                    radioSY.setChecked(false);
                    radioSG.setChecked(false);
                    chooseLampu("s","r");
                }
            }
        });
        radioSY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioSY.isChecked()){
                    radioSR.setChecked(false);
                    radioSG.setChecked(false);
                    chooseLampu("s","y");
                }
            }
        });
        radioSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioSG.isChecked()){
                    radioSY.setChecked(false);
                    radioSR.setChecked(false);
                    chooseLampu("s","g");
                }
            }
        });
    }

    private void initListener() {
        radioManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioManual.isChecked()){
                    pd1 = ProgressDialog.show(KontrolerLampuActivity.this, "Mengubah mode", "Harap tunggu...",false,false);
                    database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLstate").setValue(false, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            pd1.dismiss();
                            if(databaseError==null){
                                radioAuto.setChecked(false);
                                dataControlerListener();
                            }
                        }
                    });
                }
            }
        });

        radioAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioAuto.isChecked()){
                    pd1 = ProgressDialog.show(KontrolerLampuActivity.this, "Mengubah mode", "Harap tunggu...",false,false);
                    database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLstate").setValue(true, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            pd1.dismiss();
                            if(databaseError==null){
                                radioManual.setChecked(false);
                                setComponentEnable(false);
                            }
                        }
                    });
                }
            }
        });

        switchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchB.isChecked()){setLampu("b", true);}
                else{setLampu("b", false);}
            }
        });

        switchT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchT.isChecked()){setLampu("t", true);}
                else{setLampu("t", false);}
            }
        });

        switchU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchU.isChecked()){setLampu("u", true);}
                else{setLampu("u", false);}
            }
        });

        switchS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchS.isChecked()){setLampu("s", true);}
                else{setLampu("s", false);}
            }
        });

        switchPlaySound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchPlaySound.isChecked()){
                    setPlaySound(true);
                }
                else{
                    setPlaySound(false);
                }
            }
        });
    }

    private void setPlaySound(boolean playSound) {
        pd1 = ProgressDialog.show(KontrolerLampuActivity.this, "Memperbarui kondisi", "Harap tunggu...",false,false);

        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLplaysound").setValue(playSound, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                pd1.dismiss();
                if(databaseError==null){
                    Toast.makeText(getApplicationContext(), "Set sound Ok", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Set sound Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setComponentEnable(boolean setEnable){
        switchB.setEnabled(setEnable);
        switchT.setEnabled(setEnable);
        switchU.setEnabled(setEnable);
        switchS.setEnabled(setEnable);
        switchPlaySound.setEnabled(setEnable);

        radioBR.setEnabled(setEnable);radioBY.setEnabled(setEnable);radioBG.setEnabled(setEnable);
        radioTR.setEnabled(setEnable);radioTY.setEnabled(setEnable);radioTG.setEnabled(setEnable);
        radioUR.setEnabled(setEnable);radioUY.setEnabled(setEnable);radioUG.setEnabled(setEnable);
        radioSR.setEnabled(setEnable);radioSY.setEnabled(setEnable);radioSG.setEnabled(setEnable);
    }

    public void setLampu(String btus, boolean stat){
        pd1 = ProgressDialog.show(KontrolerLampuActivity.this, "Memperbarui kondisi", "Harap tunggu...",false,false);

        if(btus.equals("b")){ btus="TLb"; }
        else if(btus.equals("t")){ btus="TLt"; }
        else if(btus.equals("u")){ btus="TLu"; }
        else if(btus.equals("s")){ btus="TLs"; }
        final String newtl = btus;
        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLpos").child(btus).child("stat").setValue(stat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                pd1.dismiss();
                if(databaseError==null){
                    Toast.makeText(getApplicationContext(), newtl + " Ok", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), newtl + " Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getLampu(String btus, final Switch switchLamp){
        if(btus.equals("b")){ btus="TLb"; }
        else if(btus.equals("t")){ btus="TLt"; }
        else if(btus.equals("u")){ btus="TLu"; }
        else if(btus.equals("s")){ btus="TLs"; }

        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLpos").child(btus).child("stat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().toString().equals("true")) {
                        switchLamp.setChecked(true);
                    } else {
                        switchLamp.setChecked(false);
                    }
                }
                else{
                    switchLamp.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void chooseLampu(String btus, String ryg){
        pd1 = ProgressDialog.show(KontrolerLampuActivity.this, "Memilih lampu", "Harap tunggu...",false,false);

        if(btus.equals("b")){ btus="TLb"; }
        else if(btus.equals("t")){ btus="TLt"; }
        else if(btus.equals("u")){ btus="TLu"; }
        else if(btus.equals("s")){ btus="TLs"; }

        if(ryg.equals("r")){ ryg="red"; }
        else if(ryg.equals("y")){ ryg="yellow"; }
        else if(ryg.equals("g")){ ryg="green"; }
        final String ulamp = ryg;

        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLpos").child(btus).child("ulamp").setValue(ryg, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                pd1.dismiss();
                if(databaseError==null){
                    Toast.makeText(getApplicationContext(), ulamp + " Ok", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), ulamp + " Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getChooseLampu(String btus){
        if(btus.equals("b")){ btus="TLb"; }
        else if(btus.equals("t")){ btus="TLt"; }
        else if(btus.equals("u")){ btus="TLu"; }
        else if(btus.equals("s")){ btus="TLs"; }

        final String btusfin = btus;

        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLpos").child(btus).child("ulamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equals("red")){
                        if(btusfin.equals("TLb")){
                            radioBR.setChecked(true);
                            radioBY.setChecked(false);
                            radioBG.setChecked(false);
                        }
                        else if(btusfin.equals("TLt")){
                            radioTR.setChecked(true);
                            radioTY.setChecked(false);
                            radioTG.setChecked(false);
                        }
                        else if(btusfin.equals("TLu")){
                            radioUR.setChecked(true);
                            radioUY.setChecked(false);
                            radioUG.setChecked(false);
                        }
                        else if(btusfin.equals("TLs")){
                            radioSR.setChecked(true);
                            radioSY.setChecked(false);
                            radioSG.setChecked(false);
                        }
                    }
                    else if(dataSnapshot.getValue().toString().equals("yellow")){
                        if(btusfin.equals("TLb")){
                            radioBR.setChecked(false);
                            radioBY.setChecked(true);
                            radioBG.setChecked(false);
                        }
                        else if(btusfin.equals("TLt")){
                            radioTR.setChecked(false);
                            radioTY.setChecked(true);
                            radioTG.setChecked(false);
                        }
                        else if(btusfin.equals("TLu")){
                            radioUR.setChecked(false);
                            radioUY.setChecked(true);
                            radioUG.setChecked(false);
                        }
                        else if(btusfin.equals("TLs")){
                            radioSR.setChecked(false);
                            radioSY.setChecked(true);
                            radioSG.setChecked(false);
                        }
                    }
                    else if(dataSnapshot.getValue().toString().equals("green")){
                        if(btusfin.equals("TLb")){
                            radioBR.setChecked(false);
                            radioBY.setChecked(false);
                            radioBG.setChecked(true);
                        }
                        else if(btusfin.equals("TLt")){
                            radioTR.setChecked(false);
                            radioTY.setChecked(false);
                            radioTG.setChecked(true);
                        }
                        else if(btusfin.equals("TLu")){
                            radioUR.setChecked(false);
                            radioUY.setChecked(false);
                            radioUG.setChecked(true);
                        }
                        else if(btusfin.equals("TLs")){
                            radioSR.setChecked(false);
                            radioSY.setChecked(false);
                            radioSG.setChecked(true);
                        }
                    }
                }
                else{
                    if(btusfin.equals("TLb")){
                        radioBR.setEnabled(false);
                        radioBY.setEnabled(false);
                        radioBG.setEnabled(false);
                    }
                    else if(btusfin.equals("TLt")){
                        radioTR.setEnabled(false);
                        radioTY.setEnabled(false);
                        radioTG.setEnabled(false);
                    }
                    else if(btusfin.equals("TLu")){
                        radioUR.setEnabled(false);
                        radioUY.setEnabled(false);
                        radioUG.setEnabled(false);
                    }
                    else if(btusfin.equals("TLs")){
                        radioSR.setEnabled(false);
                        radioSY.setEnabled(false);
                        radioSG.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void dataControlerListener(){
        radioManual.setEnabled(true);
        radioAuto.setEnabled(true);
        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLstate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true")){
                    radioAuto.setChecked(true);
                    radioManual.setChecked(false);
                    setComponentEnable(false);
                }
                else{
                    radioAuto.setChecked(false);
                    radioManual.setChecked(true);
                    setComponentEnable(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference("new-tl").child(uid_area).child(uid_tl).child("TLplaysound").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true")){
                    switchPlaySound.setChecked(true);
                }
                else{
                    switchPlaySound.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getLampu("b", switchB);
        getLampu("t", switchT);
        getLampu("u", switchU);
        getLampu("s", switchS);

        getChooseLampu("b");
        getChooseLampu("t");
        getChooseLampu("u");
        getChooseLampu("s");
    }
}