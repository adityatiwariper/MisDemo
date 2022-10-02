package com.MisInfotech.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.MisInfotech.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Utils {

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static void setLogin(Context context, boolean isLoggedIn) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("com.ACIsLoggedIn", isLoggedIn);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("com.ACIsLoggedIn", false);
    }

    public static void setUserCredential(Context context, String user_uid) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DEMOUUID", user_uid);
        editor.commit();
    }
    public static void getUserCred(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String UUID = prefs.getString("DEMOUUID", "");
        Settings.UUID = UUID;
    }

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public static String getFileType(String path) {
        String fileType = null;
        fileType = path.substring(path.indexOf('.', path.lastIndexOf('/')) + 1).toLowerCase();
        return fileType;
    }

    public static void ShowDetails(Context context, String image, String Notes, String description, String date) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.detials_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.windowAnimations = R.style.DialogAnimation;
        ImageView upload = dialog.findViewById(R.id.ivphoto);
        TextView tvNotes = dialog.findViewById(R.id.tvNotes);
        TextView tvdescription = dialog.findViewById(R.id.tvdescription);
        TextView tvdate = dialog.findViewById(R.id.tvdate);
        byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        upload.setImageBitmap(bmp);
        tvNotes.setText(Notes);
        tvdescription.setText(description);
        tvdate.setText(date);
        dialog.getWindow().setLayout((int) (Utils.getScreenWidth((Activity) context) * 1), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


}
