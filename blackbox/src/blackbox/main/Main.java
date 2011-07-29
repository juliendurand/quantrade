package blackbox.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import blackbox.bank.Bank;
import blackbox.bank.TradingAccount;
import blackbox.data.Downloader;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.InterdayExchange;
import blackbox.metric.StrategyPerformance;
import blackbox.strategy.HAHStrategy;
import blackbox.strategy.VolAdjTopDropCloseOpenStrategy;

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
		//exchange.registerInterdayStrategy(new FirstStrategy(exchange));
		//exchange.registerInterdayStrategy(new CloseOpenStrategy(exchange));
		//exchange.registerInterdayStrategy(new OpenCloseStrategy(exchange));
	//exchange.registerInterdayStrategy(new PerfectCloseOpenStrategy(exchange));
		//exchange.registerInterdayStrategy(new NearPerfectCloseOpenStrategy(exchange));
	exchange.registerInterdayStrategy(new VolAdjTopDropCloseOpenStrategy(exchange));
		//exchange.registerInterdayStrategy(new HAHStrategy(exchange));
		//exchange.registerInterdayStrategy(new ABCDEStatCloseOpenStrategy(exchange));
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if(true){
			try{
				exchange.replay(dateFormat.parse("01-01-2000"), dateFormat.parse("31-12-2011"));
				for(AInterdayStrategy strat : exchange.getInterdayStrategy()){
					Downloader.saveToFile(bank.getAccount(strat.getName()).getBalanceHistory(),Downloader.PATH+"../"+strat.getName()+System.currentTimeMillis()+"_EquityCurve"+".csv");
					Downloader.saveToFile(((TradingAccount)bank.getAccount(strat.getName())).getTrades(),Downloader.PATH+"../"+strat.getName()+System.currentTimeMillis()+"_Trades"+".csv");
					StrategyPerformance perf = new StrategyPerformance(bank.getAccount(strat.getName()).getHistory(), ((TradingAccount)bank.getAccount(strat.getName())).getNbTrades());
					Downloader.saveToFile(perf.toString(),Downloader.PATH+"../"+strat.getName()+System.currentTimeMillis()+"_Performance"+".csv");		
					System.out.print(bank.getAccount(strat.getName()));
					System.out.println(perf);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

}
