package blackbox.optimizer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import blackbox.bank.Bank;
import blackbox.bank.TradingAccount;
import blackbox.exchange.AInterdayStrategy;
import blackbox.exchange.InterdayExchange;
import blackbox.metric.StrategyPerformance;
import blackbox.predictor.APredictor;
import blackbox.predictor.LinearPredictor;
import blackbox.strategy.DataExtracterStrategy;

public class Optimizer {
	
	private Bank _bank;
	private InterdayExchange _exchange; 
	
	private AInterdayStrategy _strategy;
	private APredictor _predictor;
	
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Optimizer o= new Optimizer();
		o.init();
		o.run();
		((DataExtracterStrategy)o._strategy).finish();
	}
	
	private void init(){
		_bank = new Bank();
		long start = System.currentTimeMillis();
		_exchange = new InterdayExchange(_bank);
		long end = System.currentTimeMillis();
		_bank.setMarketPriceSource(_exchange);
		System.out.println("Time to load all data : "+(end-start)+" ms");
		_predictor = new LinearPredictor();
		_strategy = new DataExtracterStrategy(_exchange);
		_exchange.registerInterdayStrategy(_strategy);
	}
	
	private void run(){
		//for(;;){
			_bank.reset();
			try{
				_exchange.replay(dateFormat.parse("01-01-2006"), dateFormat.parse("31-12-2011"));
				StrategyPerformance perf = new StrategyPerformance(_bank.getAccount(_strategy.getName()).getHistory(), ((TradingAccount)_bank.getAccount(_strategy.getName())).getNbTrades());
				System.out.println(perf.SQNTradePlus);
			}catch (Exception e) {
				// TODO: handle exception
			}
		//}
	}

}
