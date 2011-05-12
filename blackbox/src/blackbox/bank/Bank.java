package blackbox.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;

public class Bank {
	
	public final String CASH_ACCOUNT = "CASH";
	public final String EXCHANGE_ACCOUNT = "EXCHANGE";
	public final String REVENUE_ACCOUNT = "REVENUE";

	private String _name = "Blackbox Bank";
	private String _currency = "EUR";
	private Map<String, IAccount> _accounts = new HashMap<String, IAccount>();

	public Bank(){
		addAccount(new CashAccount(CASH_ACCOUNT, EAccountType.Asset));
		addAccount(new TradingAccount(EXCHANGE_ACCOUNT, EAccountType.Asset));
		addAccount(new TradingAccount(REVENUE_ACCOUNT, EAccountType.Liability));
	}
	
	public String getName(){
		return _name;
	}
	
	public String getcurrency(){
		return _currency;
	}
	
	public void addAccount(IAccount account){
		_accounts.put(account.getId(), account);
	}
	
	public void deleteAccount(String accountId) throws Exception{
		if(!_accounts.containsKey(accountId)){
			throw new Exception("Delete account : Bad accound Id");
		}
		_accounts.remove(accountId);
	}
	
	public boolean checkBalance() throws Exception{
		BigDecimal assetSum = BigDecimal.ZERO;
		BigDecimal liabilitySum = BigDecimal.ZERO;
		for(IAccount account : _accounts.values()){
			switch (account.getType()) {
			case Asset:
				assetSum = assetSum.add(account.getBalance(_currency));
				break;
			case Liability:
				liabilitySum = liabilitySum.add(account.getBalance(_currency));
				break;
			default:
				throw new Exception("Bank balance check - unknown account type.");
			}
		}
		return assetSum.compareTo(liabilitySum)==0;
	}
	
	public void makeDeposit(String accountId, BigDecimal amount, String accountingCurrency) throws Exception{
		ArrayList<AccountingEntry> entries = new ArrayList<AccountingEntry>();
		entries.add(new AccountingEntry(EEntryType.debit, accountId, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.credit, CASH_ACCOUNT, amount, accountingCurrency));
		processTransaction(new Transaction(entries));
	}
	
	public void BuyInstrument(String accountId, String ticker, BigDecimal quantity, BigDecimal price, String accountingCurrency) throws Exception {
		ArrayList<AccountingEntry> entries = new ArrayList<AccountingEntry>();
		BigDecimal amount = price.multiply(quantity);
		entries.add(new AccountingEntry(EEntryType.debit, accountId, ticker, quantity, price, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.credit, accountId, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.credit, EXCHANGE_ACCOUNT, ticker, quantity, price, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.debit, CASH_ACCOUNT, amount, accountingCurrency));
		processTransaction(new Transaction(entries));
	}
	
	public void SellInstrument(String accountId, String ticker, BigDecimal quantity, BigDecimal price, String accountingCurrency) throws Exception {
		ArrayList<AccountingEntry> entries = new ArrayList<AccountingEntry>();
		BigDecimal amount = price.multiply(quantity);
		entries.add(new AccountingEntry(EEntryType.credit, accountId, ticker, quantity, price, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.debit, accountId, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.debit, EXCHANGE_ACCOUNT, ticker, quantity, price, amount, accountingCurrency));
		entries.add(new AccountingEntry(EEntryType.credit, CASH_ACCOUNT, amount, accountingCurrency));
		processTransaction(new Transaction(entries));
	}
	
	public void processTransaction(Transaction t) throws Exception{
		for(AccountingEntry entry : t.getEntries()){
			IAccount account = _accounts.get(entry.getAccountId());
			if(account==null){
				throw new Exception("Transaction - Bad account");
			}
		}
		for(AccountingEntry entry : t.getEntries()){
			IAccount account = _accounts.get(entry.getAccountId());
			switch (entry.getType()) {
			case debit:
				account.debit(entry);
				break;
			case credit:
				account.credit(entry);
				break;
			default:
				throw new Exception();
			}
		}
		if(!checkBalance()){
			throw new Exception("Transaction corrupted the bank books - Balance is unbalanced.");
		}
	}
	
	public void printBalanceSheet(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------------------------\n");
		buffer.append(_name);
		buffer.append(" - BALANCE SHEET\n");
		buffer.append("------------------------------------------\n\n");
		buffer.append("Assets\n");
		buffer.append("******\n\n");
		BigDecimal sumAssets = BigDecimal.ZERO;
		for(IAccount account : _accounts.values()){
			if(account.getType()==EAccountType.Asset){
				buffer.append(account.getId());
				buffer.append("\t\t");
				BigDecimal balance = account.getBalance(_currency);
				sumAssets = sumAssets.add(balance);
				buffer.append(balance);
				buffer.append(" ");
				buffer.append(_currency);
				buffer.append("\n");
			}
		}
		buffer.append("\nTotal assets: ");
		buffer.append(sumAssets);
		buffer.append(" ");
		buffer.append(_currency);
		buffer.append("\n\n");
		buffer.append("Liabilities\n");
		buffer.append("***********\n\n");
		BigDecimal sumLiabilities = BigDecimal.ZERO;
		for(IAccount account : _accounts.values()){
			if(account.getType()==EAccountType.Liability){
				buffer.append(account.getId());
				buffer.append("\t\t");
				BigDecimal balance = account.getBalance(_currency);
				sumLiabilities = sumLiabilities.add(balance);
				buffer.append(balance);
				buffer.append(" ");
				buffer.append(_currency);
				buffer.append("\n");
			}
		}
		buffer.append("\nTotal liabilities: ");
		buffer.append(sumLiabilities);
		buffer.append(" ");
		buffer.append(_currency);
		buffer.append("\n\n");
		buffer.append("------------------------------------------\n");
		buffer.append("END OF BALANCE SHEET\n");
		buffer.append("------------------------------------------\n");
		System.out.print(buffer);
	}

	public static void main(String[] params){
		Bank bank = new Bank();
		bank.addAccount(new TradingAccount("trader 1", EAccountType.Liability));
		bank.addAccount(new TradingAccount("trader 2", EAccountType.Liability));
		try{
			bank.makeDeposit("trader 1", BigDecimal.valueOf(1874.72), "EUR");	
			bank.makeDeposit("trader 2", BigDecimal.valueOf(1000.00), "EUR");
			bank.BuyInstrument("trader 1", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
			bank.BuyInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.73d), "EUR");
			bank.SellInstrument("trader 2", "FTE.PA", BigDecimal.valueOf(28), BigDecimal.valueOf(15.98d), "EUR");
		}catch (Exception e) {
			e.printStackTrace();
		}
		bank.printBalanceSheet();
		//System.out.print(bank._accounts.get("trader 1"));
		System.out.print(bank._accounts.get("trader 2"));
		System.out.print(bank._accounts.get(bank.EXCHANGE_ACCOUNT));
	}
}
