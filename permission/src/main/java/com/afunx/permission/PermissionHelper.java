package com.afunx.permission;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission Helper for apply all permission in AndroidManifest.xml
 * <p>
 * Created by afunx on 2017/12/13.
 */

public class PermissionHelper {

    private static SparseArray<PermissionListener> sPermissionListener = new SparseArray<>();

    /**
     * request permissions in AndroidManifest.xml
     * <p>A typical usage of it:</p>
     * <p>
     * <pre>
     *     public class MainActivity extends AppCompatActivity {
     *         @Override
     *         protected void onCreate(Bundle savedInstanceState) {
     *             super.onCreate(savedInstanceState);
     *             setContentView(R.layout.activity_main);
     *
     *             PermissionListener permissionListener = new PermissionListener() {
     *                 @Override
     *                 public void onPermissionGrantedAll() {
     *                     // TODO all permission request in AndroidManifest.xml are granted
     *                 }
     *
     *                 @Override
     *                 public void onPermissionDenied(final List<String> deniedPermissionList) {
     *                     // TODO some permission requests in AndroidManifest.xml are denied
     *                 }
     *             };
     *             PermissionHelper.requestPermissions(1, permissionListener, this);
     *         }
     *     }
     * </pre>
     *
     * @param requestCode        request code
     * @param permissionListener permission listener
     * @param activity           activity
     */
    public static void requestPermissions(final int requestCode, @NonNull PermissionListener permissionListener, @NonNull Activity activity) {
        // get packageName
        String packageName = activity.getPackageName();
        // get PackageInfo for permissions in AndroidManifest.xml
        PackageInfo packageInfo = null;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // permission require applying
        List<String> applyPerms = new ArrayList<>();

        if (packageInfo != null && packageInfo.requestedPermissions != null) {
            String[] permissions = packageInfo.requestedPermissions;
            for (final String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    applyPerms.add(permission);
                }
            }
        }

        if (!applyPerms.isEmpty()) {
            // apply permission if necessary
            String[] permissions = new String[applyPerms.size()];
            applyPerms.toArray(permissions);
            sPermissionListener.put(requestCode, permissionListener);
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            // all permission in AndroidManifest.xml are permitted already
            permissionListener.onPermissionGrantedAll();
        }
    }

    /**
     * call the method when request permission result returned
     * <p>
     * <p>A typical usage of it:</p>
     * <p>
     * <pre>
     *     public class MainActivity extends AppCompatActivity {
     *         @Override
     *         public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
     *              PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *              super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *         }
     *     }
     * </pre>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(int, PermissionListener, Activity)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     */
    public static void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        final PermissionListener permissionListener = sPermissionListener.get(requestCode);
        if (permissionListener != null) {
            sPermissionListener.remove(requestCode);
            // get denied permission list
            List<String> deniedPermissionList = new ArrayList<>();
            // check permission granted
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }
            if (deniedPermissionList.isEmpty()) {
                permissionListener.onPermissionGrantedAll();
            } else {
                permissionListener.onPermissionDenied(deniedPermissionList);
            }
        }
    }

}