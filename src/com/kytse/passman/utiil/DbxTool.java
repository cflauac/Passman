package com.kytse.passman.utiil;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;

public class DbxTool {
    public static final String APP_KEY = "9ks76w77110alwc";
    public static final String APP_SECRET = "m2yahak32rs1dlz";
    public static final String TAG = "MainActivity";

    public static final int REQUEST_LINK_TO_DBX = 0;
    
    public static DbxAccountManager mDbxAcctMgr;
    public static DbxDatastore mDatastore;
    public static AccountTable mAccountTable;
    
}
