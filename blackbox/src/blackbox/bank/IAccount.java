package blackbox.bank;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IAccount {
	public String getId();
	public EAccountType getType();
	public BigDecimal getBalance(String currency);
	public String getCurrency();
	public void debit(AccountingEntry entry) throws Exception;
	public void credit(AccountingEntry entry) throws Exception;
	public void closingRun(Date date);
	public String getBalanceHistory();
	public List<DailyBalance> getHistory();
	
}
