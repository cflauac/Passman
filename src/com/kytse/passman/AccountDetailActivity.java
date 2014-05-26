package com.kytse.passman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.kytse.passman.utiil.AccountTable.Account;
import com.kytse.passman.utiil.AccountTable;
import com.kytse.passman.utiil.DbxTool;

public class AccountDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
        	Intent intent = new Intent(AccountDetailActivity.this, SettingsActivity.class);
        	startActivity(intent);
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
			View rootView = inflater.inflate(R.layout.fragment_account_detail,
					container, false);
			return rootView;
		}
	}
	
	public void deleteAccount(MenuItem menuitem) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_dialog_message)
		       .setTitle(R.string.delete_dialog_title);
		builder.setPositiveButton(R.string.delete_dialog_yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = getIntent();
				String id = intent.getStringExtra("id");
				try { 
					Account account = DbxTool.mAccountTable.getAccount(id);
					account.delete();
					Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_LONG).show();
					finish();
				} catch (DbxException e) {
			        handleException(e);
			        return;
			    }						
			}
		    	   
		});
		builder.setNegativeButton(R.string.delete_dialog_no, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
			}
		    	   
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();

	}
	
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
        if (DbxTool.mDatastore != null) {
        	DbxTool.mDatastore.close();
        	DbxTool.mDatastore = null;
        }
	}
    
	@Override
	protected void onResume() {
		super.onResume();

        if (null == DbxTool.mDatastore) {
        	try {
				DbxTool.mDatastore = DbxDatastore.openDefault(DbxTool.mDbxAcctMgr.getLinkedAccount());
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        DbxTool.mAccountTable = new AccountTable(DbxTool.mDatastore);
        
	}
	
    private void handleException(DbxException e) {
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

	@Override
	protected void onPostResume() {
		super.onPostResume();
		
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		try { 
			Account account = DbxTool.mAccountTable.getAccount(id);
			
			TextView domain = (TextView)findViewById(R.id.textView_detail_domain);
			TextView username = (TextView)findViewById(R.id.textView_detail_username);
			TextView password = (TextView)findViewById(R.id.textView_detail_password);
			TextView remarks = (TextView)findViewById(R.id.textView_detail_remarks);
					
			domain.setText(account.getDomain());
			username.setText(account.getUsername());
			password.setText(account.getPassword());
			remarks.setText(account.getRemarks());
		} catch (DbxException e) {
	        handleException(e);
	        return;
	    }
	}

}
