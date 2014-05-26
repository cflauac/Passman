package com.kytse.passman;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.kytse.passman.utiil.DbxTool;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		DbxTool.mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), DbxTool.APP_KEY, DbxTool.APP_SECRET);
		
		if (DbxTool.mDbxAcctMgr.hasLinkedAccount()) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			finish();
			startActivity(intent);
		}
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
	}
	
	public void linkToDropbox(View view) {
    	DbxTool.mDbxAcctMgr.startLink((Activity)LoginActivity.this, DbxTool.REQUEST_LINK_TO_DBX);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DbxTool.REQUEST_LINK_TO_DBX) {
            if (resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, data);
    			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    			finish();
    			startActivity(intent);
            } else {
                // ... Link failed or was cancelled by the user.
                Toast.makeText(this, "Link to Dropbox failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
