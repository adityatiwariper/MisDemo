package com.MisInfotech.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.MisInfotech.R;
import com.MisInfotech.Utils.Utils;
import com.MisInfotech.databinding.ActivitySplashBinding;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity  {

    ActivitySplashBinding b;
    Activity activity;
    List<String> permissionsList = new ArrayList<>();
    public static boolean askOnceAgain = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        activity = SplashActivity.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionsList.clear();
        if (askOnceAgain) {
            askOnceAgain = false;
            checkPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99: {
                boolean required = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                        required = false;
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                        required = true;
                    }
                }
                if (required) {
                    MyPermissionErrorDialog(activity);
                } else {
                    goToNextScreen();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void checkPermissions() {
        int hasStorageReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasStorageWritePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> permissions = new ArrayList<>();
        if (hasStorageReadPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (hasStorageWritePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissions.isEmpty()) {
            MyPermissionDialog(activity, permissions);
        } else {
            goToNextScreen();
        }
    }

    public static void MyPermissionDialog(final Activity activity, final List<String> permissions) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.permission_error_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation;
        TextView optiontitle = (TextView) dialog.findViewById(R.id.optiontitle);
        final TextView optiontext = (TextView) dialog.findViewById(R.id.optiontext);
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
        optiontitle.setText("Permission!");
        optiontext.setText("Please allow required permissions for best performance.");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), 99);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setLayout((int) (Utils.getScreenWidth(activity) * 1), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    private void goToNextScreen() {
        if(Utils.isLoggedIn(SplashActivity.this)){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            this.finish();
        }else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            this.finish();
        }
    }

    public static void MyPermissionErrorDialog(final Activity context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.permission_error_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation;
        TextView optiontitle = (TextView) dialog.findViewById(R.id.optiontitle);
        final TextView optiontext = (TextView) dialog.findViewById(R.id.optiontext);
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
        optiontitle.setText("Permission Error!");
        optiontext.setText("You need to allow access to some permissions to give best performance of this Neo Fitnes App..");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                askOnceAgain = true;
                dialog.dismiss();
            }
        });
        dialog.getWindow().setLayout((int) (Utils.getScreenWidth(context) * 1), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

}
