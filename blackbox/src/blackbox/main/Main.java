package blackbox.main;

import java.math.BigDecimal;

import blackbox.bank.Bank;
import blackbox.exchange.InterdayExchange;
import blackbox.strategy.FirstStrategy;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Bank bank = new Bank();
		long start = System.currentTimeMillis();
		InterdayExchange exchange = new InterdayExchange(bank);
		long end = System.currentTimeMillis();
		bank.setMarketPriceSource(exchange);
		System.out.println("Time to load all data : "+(end-start)+" ms");
		bank.createTradingAccount("trader 1");
		bank.createTradingAccount("trader 2");
		try{
			bank.makeDeposit("trader 1", BigDecimal.valueOf(1874.72), "EUR");	
			bank.makeDeposit("trader 2", BigDecimal.valueOf(1000.00), "EUR");
			bank.BuyInstrument("trader 1", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
			bank.BuyInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
			bank.SellInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.98d), "EUR");
		}catch (Exception e) {
			e.printStackTrace();
		}
		exchange.registerInterdayStrategy(new FirstStrategy());
		bank.printBalanceSheet();
		System.out.print(bank.getAccount("trader 1"));
		System.out.print(bank.getAccount("trader 2"));
		System.out.print(bank.getAccount("FirstStrategy"));
		System.out.print(bank.getAccount(bank.EXCHANGE_ACCOUNT));
		
	}

}
