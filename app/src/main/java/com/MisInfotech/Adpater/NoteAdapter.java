package com.MisInfotech.Adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MisInfotech.Model.NotesModel;
import com.MisInfotech.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    public ArrayList<NotesModel> arrayList;
    public Context context;

    public NoteAdapter(ArrayList<NotesModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.checkBox.setChecked(false);
        if(arrayList.get(position).getStatus().equals("1")){
            holder.tvnotes.setText(arrayList.get(position).getSubject());
            holder.tvnotes.setPaintFlags(holder.tvnotes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.tvnotes.setText(arrayList.get(position).getSubject());
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isChecked =  holder.checkBox.isChecked();
                FirebaseDatabase  database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabaseRef = database.getReference();
                if(isChecked){
                    mDatabaseRef.child("NOTES").child(arrayList.get(position).getSubject()).child("status").setValue("1");
                }else{
                    mDatabaseRef.child("NOTES").child(arrayList.get(position).getSubject()).child("status").setValue("0");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvnotes;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemview){
            super(itemview);
            tvnotes = itemview.findViewById(R.id.tvnotes);
            checkBox = itemview.findViewById(R.id.checkBox);
        }
    }
}
