package com.kytse.passman.utiil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class AccountTable {
	
	private DbxDatastore mDatastore;
	private DbxTable mTable;
	
	public class Account {
		private DbxRecord mRecord;
		
		public Account(DbxRecord record) {
			mRecord = record;
		}
		
		public String getId() {
			return mRecord.getId();
		}

		public String getDomain() {
			return mRecord.getString("domain");
		}
		
		public String getUsername() {
			return mRecord.getString("username");
		}
		
		public String getPassword() {
			DbxList mList = mRecord.getList("password");
			return mList.getString(mList.size()-1);
		}
		
		public String getPassword(int index) {
			DbxList mList = mRecord.getList("password");
			return mList.getString(index);
		}

		public void setPassword(String password) throws DbxException {
			DbxList mPasswordList = mRecord.getList("password");
			mPasswordList.add(password);
			DbxList mModifiedList = mRecord.getList("modified");
			mModifiedList.add(new Date());
			mDatastore.sync();
		}
		
		public String getRemarks() {
			return mRecord.getString("remarks");
		}
		
		public void setRemarks(String remarks) throws DbxException {
			mRecord.set("remarks", remarks);
			mDatastore.sync();
		}
		
		public Date getModified() {
			DbxList mList = mRecord.getList("modified");
			return mList.getDate(mList.size()-1);
		}
		
		public Date getModified(int index) {
			DbxList mList = mRecord.getList("modified");
			return mList.getDate(index);
		}
		
		public Date getVisited() {
			return mRecord.getDate("visited");
		}
		
		public void setVisited() throws DbxException {
			mRecord.set("visited", new Date());
			mDatastore.sync();
		}
		
		public void delete() throws DbxException {
			mRecord.deleteRecord();
			mDatastore.sync();
		}
	}
	
	public AccountTable(DbxDatastore datastore) {
		mDatastore = datastore;
		mTable = datastore.getTable("accounts");
	}
	
	public void createAccount(String domain, String username, String password, String remarks)  throws DbxException {
		DbxFields accountFields = new DbxFields()
									.set("domain", domain)
									.set("username", username)
									.set("password", new DbxList().add(password))
									.set("remarks", remarks)
									.set("modified", new DbxList().add(new Date()))
									.set("visited", new Date());
		mTable.insert(accountFields);
		mDatastore.sync();
	}
	
	public List<Account> getAccountsSorted() throws DbxException {
		List<Account> resultList = new ArrayList<Account>();
		for (DbxRecord result : mTable.query()) {
			resultList.add(new Account(result));
		}
		Collections.sort(resultList, new Comparator<Account>() {

			@Override
			public int compare(Account arg0, Account arg1) {
				if (arg0.getDomain().equals(arg1.getDomain()))
					return arg0.getUsername().compareTo(arg1.getUsername());
				else
					return arg0.getDomain().compareTo(arg1.getDomain());
			}
			
		});
		return resultList;
	}
	
	
}
