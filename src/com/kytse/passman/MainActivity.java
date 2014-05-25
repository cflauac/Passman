package com.kytse.passman;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.kytse.passman.utiil.AccountTable;
import com.kytse.passman.utiil.AccountTable.Account;
import com.kytse.passman.utiil.DbxTool;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
    
    private DbxDatastore.SyncStatusListener mDatastoreListener = new DbxDatastore.SyncStatusListener() {
        @Override
        public void onDatastoreStatusChange(DbxDatastore ds) {
            Log.d(TAG, "SYNC STATUS: " + ds.getSyncStatus().toString());
            if (ds.getSyncStatus().hasIncoming) {
                try {
                    DbxTool.mDatastore.sync();
                } catch (DbxException e) {
                    handleException(e);
                }
            }
            updateList();
        }
    };
    
    private void updateList() {
    	
        List<Account> accounts;
        try {
            accounts = DbxTool.mAccountTable.getAccountsSorted();
        } catch (DbxException e) {
            handleException(e);
            return;
        }

        ListView accountsView = (ListView) findViewById(R.id.listView_account);
        accountsView.setAdapter(new AccountAdapter(accounts));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    
	@Override
    protected void onPause() {
        super.onPause();
        if (DbxTool.mDatastore != null) {
        	DbxTool.mDatastore.removeSyncStatusListener(mDatastoreListener);
        	DbxTool.mDatastore.close();
        	DbxTool.mDatastore = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    public void unlinkToDropbox (MenuItem item) {
    	DbxTool.mDbxAcctMgr.unlink();
        Toast.makeText(getApplicationContext(), "unlinked to dropbox", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
    
    public void addAccount(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AccountEditorActivity.class);
        startActivity(intent);
    }
    
    public class AccountAdapter extends BaseAdapter {
    	private List<Account> mAccounts;
    	
    	public AccountAdapter(List<Account> accounts) {
    		mAccounts = accounts;
    	}
    	
		@Override
		public int getCount() {
			// TODO Auto-generateSd method stub
			return mAccounts.size();
		}

		@Override
		public Account getItem(int arg0) {
			// TODO Auto-generated method stub
			return mAccounts.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.item_account, null);
			}

            TextView domain = (TextView)convertView.findViewById(R.id.textView_list_domain);
            domain.setText(mAccounts.get(position).getDomain());
            
            TextView username = (TextView)convertView.findViewById(R.id.textView_list_username);
            username.setText(mAccounts.get(position).getUsername());
			
			return convertView;
		}
    	
    }
    
    private void handleException(DbxException e) {
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

	@Override
	protected void onResume() {
		super.onResume();
        try {
            if (null == DbxTool.mDatastore) {
            	DbxTool.mDatastore = DbxDatastore.openDefault(DbxTool.mDbxAcctMgr.getLinkedAccount());
            }
            DbxTool.mAccountTable = new AccountTable(DbxTool.mDatastore);

            DbxTool.mDatastore.addSyncStatusListener(mDatastoreListener);
            DbxTool.mDatastore.sync();
            updateList();

        } catch (DbxException e) {
            handleException(e);
        }
	}

}
