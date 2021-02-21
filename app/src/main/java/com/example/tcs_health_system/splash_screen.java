package com.example.tcs_health_system;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class splash_screen extends AppCompatActivity {
Thread splash;
Intent intent;

    public static final String PREFS_NAME = "login_page";

    //SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
     String names;//pref.getString("user",null);
    //final boolean islogg=pref.getBoolean("islog",false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       //profile = Profile.getCurrentProfile().getCurrentProfile();
        SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
        final String name =pref.getString("user",null);
        final boolean islogg=pref.getBoolean("islog",false);
        names=name;


        if(isWorkingInternetPresent()){

            splash=new Thread(){
                @Override
                public void run() {
                    try{
                        sleep(5000);
                    }
                    catch (Exception e){

                    }finally {
                        if (name!=null) {
                          intent=new Intent(splash_screen.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                       else {
                        intent = new Intent(splash_screen.this, Login.class);
                        startActivity(intent);
                        finish();
                        }
                    }

                }
            };
            splash.start();

        }

        else{
            showAlertDialog(splash_screen.this, "Internet Connection",
                    "You don't have internet connection", false);
        }


    }

    public boolean isWorkingInternetPresent() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Connect to Internet")
                .setCancelable(false)
                .setMessage("Check your internet connectivity").setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        if(isWorkingInternetPresent()){

                            splash=new Thread(){
                                @Override
                                public void run() {
                                    try{
                                        sleep(1000);

                                    }
                                    catch (Exception e){

                                    }finally {
                                        if (names!=null) {
                                            intent=new Intent(splash_screen.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else {
                                            intent = new Intent(splash_screen.this, Login.class);
                                            startActivity(intent);
                                        }
                                    }

                                }
                            };
                            splash.start();

                        }
                        else{
                            showAlertDialog(splash_screen.this, "Internet Connection",
                                    "You don't have internet connection", false);
                        }
                    }
                }).show();
    }


  /*  public void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

*/

}
