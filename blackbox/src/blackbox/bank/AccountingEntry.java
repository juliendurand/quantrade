package blackbox.bank;

import java.math.BigDecimal;

public class AccountingEntry{
	private EEntryType _type;
	private String _accountId;
	private String _subject;
	private BigDecimal _quantity = BigDecimal.ZERO;
	private BigDecimal _price = BigDecimal.ZERO;
	private String _currency = null;
	private BigDecimal _exchangeRate = BigDecimal.ONE;
	private BigDecimal _amount = BigDecimal.ZERO;
	private String _acccountingCurrency = null;
	
	public AccountingEntry(EEntryType type, String accountId, BigDecimal amount,
			String acccountingCurrency) {
		this._type = type;
		this._accountId = accountId;
		this._amount = amount;
		this._acccountingCurrency = acccountingCurrency;
	}

	public AccountingEntry(EEntryType type, String accountId, String currency,
			BigDecimal exchangeRate, BigDecimal amount,
			String acccountingCurrency) {
		this._type = type;
		this._accountId = accountId;
		this._currency = currency;
		this._exchangeRate = exchangeRate;
		this._amount = amount;
		this._acccountingCurrency = acccountingCurrency;
	}

	public AccountingEntry(EEntryType type, String accountId, String subject, BigDecimal quantity,
			BigDecimal price, BigDecimal amount,
			String acccountingCurrency) {
		this._type = type;
		this._accountId = accountId;
		this._quantity = quantity;
		this._subject = subject;
		this._price = price;
		this._amount = amount;
		this._acccountingCurrency = acccountingCurrency;
	}

	public AccountingEntry(EEntryType type, String accountId, String subject, BigDecimal quantity,
			BigDecimal price, String currency,
			BigDecimal exchangeRate, BigDecimal amount,
			String acccountingCurrency) {
		this._type = type;
		this._accountId = accountId;
		this._quantity = quantity;
		this._subject = subject;
		this._price = price;
		this._currency = currency;
		this._exchangeRate = exchangeRate;
		this._amount = amount;
		this._acccountingCurrency = acccountingCurrency;
	}

	public EEntryType getType(){
		return _type;
	}
	
	public String getAccountId() {
		return _accountId;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public String getSubject() {
		return _subject;
	}

	public BigDecimal getPrice() {
		return _price;
	}

	public String getCurrency() {
		return _currency;
	}

	public BigDecimal getExchangeRate() {
		return _exchangeRate;
	}

	public BigDecimal getAmount() {
		return _amount;
	}

	public String getAcccountingCurrency() {
		return _acccountingCurrency;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("account: ");
		buffer.append(_accountId);
		if(_quantity.compareTo(BigDecimal.ZERO)!=0){
			buffer.append(" quantity: ");
			buffer.append(_quantity);
			buffer.append(" subject: ");
			buffer.append(_subject);
			buffer.append(" price: ");
			buffer.append(_price);
		}
		if(_currency!=null){
			buffer.append(" currency: ");
			buffer.append(_currency);
			buffer.append(" exchange rate: ");
			buffer.append(_exchangeRate);
		}
		buffer.append(" amount: ");
		buffer.append(_amount);
		buffer.append(" ");
		buffer.append(_acccountingCurrency);
		return buffer.toString();
	}
}