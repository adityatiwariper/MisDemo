package com.MisInfotech.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MisInfotech.Model.NotesModel;
import com.MisInfotech.Adpater.NoteAdapter;
import com.MisInfotech.R;
import com.MisInfotech.Utils.Settings;
import com.MisInfotech.Utils.Utils;
import com.MisInfotech.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding b;

    String Subject,Description,value,picturePath = "",encodedImage="";
    ArrayList<NotesModel> chatList= new ArrayList<NotesModel>();
    public static NoteAdapter noteAdapter;
    private static final int selectPicture = 1,capturePicture = 100;
    Bitmap bitmap = null;
    Bitmap rotatedBitmap;
    Uri fileUri;
    public static Activity activity;


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
                    chatList.add(new NotesModel(dataSnapshot1.child("subject").getValue().toString(),dataSnapshot1.child("description").getValue().toString(),dateAsString,dataSnapshot1.child("status").getValue().toString(),dataSnapshot1.child("image").getValue().toString()));
                }
                Collections.sort(chatList, new Comparator<NotesModel>() {
                    @Override
                    public int compare(NotesModel item1, NotesModel item2) {
                        return item1.getDate().compareToIgnoreCase(item2.getDate());

                    }
                });
                noteAdapter = new NoteAdapter(chatList,MainActivity.this);
                b.recyclerview.setAdapter(noteAdapter);
                noteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        activity = MainActivity.this;

        readMessages();
        swipefunction();

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
        EditText etDescription = dialog.findViewById(R.id.etDescription);
        ImageView upload = dialog.findViewById(R.id.upload);
        TvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //photoPickerIntent.putExtra("crop", "true");
                startActivityForResult(photoPickerIntent, selectPicture);



            }
        });
        TvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subject = etSubject.getText().toString().trim();
                Description = etDescription.getText().toString().trim();
                if(!TextUtils.isEmpty(Subject)){
                    if(!TextUtils.isEmpty(Description)){
                        sendmessage(mainActivity,Subject,Description,uuid);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(getApplicationContext(), "Enter The Subject", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Enter The Description", Toast.LENGTH_SHORT).show();
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
        hashMap.put("description",description);
        hashMap.put("status","0");
        hashMap.put("image",encodedImage);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == capturePicture) {
            if (resultCode == RESULT_OK) {

                if (fileUri == null) {

                    picturePath = fileUri.getPath();

                } else {
                    if (!fileUri.equals(""))
                        picturePath = fileUri.getPath();
                }


                String selectedImagePath = picturePath;


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                Matrix matrix = new Matrix();
                matrix.postRotate(Utils.getImageOrientation(picturePath));
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();

                encodedImage = Utils.getEncoded64ImageStringFromBitmap(rotatedBitmap);

            }

        } else if (requestCode == selectPicture) {
            if (data != null) {

                try {
                    //get the Uri for the captured image
                    Uri picUri = data.getData();

                    Uri contentURI = data.getData();

                    if (contentURI.toString().contains("content://com.google.android.apps.photos")) {
                        try {
                            bitmap = getBitmapFromUri(contentURI);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = MainActivity.this.getContentResolver().query(contentURI, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        System.out.println("Image Path : " + picturePath);
                        cursor.close();
                        String filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

                        String ext = Utils.getFileType(picturePath);

                        String selectedImagePath = picturePath;

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);
                        final int REQUIRED_SIZE = 500;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                    }

                    Matrix matrix = new Matrix();
                    matrix.postRotate(Utils.getImageOrientation(picturePath));
                    rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);

                    encodedImage = Utils.getEncoded64ImageStringFromBitmap(rotatedBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(MainActivity.this, "Unable to Select the Image", Toast.LENGTH_SHORT).show();

            }

        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void swipefunction() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                NotesModel deletedCourse = chatList.get(viewHolder.getAdapterPosition());

                int position = viewHolder.getAdapterPosition();

                chatList.remove(viewHolder.getAdapterPosition());

                noteAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                Snackbar.make(b.recyclerview, deletedCourse.getSubject(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatList.add(position, deletedCourse);

                        noteAdapter.notifyItemInserted(position);
                    }
                }).show();
            }
        }).attachToRecyclerView(b.recyclerview);
    }
}
