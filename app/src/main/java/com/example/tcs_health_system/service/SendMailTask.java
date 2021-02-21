package com.example.tcs_health_system.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Context sendMailActivity;
    private Service sendMailService;

    public SendMailTask(Activity activity) {
        sendMailActivity = activity;
    }

    protected void onPreExecute() {
//        statusDialog = new ProgressDialog(sendMailActivity);
//        statusDialog.setMessage("Getting ready...");
//        statusDialog.setIndeterminate(false);
//        statusDialog.setCancelable(false);
//        statusDialog.show();
//        //Toast.makeText(SendMailTask.this, "fdgdfdfg", Toast.LENGTH_SHORT).show();
        Log.v("yhnju","mail sent");
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMail androidEmail = new GMail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
            publishProgress("Sending email....");
            androidEmail.sendEmail();
            publishProgress("Email Sent.");
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
//        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {

        //statusDialog.dismiss();
    }

}
