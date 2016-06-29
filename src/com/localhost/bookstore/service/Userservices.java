package com.localhost.bookstore.service;

import java.util.Iterator;
import java.util.Set;

import javax.jws.soap.SOAPBinding.Use;

import com.localhost.bookstore.dao.BookDAO;
import com.localhost.bookstore.dao.TradeDAO;
import com.localhost.bookstore.dao.TradeItemDAO;
import com.localhost.bookstore.dao.UserDAO;
import com.localhost.bookstore.dao.impl.BookDAoImpl;
import com.localhost.bookstore.dao.impl.TradeDAOImpl;
import com.localhost.bookstore.dao.impl.TradeItemDAOImpl;
import com.localhost.bookstore.dao.impl.UserDAOImpl;
import com.localhost.bookstore.domain.Trade;
import com.localhost.bookstore.domain.TradeItem;
import com.localhost.bookstore.domain.User;

public class Userservices {

	private UserDAO userDAO = new UserDAOImpl();
	private TradeDAO tradedao = new TradeDAOImpl();
	private TradeItemDAO tradeItemdao = new TradeItemDAOImpl();
	private BookDAO bookdao = new BookDAoImpl();
	
	
	public User getUserByUserName(String username){
		return userDAO.getUser(username);
	}
	
	
	public User getUserWithTrades(String username){
		
//		调用 UserDAO 的方法获取 User 对象
		User user = userDAO.getUser(username);
		if(user == null){
			return null;
		}
		
//		调用 TradeDAO 的方法获取 Trade 的集合，把其装配为 User 的属性
		int userId = user.getUserId();
		
//		调用 TradeItemDAO 的方法获取每一个 Trade 中的 TradeItem 的集合，并把其装配为 Trade 的属性
		Set<Trade> trades = tradedao.getTradesWithUserId(userId);
		
		if(trades != null){
			Iterator<Trade> tradeIt = trades.iterator();
			
			while(tradeIt.hasNext()){
				Trade trade = tradeIt.next();
				
				int tradeId = trade.getTradeId();
				Set<TradeItem> items = tradeItemdao.getTradeItemsWithTradeId(tradeId);
				
				if(items != null){
					for(TradeItem item: items){
						item.setBook(bookdao.getBook(item.getBookId())); 
					}
					
					if(items != null && items.size() != 0){
						trade.setItems(items);						
					}
				}
				
				if(items == null || items.size() == 0){
					tradeIt.remove();	
				}
				
			}
		}
		
		if(trades != null && trades.size() != 0){
			user.setTrades(trades);			
		}
		
		return user;
	}

	
}
