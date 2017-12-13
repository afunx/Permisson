package com.afunx.permission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGrantedAll() {
                // TODO all permission request in AndroidManifest.xml are granted
                Toast.makeText(MainActivity.this, "All permission request in AndroidManifest.xml are granted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(final List<String> deniedPermissionList) {
                // TODO some permission requests in AndroidManifest.xml are denied
                Toast.makeText(MainActivity.this, "some permission requests in AndroidManifest.xml are denied: " + deniedPermissionList , Toast.LENGTH_LONG).show();
            }
        };
        PermissionHelper.requestPermissions(1, permissionListener, this);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
