package blackbox.bank;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank {

	private String _name = "Blackbox Bank";
	private String _currency = "EUR";
	private Map<String, IAccount> _accounts = new HashMap<String, IAccount>();

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
				assetSum.add(account.getBalance(_currency));
				break;
			case Liability:
				liabilitySum.add(account.getBalance(_currency));
				break;
			default:
				throw new Exception("Bank balance check - unknown account type.");
			}
		}
		return assetSum.compareTo(liabilitySum)==0;
	}
	
	public void processTransaction(Transaction t){
	}
	
	public void printBalanceSheet(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------------------------\n");
		buffer.append(_name);
		buffer.append(" - BALANCE SHEET\n");
		buffer.append("------------------------------------------\n\n");
		buffer.append("Assets\n");
		buffer.append("******\n\n");
		for(IAccount account : _accounts.values()){
			BigDecimal sumAssets = BigDecimal.ZERO;
			if(account.getType()==EAccountType.Asset){
				buffer.append(account.getId());
				buffer.append("\t\t");
				BigDecimal balance = account.getBalance(_currency);
				sumAssets.add(balance);
				buffer.append(balance);
				buffer.append(" ");
				buffer.append(_currency);
				buffer.append("\n");
			}
		}
		buffer.append("\n");
		buffer.append("Liabilities\n");
		buffer.append("***********\n\n");
		BigDecimal sumLiabilities = BigDecimal.ZERO;
		for(IAccount account : _accounts.values()){
			if(account.getType()==EAccountType.Liability){
				buffer.append(account.getId());
				buffer.append("\t\t");
				BigDecimal balance = account.getBalance(_currency);
				sumLiabilities.add(balance);
				buffer.append(balance);
				buffer.append(" ");
				buffer.append(_currency);
				buffer.append("\n");
			}
		}
		buffer.append("\n");
		buffer.append("------------------------------------------\n");
		buffer.append("END OF BALANCE SHEET\n");
		buffer.append("------------------------------------------\n");
		System.out.print(buffer);
	}

	public static void main(String[] params){
		Bank bank = new Bank();
		bank.printBalanceSheet();
	}
}
