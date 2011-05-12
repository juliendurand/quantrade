package blackbox.bank;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TradingAccount extends CashAccount{

	private Map<String, Position> _positions = new HashMap<String, Position>();
	
	public TradingAccount(String id, EAccountType type) {
		super(id, type);
	}

	@Override
	public void debit(AccountingEntry entry) throws Exception {
		if(entry.getSubject()==null){
			super.debit(entry);}
		else{
			switch (getType()) {
			case Asset:
				substractFromPosition(entry.getSubject(), entry.getQuantity(), entry.getPrice());
				break;
			case Liability:
				addToPosition(entry.getSubject(), entry.getQuantity(), entry.getPrice());
				break;
			default:
				throw new Exception();
			}
		}
	}

	@Override
	public void credit(AccountingEntry entry) throws Exception {	
		if(entry.getSubject()==null){
			super.credit(entry);}
		else{
			switch (getType()) {
			case Asset:
				addToPosition(entry.getSubject(), entry.getQuantity(), entry.getPrice());
				break;
			case Liability:
				substractFromPosition(entry.getSubject(), entry.getQuantity(), entry.getPrice());
				break;
			default:
				throw new Exception();
			}
		}
	}
	
	private void addToPosition(String ticker, BigDecimal quantity, BigDecimal price){
		Position p = _positions.get(ticker);
		if(p==null){
			p= new Position(ticker, BigDecimal.ZERO, BigDecimal.ZERO);
		}
		p.setPrice(price);
		p.setQuantity(p.getQuantity().add(quantity));
		if(p.getQuantity()==BigDecimal.ZERO){
			_positions.remove(ticker);
		}else{
			_positions.put(ticker, p);
		}
	}
	
	private void substractFromPosition(String ticker, BigDecimal quantity, BigDecimal price){
		addToPosition(ticker, quantity.negate(), price);
	}
	
	public BigDecimal getCash(){
		return super.getBalance("EUR");
	}
	
	public BigDecimal getPositions(){
		BigDecimal sumPositions = BigDecimal.ZERO;
		for(Position p : _positions.values()){
			BigDecimal quantity = p.getQuantity();
			BigDecimal price = p.getPrice();
			BigDecimal value = quantity.multiply(price);
			sumPositions = sumPositions.add(value);
		}
		return sumPositions;
	}
	
	@Override
	public BigDecimal getBalance(String currency) {
		return getCash().add(getPositions());
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------------------------------------\n");
		buffer.append(getId());
		buffer.append(" - PORTFOLIO\n");
		buffer.append("------------------------------------------\n\n");
		buffer.append("Positions\n");
		buffer.append("******\n\n");
		BigDecimal sumPositions = BigDecimal.ZERO;
		for(Position p : _positions.values()){
			buffer.append(p.getTicker());
			buffer.append("\t\t");
			BigDecimal quantity = p.getQuantity();
			BigDecimal price = p.getPrice();
			BigDecimal value = quantity.multiply(price);
			sumPositions = sumPositions.add(value);
			buffer.append(quantity);
			buffer.append(" x ");
			buffer.append(price);
			buffer.append(" ");
			buffer.append(getCurrency());
			buffer.append(" = ");
			buffer.append(value);
			buffer.append(" ");
			buffer.append(getCurrency());
			buffer.append("\n");
		}
		buffer.append("\nTotal positions: ");
		buffer.append(getPositions());
		buffer.append(" ");
		buffer.append(getCurrency());
		buffer.append("\n\n");
		buffer.append("Cash\n");
		buffer.append("***********\n");
		buffer.append("\nTotal cash: ");
		buffer.append(getCash());
		buffer.append(" ");
		buffer.append(getCurrency());
		buffer.append("\n\n");
		buffer.append("Portfolio\n");
		buffer.append("***********\n");
		buffer.append("\nTotal portfolio: ");
		buffer.append(getBalance("EUR"));
		buffer.append(" ");
		buffer.append(getCurrency());
		buffer.append("\n\n");
		buffer.append("------------------------------------------\n");
		buffer.append("END OF PORTFOLIO\n");
		buffer.append("------------------------------------------\n");
		return buffer.toString();
	}
	
}
