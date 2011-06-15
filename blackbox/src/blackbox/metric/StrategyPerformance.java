package blackbox.metric;

import java.util.List;

import javax.management.StandardEmitterMBean;

import blackbox.bank.CashAccount;
import blackbox.bank.DailyBalance;

public class StrategyPerformance {
	
	public int nbDays = 0;
	public double Max = 0d;
	private double _minMax = 0d;
	public double PctMaxDrawDown = 0d;
	public double NbWinningDays = 0d;
	public double NbUnchangedDays = 0d;
	public double NbLosingDays =  0d;
	public double PctWinningDays = 0d;
	public double PctUnchangedDays = 0d;
	public double PctLosingDays =  0d;
	public double Expectancy = 0d;
	public double StdDev = 0d;
	public double UpExpectancy = 0d;
	public double UpStdDev = 0d;
	public double DownExpectancy = 0d;
	public double DownStdDev = 0d;
	public double SQN = 0d;
	public double SQNPlus = 0d;
	
	public StrategyPerformance(List<DailyBalance> history){
		calculateMetrics(history);
	}
	
	public void calculateMetrics(List<DailyBalance> history){
		double yesterdayValue = 0d;
		double todayValue = 0d;
		boolean start = true;
		for(DailyBalance day : history){
			if(start){
				start=false;
				yesterdayValue = day.value.doubleValue();
				Max = yesterdayValue;
				_minMax = yesterdayValue;
				continue;
			}
			todayValue = day.value.doubleValue();
			//
			nbDays++;
			double dailyReturn = todayValue/yesterdayValue-1;
			if(dailyReturn>0d){
				NbWinningDays++;
				UpExpectancy+=dailyReturn;
				UpStdDev+=(dailyReturn+1)*(dailyReturn+1);
			}else{
				if(dailyReturn<0d){
					NbLosingDays++;	
					DownExpectancy+=dailyReturn;
					DownStdDev+=(dailyReturn+1)*(dailyReturn+1);
				}
				else{
					NbUnchangedDays++;
				}	
			}
			if(todayValue>=Max){
				double drawdown = (Max-_minMax)/Max*100;
				if(drawdown>PctMaxDrawDown){
					PctMaxDrawDown = drawdown;
					System.out.println("new max drawdown on "+CashAccount.dateFormatter.format(day.date)+" "+PctMaxDrawDown +" %");
				}
				Max = todayValue;			
				_minMax = Max;
			}
			if(todayValue<_minMax){
				_minMax = todayValue;
			}
			Expectancy+=dailyReturn;
			StdDev+=(dailyReturn+1)*(dailyReturn+1);
			//
			yesterdayValue = todayValue;
		}
		PctWinningDays = NbWinningDays/nbDays;
		PctUnchangedDays = NbUnchangedDays/nbDays;
		PctLosingDays = NbLosingDays/nbDays;
		Expectancy/=nbDays;
		StdDev = Math.sqrt(StdDev/nbDays-(Expectancy+1)*(Expectancy+1));
		UpExpectancy/=NbWinningDays;
		UpStdDev = Math.sqrt(UpStdDev/NbWinningDays-(UpExpectancy+1)*(UpExpectancy+1));
		DownExpectancy/=NbLosingDays;
		DownStdDev = Math.sqrt(DownStdDev/NbLosingDays-(DownExpectancy+1)*(DownExpectancy+1));
		SQN = Math.sqrt(nbDays)*Expectancy/StdDev;
		SQNPlus = Math.sqrt(nbDays)*Expectancy/DownStdDev;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------------------------\n");
		buffer.append("PERFORMANCES\n");
		buffer.append("------------------------------------------\n");
		buffer.append("Nb trading days: ");
		buffer.append(nbDays);
		buffer.append("\n");
		buffer.append("% winning days: ");
		buffer.append(PctWinningDays*100);
		buffer.append(" %\n");
		buffer.append("% unchanged days: ");
		buffer.append(PctUnchangedDays*100);
		buffer.append(" %\n");
		buffer.append("% losing days: ");
		buffer.append(PctLosingDays*100);
		buffer.append(" %\n");
		buffer.append("Daily expectancy: ");
		buffer.append(Expectancy*100);
		buffer.append(" %\n");
		buffer.append("Daily standard deviation: ");
		buffer.append(StdDev*100);
		buffer.append(" %\n");
		buffer.append("Daily up expectancy: ");
		buffer.append(UpExpectancy*100);
		buffer.append(" %\n");
		buffer.append("Daily up standard deviation: ");
		buffer.append(UpStdDev*100);
		buffer.append(" %\n");
		buffer.append("Daily down expectancy: ");
		buffer.append(DownExpectancy*100);
		buffer.append(" %\n");
		buffer.append("Daily down standard deviation: ");
		buffer.append(DownStdDev*100);
		buffer.append(" %\n");
		buffer.append("% maximum drawdown: ");
		buffer.append(PctMaxDrawDown);
		buffer.append(" %\n");
		buffer.append("Strategy quality number: ");
		buffer.append(SQN);
		buffer.append("\n");
		buffer.append("Enhanced trategy quality number: ");
		buffer.append(SQNPlus);
		buffer.append("\n");
		return buffer.toString();
	}

}
