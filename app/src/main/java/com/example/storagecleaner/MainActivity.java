package com.example.storagecleaner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;

import java.util.List;

public class MainActivity extends AppCompatActivity{


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
            }
        }
    }


    @Override
public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                DocumentFile foo = DocumentFile.fromTreeUri(this, uri);
                DocumentFile[] files = foo.listFiles();
                int index = 0;
                while (files[index].getName().indexOf(".mp4") == -1) {
                    index++;
                }
                DocumentFile video1 = files[index];
                Uri chosenUri = video1.getUri();
                String fullFilePath = UriUtils.getPathFromUri(this,chosenUri);
                String newFilePath = fullFilePath.replaceAll(".mp4","foo.mp4");
                System.out.println(fullFilePath);




                 VideoCompressor.start(fullFilePath, newFilePath, new CompressionListener() {
       @Override
       public void onStart() {
         // Compression start
       }

       @Override
       public void onSuccess() {
           System.out.println("success!");
         // On Compression success
       }

       @Override
       public void onFailure(String failureMessage) {
           System.out.println(failureMessage);
         // On Failure
       }

       @Override
       public void onProgress(final float v) {
         // Update UI with progress value
         runOnUiThread(new Runnable() {
            public void run() {
                System.out.println(v);
              //  progress.setText(progressPercent + "%");
              //  progressBar.setProgress((int) progressPercent);
           }
         });
       }

       @Override
       public void onCancelled() {
         // On Cancelled
       }
 }, VideoQuality.MEDIUM, false, false);



                // Perform operations on the document using its URI.
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);
      //  }
    }


}

