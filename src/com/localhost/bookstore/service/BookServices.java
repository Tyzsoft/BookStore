package com.localhost.bookstore.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import com.localhost.bookstore.dao.AccountDAO;
import com.localhost.bookstore.dao.BookDAO;
import com.localhost.bookstore.dao.TradeDAO;
import com.localhost.bookstore.dao.TradeItemDAO;
import com.localhost.bookstore.dao.UserDAO;
import com.localhost.bookstore.dao.impl.AccountDAOIml;
import com.localhost.bookstore.dao.impl.BookDAoImpl;
import com.localhost.bookstore.dao.impl.TradeDAOImpl;
import com.localhost.bookstore.dao.impl.TradeItemDAOImpl;
import com.localhost.bookstore.dao.impl.UserDAOImpl;
import com.localhost.bookstore.domain.Book;
import com.localhost.bookstore.domain.ShoppingCart;
import com.localhost.bookstore.domain.ShoppingCartItem;
import com.localhost.bookstore.domain.Trade;
import com.localhost.bookstore.domain.TradeItem;
import com.localhost.bookstore.web.CriteriaBook;
import com.localhost.bookstore.web.Page;

public class BookServices {
	
	private BookDAO bookdao = new BookDAoImpl();
	
	public Page<Book> getPage(CriteriaBook cb){
		return bookdao.getPage(cb);
	}
	
	public Book getBook(int id){
		return bookdao.getBook(id);
	}
	
	public boolean addToCart(int id,ShoppingCart sc){
		Book book =bookdao.getBook(id);
		
		if(book != null){
			sc.addBook(book);
			return true;
		}
		return false;
	}
	
	public void removeItemFromShoppingCart(ShoppingCart sc, int id) {
		sc.removeItem(id);
	}
	
	public void clear(ShoppingCart sc) {
		sc.clear();
	}
	
	public void updateItemQuantity(ShoppingCart sc, int id, int quantity) {
		sc.updateItemQuantity(id, quantity);
	}
	
	private AccountDAO accountDAO = new AccountDAOIml();
	private TradeDAO tradeDAO = new TradeDAOImpl();
	private UserDAO userDAO = new UserDAOImpl();
	private TradeItemDAO tradeItemDAO = new TradeItemDAOImpl();

	//业务方法. 
	public void cash(ShoppingCart shoppingCart, String username,
			String accountId) {
		
		//1. 更新 mybooks 数据表相关记录的 salesamount 和 storenumber
		bookdao.batchUpdateStoreNumberAndSalesAmount(shoppingCart.getItems());
		

		
		//2. 更新 account 数据表的 balance
		accountDAO.updateBalance(Integer.parseInt(accountId), shoppingCart.getTotalMoney());
		
		//3. 向 trade 数据表插入一条记录
		Trade trade = new Trade();
		trade.setTradeTime(new Date(new java.util.Date().getTime()));
		trade.setUserId(userDAO.getUser(username).getUserId());
		tradeDAO.insert(trade);
		
		//4. 向 tradeitem 数据表插入 n 条记录
		Collection<TradeItem> items = new ArrayList<>();
		for(ShoppingCartItem sci: shoppingCart.getItems()){
			TradeItem tradeItem = new TradeItem();
			
			tradeItem.setBookId(sci.getBook().getId());
			tradeItem.setQuantity(sci.getQuantity());
			tradeItem.setTradeId(trade.getTradeId());
			
			items.add(tradeItem);
		}
		tradeItemDAO.batchSave(items);
		
		//5. 清空购物车
		shoppingCart.clear();
	}
	
}
