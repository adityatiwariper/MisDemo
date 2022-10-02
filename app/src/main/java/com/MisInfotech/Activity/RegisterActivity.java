package com.MisInfotech.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.MisInfotech.R;
import com.MisInfotech.Utils.Utils;
import com.MisInfotech.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityRegisterBinding b;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        mAuth = FirebaseAuth.getInstance();

        b.signinbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signinbtn:

                valiadte();
        }

    }

    private void valiadte() {
        if(!TextUtils.isEmpty(b.etemail.getText().toString())){
            if(!TextUtils.isEmpty(b.etpassword.getText().toString())){
                Register(RegisterActivity.this,b.etemail.getText().toString(),b.etpassword.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(),"Please Enter the Password",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please Enter the Email",Toast.LENGTH_SHORT).show();
        }
    }

    private void Register(RegisterActivity registerActivity, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.e("LLLLL","LLLLL ");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String user_id = user.getUid();
                    Utils.setUserCredential(registerActivity, user_id);
                    Intent intent = new Intent(registerActivity, MainActivity.class);
                    intent.putExtra("UUID",user_id);
                    registerActivity.startActivity(intent);
                    RegisterActivity.this.finish();
                }else {
                    Toast.makeText(RegisterActivity.this, "Please Try Once Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideStatusBarNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RegisterActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            RegisterActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            RegisterActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            RegisterActivity.this.getWindow().setNavigationBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.black));
            RegisterActivity.this.getWindow().setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.white));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
