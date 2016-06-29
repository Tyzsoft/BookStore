package com.localhost.bookstore.dao.impl;

import com.localhost.bookstore.dao.AccountDAO;
import com.localhost.bookstore.domain.Account;

public class AccountDAOIml extends BaseDAo<Account> implements AccountDAO {

	@Override
	public Account get(Integer accountId) {
		String sql = "SELECT accountId, balance FROM account WHERE accountId = ?";
		return query(sql, accountId); 
	}

	@Override
	public void updateBalance(Integer accountId, float amount) {
		String sql = "UPDATE account SET balance = balance - ? WHERE accountId = ?";
		update(sql, amount, accountId); 

	}

}
