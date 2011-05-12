package blackbox.bank;

import java.util.List;


public class Transaction {
	
	private List<AccountingEntry> _entries;
	private long _timestamp = 0L;
	
	public Transaction(List<AccountingEntry> entries) {
		this._entries = entries;
	}

	public List<AccountingEntry> getEntries(){
		return _entries;
	}
	
	public long getTimestamp(){
		return _timestamp;
	}
	
	public long setTimestamp(long timestamp){
		return _timestamp = timestamp;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("-------------------------------------------\n");
		buffer.append("Debit:\n");
		for(AccountingEntry entry : _entries){
			if(entry.getType()==EEntryType.debit){
				buffer.append("        ");
				buffer.append(entry);
				buffer.append("\n");
			}
		}
		buffer.append("\nCredit:\n");
		for(AccountingEntry entry : _entries){
			if(entry.getType()==EEntryType.credit){
				buffer.append("        ");
				buffer.append(entry);
				buffer.append("\n");
			}
		}
		buffer.append("\n");
		return buffer.toString();
	}

}
