package com.mybank;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * {@link BankAccountTest} tests the {@link Account} interface.
 * 
 * @see Account
 */
public class BankAccountTest {

	/** The Constant TEST_CPR. */
	private static final int TEST_CPR = 1234567890;

	/** The local test. */
	private static final boolean localTest = true;

	/** The client. */
	private static Account client;

	/**
	 * Sets up before class.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (localTest) {
			client = new BankAccount(TEST_CPR);
		} else {
			client = new BankAccountHTTPProxy("http://localhost:8081");
		}
	}

	/**
	 * Test balance zero.
	 * 
	 * @throws AccountException
	 *             if the balance cannot be obtained
	 */
	@Test
	public void testBalanceZero() throws AccountException {
		int balance = client.getBalance();
		assertEquals(0, balance);
	}

	/**
	 * Test correct balance.
	 */
	@Test
	public void testCorrectBalance() throws AccountException {
		client.deposit(100);
		int balance = client.getBalance();
		// Tests balance after series of deposit-withdraw invocations
		assertEquals(100,balance);
	}

	/**
	 * Test over draft.
	 */
	@Test(expected = com.mybank.AccountException.class)
	public void testOverDraft() throws AccountException {
		client.withdraw(100);
		// BankAccountException should be thrown if we try to withdraw amount
		// grater than balance

	}

	/**
	 * Test negative withdraw.
	 */
	@Test(expected = com.mybank.AccountException.class)
	public void testNegativeWithdraw() throws AccountException {

		// BankAccountException should be thrown if we try to withdraw a
		// negative amount.
		client.withdraw(-100);
	}

	/**
	 * Test negative deposit.
	 */
	@Test(expected = com.mybank.AccountException.class)
	public void testNegativeDeposit() throws AccountException {

		// BankAccountException should be thrown if we try to deposit a negative
		// amount.
		client.deposit(-100);
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (!localTest) {
			((BankAccountHTTPProxy) client).stop();
		}
	}
}
