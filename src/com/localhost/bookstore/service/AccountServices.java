package com.localhost.bookstore.service;

import com.localhost.bookstore.dao.AccountDAO;
import com.localhost.bookstore.dao.impl.AccountDAOIml;
import com.localhost.bookstore.domain.Account;

public class AccountServices {
		
	private AccountDAO accountDao = new AccountDAOIml();
	
	public Account getAccount(int accountId){
		return accountDao.get(accountId);
	}
	
}
