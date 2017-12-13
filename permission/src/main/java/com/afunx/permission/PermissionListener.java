package com.afunx.permission;

import java.util.List;

/**
 * Permission Listener
 * <p>
 * Created by afunx on 2017/12/13.
 */

public interface PermissionListener {
    /**
     * all permission requested are granted
     */
    void onPermissionGrantedAll();

    /**
     * some permission requested are denied
     *
     * @param deniedPermissionList denied permission list
     */
    void onPermissionDenied(List<String> deniedPermissionList);
}
