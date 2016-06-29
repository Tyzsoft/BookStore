package com.localhost.bookstore.dao;

import com.localhost.bookstore.domain.Account;

public interface AccountDAO {

	/**
	 * ���� accountId ��ȡ��Ӧ�� Account ����
	 * @param accountId
	 * @return
	 */
	public abstract Account get(Integer accountId);

	/**
	 * ���ݴ���� accountId, amount ����ָ���˻������: �۳� amount ָ����Ǯ��
	 * @param accountId
	 * @param amount
	 */
	public abstract void updateBalance(Integer accountId, float amount);

}