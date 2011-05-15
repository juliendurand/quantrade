package blackbox.bank;

import java.math.BigDecimal;

public class CashAccount implements IAccount {

	private String _id;
	private EAccountType _type;
	private BigDecimal _balance = BigDecimal.ZERO;
	private String Currency = "EUR";
	private StringBuffer _balanceHistory = new StringBuffer();
	
	public CashAccount(String id, EAccountType type){
		_id = id;
		_type = type;
	}
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public EAccountType getType() {
		return _type;
	}

	@Override
	public BigDecimal getBalance(String currency) {
		return _balance;
	}
	
	@Override
	public String getCurrency(){
		return Currency;
	}

	@Override
	public void debit(AccountingEntry entry) throws Exception {
		switch (getType()) {
		case Asset:
			_balance = _balance.subtract(entry.getAmount());
			break;
		case Liability:
			_balance = _balance.add(entry.getAmount());
			break;
		default:
			throw new Exception();
		}
	}

	@Override
	public void credit(AccountingEntry entry) throws Exception{
		switch (getType()) {
		case Asset:
			_balance = _balance.add(entry.getAmount());
			break;
		case Liability:
			_balance = _balance.subtract(entry.getAmount());
			break;
		default:
			throw new Exception();
		}
	}
	
	@Override
	public void closingRun(String date){
		_balanceHistory.append(date);
		_balanceHistory.append(", ");
		_balanceHistory.append(getBalance("EUR"));
		_balanceHistory.append("\n");
	}
	
	@Override
	public String getBalanceHistory(){
		return _balanceHistory.toString();
	}

}
