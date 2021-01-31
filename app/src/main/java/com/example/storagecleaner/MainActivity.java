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

    LocationManager locationManager;
    LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor temp;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
            }
        }
    }

    private void handleLocationGranted() {
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
                DocumentFile video1 = files[0];
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
 }, VideoQuality.VERY_LOW, false, false);



                // Perform operations on the document using its URI.
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          // Uri foo = Uri.parse("package:"+BuildConfig.APPLICATION_ID);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

    // Provide read access to files and sub-directories in the user-selected
    // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    // Optionally, specify a URI for the directory that should be opened in
    // the system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, 1);
      //  }
    }

   /* @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("x" + event.values[0]);
        System.out.println("y" + event.values[1]);
        System.out.println("z" + event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/

    public static String getPath(final Context context, final Uri uri) {


    // DocumentProvider
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            return Environment.getExternalStorageDirectory() + "/" + split[1];

            // TODO handle non-primary volumes
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {
            try {
                final String id = DocumentsContract.getDocumentId(uri);
                //Log.d(TAG, "getPath: id= " + id);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }catch (Exception e){
                e.printStackTrace();
                List<String> segments = uri.getPathSegments();
                if(segments.size() > 1) {
                    String rawPath = segments.get(1);
                    if(!rawPath.startsWith("/")){
                        return rawPath.substring(rawPath.indexOf("/"));
                    }else {
                        return rawPath;
                    }
                }
            }
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {

        // Return the remote address
        if (isGooglePhotosUri(uri))
            return uri.getLastPathSegment();

        return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }

    return null;
}
public static String getDataColumn(Context context, Uri uri, String selection,
                                   String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {
            column
    };

    try {
        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null);
        if (cursor != null && cursor.moveToFirst()) {


            final int column_index = cursor.getColumnIndexOrThrow(column);
            return cursor.getString(column_index);
        }
    }catch (Exception e){
        e.printStackTrace();
    }finally {
        if (cursor != null)
            cursor.close();
    }
    return "nodata";
}

public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
}

public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
}

public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
}

public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
}
}

