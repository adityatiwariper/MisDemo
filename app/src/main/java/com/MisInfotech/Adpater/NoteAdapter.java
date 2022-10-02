package com.MisInfotech.Adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.MisInfotech.Model.NotesModel;
import com.MisInfotech.R;
import com.MisInfotech.Utils.Utils;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(arrayList.get(position).getStatus().equals("1")){
            holder.tvnotes.setText(arrayList.get(position).getSubject());
            holder.tvnotes.setPaintFlags(holder.tvnotes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.tvnotes.setText(arrayList.get(position).getSubject());
        }

        final CharSequence text = holder.tvnotes.getText();
        final SpannableString spannableString = new SpannableString( text );
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvnotes.setText(spannableString, TextView.BufferType.SPANNABLE);

        holder.tvdescription.setText(arrayList.get(position).getDescription());
        String s = "<b>"+arrayList.get(position).getDescription()+"</b>"+"<i>"+arrayList.get(position).getDescription()+"</i>"+"<u>"+arrayList.get(position).getDescription()+"</u>!";
        holder.tvdescription.setText(Html.fromHtml(s,Html.FROM_HTML_MODE_COMPACT));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ShowDetails(context,arrayList.get(position).getImage(),arrayList.get(position).getSubject(),arrayList.get(position).getDescription(),arrayList.get(position).getDate());
            }
        });


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

        public TextView tvnotes,tvdescription;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemview){
            super(itemview);
            tvnotes = itemview.findViewById(R.id.tvnotes);
            tvdescription = itemview.findViewById(R.id.tvdescription);
            checkBox = itemview.findViewById(R.id.checkBox);
        }
    }
}
