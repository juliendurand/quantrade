package blackbox.main;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import blackbox.bank.Bank;
import blackbox.data.Downloader;
import blackbox.exchange.InterdayExchange;
import blackbox.metric.StrategyPerformance;
import blackbox.strategy.CloseOpenStrategy;
import blackbox.strategy.FirstStrategy;
import blackbox.strategy.OpenCloseStrategy;

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
//		bank.createTradingAccount("trader 1");
//		bank.createTradingAccount("trader 2");
//		try{
//			bank.makeDeposit("trader 1", BigDecimal.valueOf(1874.72), "EUR");	
//			bank.makeDeposit("trader 2", BigDecimal.valueOf(1000.00), "EUR");
//			bank.BuyInstrument("trader 1", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
//			bank.BuyInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
//			bank.SellInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.98d), "EUR");
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		exchange.registerInterdayStrategy(new FirstStrategy(exchange));
		exchange.registerInterdayStrategy(new CloseOpenStrategy(exchange));
		exchange.registerInterdayStrategy(new OpenCloseStrategy(exchange));
		//bank.printBalanceSheet();
		//System.out.print(bank.getAccount("trader 1"));
		//System.out.print(bank.getAccount("trader 2"));
		//System.out.print(bank.getAccount("FirstStrategy"));
		//System.out.print(bank.getAccount(bank.EXCHANGE_ACCOUNT));
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		try{
			exchange.replay(dateFormat.parse("01-01-2000"), dateFormat.parse("14-05-2011"));
			System.out.print(bank.getAccount("FirstStrategy"));
			Downloader.saveToFile(bank.getAccount("FirstStrategy").getBalanceHistory(),Downloader.PATH+"../FirstStrategy"+System.currentTimeMillis()+".csv");
			Downloader.saveToFile(bank.getAccount("CloseOpenStrategy").getBalanceHistory(),Downloader.PATH+"../CloseOpenStrategy"+System.currentTimeMillis()+".csv");
			Downloader.saveToFile(bank.getAccount("OpenCloseStrategy").getBalanceHistory(),Downloader.PATH+"../OpenCloseStrategy"+System.currentTimeMillis()+".csv");
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
		StrategyPerformance perf = new StrategyPerformance(bank.getAccount("FirstStrategy").getHistory());
		System.out.println(perf);
		StrategyPerformance perf2 = new StrategyPerformance(bank.getAccount("CloseOpenStrategy").getHistory());
		System.out.println(perf2);
		StrategyPerformance perf3 = new StrategyPerformance(bank.getAccount("OpenCloseStrategy").getHistory());
		System.out.println(perf3);
	}

}
