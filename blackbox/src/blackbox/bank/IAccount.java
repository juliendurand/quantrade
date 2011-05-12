package blackbox.bank;

import java.math.BigDecimal;

public interface IAccount {
	public String getId();
	public EAccountType getType();
	public BigDecimal getBalance(String currency);
	public String getCurrency();
	public void debit(AccountingEntry entry) throws Exception;
	public void credit(AccountingEntry entry) throws Exception;
}
