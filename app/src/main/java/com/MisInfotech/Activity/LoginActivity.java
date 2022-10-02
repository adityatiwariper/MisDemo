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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.MisInfotech.R;
import com.MisInfotech.Utils.Utils;
import com.MisInfotech.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding b;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        mAuth = FirebaseAuth.getInstance();
        b.loginbtn.setOnClickListener(this);
        b.TvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.loginbtn:
                Validate();
                break;

            case R.id.TvRegister:

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                break;
        }

    }

    private void Validate() {
        if(!TextUtils.isEmpty(b.Etemail.getText().toString())){
            if(!TextUtils.isEmpty(b.etpassword.getText().toString())){
                Login(LoginActivity.this,b.Etemail.getText().toString(),b.etpassword.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(),"Please Enter the Password",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please Enter the Email",Toast.LENGTH_SHORT).show();
        }
    }

    private void Login(LoginActivity loginActivity, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String user_id = user.getUid();
                Utils.setUserCredential(LoginActivity.this, user_id);
                Utils.setLogin(LoginActivity.this, true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("UUID",user_id);
                startActivity(intent);
                LoginActivity.this.finish();

            } else {
                Toast.makeText(LoginActivity.this, "Please Check Your login Credentials", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void hideStatusBarNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LoginActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            LoginActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            LoginActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            LoginActivity.this.getWindow().setNavigationBarColor(ContextCompat.getColor(LoginActivity.this, R.color.black));
            LoginActivity.this.getWindow().setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
