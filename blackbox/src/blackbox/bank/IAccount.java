package blackbox.bank;

import java.math.BigDecimal;

public interface IAccount {
	public String getId();
	public EAccountType getType();
	public BigDecimal getBalance(String currency);
}
