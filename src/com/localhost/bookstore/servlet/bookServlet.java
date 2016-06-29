package com.localhost.bookstore.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.localhost.bookstore.domain.Account;
import com.localhost.bookstore.domain.Book;
import com.localhost.bookstore.domain.ShoppingCart;
import com.localhost.bookstore.domain.ShoppingCartItem;
import com.localhost.bookstore.domain.User;
import com.localhost.bookstore.service.AccountServices;
import com.localhost.bookstore.service.BookServices;
import com.localhost.bookstore.service.Userservices;
import com.localhost.bookstore.web.BookStoreWebUtils;
import com.localhost.bookstore.web.CriteriaBook;
import com.localhost.bookstore.web.Page;


/**
 * Servlet implementation class bookServlet
 */
public class bookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public bookServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private BookServices bookServices = new BookServices();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	private  Userservices  userServices = new Userservices();
	private AccountServices accountservices = new AccountServices();
	protected void cash(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String accountId = request.getParameter("accountId");
		
		StringBuffer errors = validateFormField(username,accountId);
		if(errors.toString().equals("")){
			errors = validateUser(username, accountId);
			if(errors.toString().equals("")){
				errors = validateBookStoreNumber(request);
				if(errors.toString().equals("")){
					errors = validateBalance(request, accountId);
				}
			}
		}
		if(!errors.toString().equals("")){
			request.setAttribute("errors", errors);
			request.getRequestDispatcher("/WEB-INF/pages/cash.jsp").forward(request, response);
			return ;
		}
		
		bookServices.cash(BookStoreWebUtils.getShoppingCart(request), username, accountId);
		response.sendRedirect(request.getContextPath() + "/success.jsp");
	}
	

	//验证余额是否充足
	public StringBuffer validateBalance(HttpServletRequest request, String accountId){
		
		StringBuffer errors = new StringBuffer("");
		ShoppingCart cart = BookStoreWebUtils.getShoppingCart(request);
		
		Account account = accountservices.getAccount(Integer.parseInt(accountId));
		if(cart.getTotalMoney() > account.getBalance()){
			errors.append("余额不足!");
		}
		
		return errors;
	}
	
	//验证库存是否充足
		public StringBuffer validateBookStoreNumber(HttpServletRequest request){
			
			StringBuffer errors = new StringBuffer("");
			ShoppingCart cart = BookStoreWebUtils.getShoppingCart(request);
			
			for(ShoppingCartItem sci: cart.getItems()){
				int quantity = sci.getQuantity();
				int storeNumber = bookServices.getBook(sci.getBook().getId()).getStoreNumber();
				
				if(quantity > storeNumber){
					errors.append(sci.getBook().getTitle() + "库存不足<br>");
				}
			}
			
			return errors;
		}
	
	//验证用户名和账号是否匹配
		public StringBuffer validateUser(String username, String accountId){
			boolean flag = false;
			User user = userServices.getUserByUserName(username);
			if(user != null){
				int accountId2 = user.getAccountId();
				if(accountId.trim().equals("" + accountId2)){
					flag = true;
				}
			}
			
			StringBuffer errors2 = new StringBuffer("");
			if(!flag){
				errors2.append("用户名和账号不匹配");
			}
			
			return errors2;
		}
	
	//验证表单域是否符合基本的规则: 是否为空. 
		public StringBuffer validateFormField(String username, String accountId){
			StringBuffer errors = new StringBuffer("");
			
			if(username == null || username.trim().equals("")){
				errors.append("用户名不能为空<br>");
			}
			
			if(accountId == null || accountId.trim().equals("")){
				errors.append("账号不能为空");			
			}
			
			return errors;
		}
	protected void updateItemQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//4. 在 updateItemQuantity 方法中, 获取 quanity, id, 再获取购物车对象, 调用 service 的方法做修改
				String idStr = request.getParameter("id");
				String quantityStr = request.getParameter("quantity");
				
				ShoppingCart sc = BookStoreWebUtils.getShoppingCart(request);
				
				int id = -1;
				int quantity = -1;
				
				try {
					id = Integer.parseInt(idStr);
					quantity = Integer.parseInt(quantityStr);
				} catch (Exception e) {}
				
				if(id > 0 && quantity > 0)
					bookServices.updateItemQuantity(sc, id, quantity);
				
				//5. 传回 JSON 数据: bookNumber:xx, totalMoney
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("bookNumber", sc.getBookNumber());
				result.put("totalMoney", sc.getTotalMoney());
				
				Gson gson = new Gson();
				String jsonStr = gson.toJson(result);
				response.setContentType("text/javascript");
				response.getWriter().print(jsonStr);
	}
	
	protected void clear(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShoppingCart sc =BookStoreWebUtils.getShoppingCart(request); 
		bookServices.clear(sc);
		request.getRequestDispatcher("/WEB-INF/pages/emptycart.jsp").forward(request, response);
	}
	
	protected void remove(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idStr = request.getParameter("id");
		
		int id = -1;
		try {
			id = Integer.parseInt(idStr);
		} catch (Exception e) {}
		
		ShoppingCart sc = BookStoreWebUtils.getShoppingCart(request);
		bookServices.removeItemFromShoppingCart(sc, id);
		
		if(sc.isEmpty()){
			request.getRequestDispatcher("/WEB-INF/pages/emptycart.jsp").forward(request, response);
			return;
		}
		
		request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
	}
	
	protected void forwardPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String page = request.getParameter("page");
		request.getRequestDispatcher("/WEB-INF/pages/" + page + ".jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = request.getParameter("method");
		
		try {
			Method method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			method.setAccessible(true);
			method.invoke(this, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	protected void addToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1. 获取商品的 id
		String idStr = request.getParameter("id");
		int id = -1;
		boolean flag = false;
		
		try {
			id = Integer.parseInt(idStr);
		} catch (Exception e) {}
		
		if(id > 0){
			//2. 获取购物车对象
			ShoppingCart sc = BookStoreWebUtils.getShoppingCart(request);
			
			//3. 调用 BookService 的 addToCart() 方法把商品放到购物车中
			flag = bookServices.addToCart(id, sc);
		}
		
		if(flag){
			//4. 直接调用 getBooks() 方法. 
			getBooks(request, response);
			return;
		}
		
		response.sendRedirect(request.getContextPath() + "/error-1.jsp");
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	
	protected void getBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String idStr = request.getParameter("id");
			int id = -1;
			Book book=null;
			try {
				id = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
			if(id > 0)
				book = bookServices.getBook(id);
			if(book == null){
				response.sendRedirect(request.getContextPath() +"/error-1.jsp");
			}
			request.setAttribute("book", book);
			request.getRequestDispatcher("/WEB-INF/pages/book.jsp").forward(request, response);
			
	}
	
	protected void getBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pageNoStr = request.getParameter("pageNo");
		String minPriceStr = request.getParameter("minPrice");
		String maxPriceStr = request.getParameter("maxPrice");
		
		int pageNo=1;
		int minPrice= 0;
		int maxPrice= Integer.MAX_VALUE;
		
		try {
			pageNo = Integer.parseInt(pageNoStr);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		try {
			minPrice = Integer.parseInt(minPriceStr);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		try {
			maxPrice = Integer.parseInt(maxPriceStr);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		
		CriteriaBook criteriaBook = new CriteriaBook(minPrice, maxPrice, pageNo);
		Page<Book> page = bookServices.getPage(criteriaBook);
		
		request.setAttribute("bookpage", page);
		request.getRequestDispatcher("/WEB-INF/pages/books.jsp").forward(request, response);
	}

	
}