package com.localhost.bookstore.dao.impl;

import com.localhost.bookstore.dao.UserDAO;
import com.localhost.bookstore.domain.User;

public class UserDAOImpl extends BaseDAo<User> implements UserDAO {

	@Override
	public User getUser(String username) {
		String sql = "SELECT userId, username, accountId " +
				"FROM userinfo WHERE username = ?";
		return query(sql, username); 
	}

}
