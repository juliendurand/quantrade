package blackbox.bank;

import java.math.BigDecimal;
import java.util.Date;

public class DailyBalance {
	public Date date;
	public BigDecimal value;
	
	public DailyBalance(Date date2, BigDecimal balance) {
		date = date2;
		value = balance;
	}
}
