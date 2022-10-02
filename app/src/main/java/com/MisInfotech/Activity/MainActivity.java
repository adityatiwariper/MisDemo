package com.MisInfotech.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.MisInfotech.Model.NotesModel;
import com.MisInfotech.Adpater.NoteAdapter;
import com.MisInfotech.R;
import com.MisInfotech.Utils.Settings;
import com.MisInfotech.Utils.Utils;
import com.MisInfotech.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding b;

    String Subject,Description,value;
    ArrayList<NotesModel> chatList= new ArrayList<NotesModel>();
    public static NoteAdapter noteAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        readMessages();

        b.recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        Intent intent = getIntent();
        if (intent != null) {
            value = intent.getStringExtra("UUID");
        }

        b.fabbtn.setOnClickListener(this);
        b.appversion.setText(getString(R.string.version));

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fabbtn:
                UpdateDailog(MainActivity.this,value);

        }

    }

    private void UpdateDailog(MainActivity mainActivity, String uuid) {

        final Dialog dialog = new Dialog(mainActivity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation;
        TextView TvCancel = dialog.findViewById(R.id.TvCancel);
        TextView TvSubmit = dialog.findViewById(R.id.TvSubmit);
        EditText etSubject = dialog.findViewById(R.id.etSubject);
        TvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subject = etSubject.getText().toString().trim();
                if(!TextUtils.isEmpty(Subject)){
                    sendmessage(mainActivity,Subject,Description,uuid);
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), "Enter The Notes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.getWindow().setLayout((int) (Utils.getScreenWidth(mainActivity) * 1), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void sendmessage(MainActivity mainActivity, String subject, String description, String value) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        Utils.getUserCred(mainActivity);
        String value1 = Settings.UUID;
        HashMap<String, Object> hashMap = new HashMap<>();
        if(value == null){
            hashMap.put("UUID",value1);
        }
        hashMap.put("UUID",value);
        hashMap.put("subject",subject);
        hashMap.put("status","0");
        hashMap.put("timestamp",timestamp);
        databaseReference.child("NOTES").child(subject).setValue(hashMap);
        DatabaseReference mReferenceCpf = FirebaseDatabase.getInstance().getReference("NOTES");
        mReferenceCpf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void readMessages() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("NOTES");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String x = dataSnapshot1.child("timestamp").getValue().toString();
                    long milliSeconds= Long.parseLong(x);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    String dateAsString = sdf.format (milliSeconds);
                    chatList.add(new NotesModel(dataSnapshot1.child("subject").getValue().toString(),dateAsString,dataSnapshot1.child("status").getValue().toString()));
                }
                Collections.sort(chatList, new Comparator<NotesModel>() {
                    @Override
                    public int compare(NotesModel item1, NotesModel item2) {
                        return item1.getDate().compareToIgnoreCase(item2.getDate());

                    }
                });
                Collections.reverse(chatList);
                noteAdapter = new NoteAdapter(chatList,MainActivity.this);
                b.recyclerview.setAdapter(noteAdapter);
                noteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
