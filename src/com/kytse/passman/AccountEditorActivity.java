package com.kytse.passman;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.kytse.passman.utiil.AccountTable;
import com.kytse.passman.utiil.DbxTool;

public class AccountEditorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_editor);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
	        LayoutInflater inflater = (LayoutInflater) getSystemService
	                (Context.LAYOUT_INFLATER_SERVICE);
	        View customActionBarView = inflater.inflate(R.layout.editor_custom_action_bar, null);
	        // Show the custom action bar but hide the home icon and title
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
	                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME |
	                ActionBar.DISPLAY_SHOW_TITLE);
	        actionBar.setCustomView(customActionBarView);
        }
	}
	
	public void addAccount(View view) {
        EditText domainInput = (EditText) findViewById(R.id.editText_domain);
        EditText usernameInput = (EditText) findViewById(R.id.editText_username);
        EditText passwordInput = (EditText) findViewById(R.id.editText_password);
        EditText remarksInput = (EditText) findViewById(R.id.editText_remarks);
        String username = usernameInput.getText().toString();
        String domain = domainInput.getText().toString();
        String password = passwordInput.getText().toString();
        String remarks = remarksInput.getText().toString();
        
        try {
            if (domain.length() == 0)
            	Toast.makeText(getApplicationContext(), "Domain cannot be empty", Toast.LENGTH_LONG).show();
            else if (username.length() == 0)
            	Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_LONG).show();
            else if (password.length() == 0)
            	Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_LONG).show();
            else {
            	DbxTool.mAccountTable.createAccount(domain, username, password, remarks);
                finish();
            }
        } catch (DbxException e) {
            handleException(e);
        }
        
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
		// TODO Auto-generated method stub
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_editor, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_account_editor,
					container, false);
			return rootView;
		}
	}
	
	private void handleException(DbxException e) {
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
