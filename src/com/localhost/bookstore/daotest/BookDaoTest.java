package com.localhost.bookstore.daotest;

import static org.junit.Assert.*;

import org.junit.Test;

import com.localhost.bookstore.dao.impl.BookDAoImpl;
import com.localhost.bookstore.domain.Book;
import com.localhost.bookstore.web.CriteriaBook;
import com.localhost.bookstore.web.Page;

public class BookDaoTest {

	private BookDAoImpl bookDAoImpl = new BookDAoImpl();
	
	@Test
	public void testGetBook() {
		Book book =bookDAoImpl.getBook(3);
		System.out.println(book);
	}

	@Test
	public void testGetPage() {
		CriteriaBook  cb = new CriteriaBook(50, 60, 90);
		Page<Book> page = bookDAoImpl.getPage(cb);
		
		System.out.println("pageNo: " + page.getPageNo());
		System.out.println("totalPageNumber: " + page.getTotalPageNumber());
		System.out.println("list: " + page.getList());
		System.out.println("prevPage: " + page.getPrevPage());
		System.out.println("nextPage: " + page.getNextPage()); 
	}

	@Test
	public void testGetTotalBookNumber() {
		int storeNumber = bookDAoImpl.getStoreNumber(5);
		System.out.println(storeNumber); 
	}

	@Test
	public void testGetPageList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStoreNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchUpdateStoreNumberAndSalesAmount() {
		fail("Not yet implemented");
	}

}
