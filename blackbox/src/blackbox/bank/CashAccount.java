package blackbox.bank;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashAccount implements IAccount {

	private String _id;
	private EAccountType _type;
	private BigDecimal _balance = BigDecimal.ZERO;
	private String Currency = "EUR";
	private List<DailyBalance> _balanceHistory = new ArrayList<DailyBalance>();
	public static DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	
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
	public void closingRun(Date date){
		_balanceHistory.add(new DailyBalance(date, getBalance("EUR")));	
	}
	
	@Override
	public List<DailyBalance> getHistory(){
		return _balanceHistory;
	}
	
	@Override
	public String getBalanceHistory(){
		StringBuffer buffer = new StringBuffer();
		for(DailyBalance day : _balanceHistory){
			buffer.append(dateFormatter.format(day.date));
			buffer.append(", ");
			buffer.append(day.value);
			buffer.append("\n");
		}
		return buffer.toString();
	}

}
