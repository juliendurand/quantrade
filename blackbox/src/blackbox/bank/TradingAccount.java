package blackbox.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blackbox.exchange.IOrder.OrderDirection;
import blackbox.exchange.InterdayExchange;
import blackbox.exchange.MarketOrder;

public class TradingAccount extends CashAccount{

	private Map<String, Position> _positions = new HashMap<String, Position>();
	private IMarketPriceSource _marketPriceSource;
	private List<AccountingEntry> _operations = new ArrayList<AccountingEntry>();
	private StringBuffer _trades = new StringBuffer();
	private int _nbTrades = 0;
	
	public TradingAccount(String id, EAccountType type, IMarketPriceSource source) {
		super(id, type);
		_marketPriceSource = source;
	}
	
	public void setMarketPriceSource(IMarketPriceSource source){
		_marketPriceSource = source;
	}
	
	public void liquidate(InterdayExchange exchange){
		for(Position p : _positions.values()){
			if(p.getQuantity().compareTo(BigDecimal.ZERO)>0){
				exchange.registerOrder(new MarketOrder(getId(), p.getTicker(), p.getQuantity(), OrderDirection.Sell, new Date(System.currentTimeMillis())));
			}else{
				exchange.registerOrder(new MarketOrder(getId(), p.getTicker(), p.getQuantity().negate(), OrderDirection.Buy, new Date(System.currentTimeMillis())));
			}
		}
	}
	
	public int getNbTrades(){
		return _nbTrades;
	}

	@Override
	public void debit(AccountingEntry entry) throws Exception {
		if(entry.getSubject()==null){
			super.debit(entry);
			}
		else{
			_nbTrades++;
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

		//_operations.add(entry);
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
		//_operations.add(entry);
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
	
	public Map<String, Position> getPositions(){
		return _positions;
	}
	
	public BigDecimal getSumPositions(){
		BigDecimal sumPositions = BigDecimal.ZERO;
		for(Position p : _positions.values()){
			BigDecimal quantity = p.getQuantity();
			BigDecimal price = BigDecimal.ZERO;
			try{
				price = _marketPriceSource.getMarketOfficialPrice(p.getTicker());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			BigDecimal value = quantity.multiply(price);
			sumPositions = sumPositions.add(value);
		}
		return sumPositions;
	}
	
	@Override
	public BigDecimal getBalance(String currency) {
		return getCash().add(getSumPositions());
	}
	
	public String getOperations(){
		StringBuffer buffer = new StringBuffer();
		for(AccountingEntry op : _operations){
			buffer.append(op.getType().name());
			buffer.append(", ");
			buffer.append(op.getQuantity());
			buffer.append(", ");
			buffer.append(op.getSubject());
			buffer.append(", ");
			buffer.append(op.getPrice());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public void registerTrade(String type, String ticker, BigDecimal volume, BigDecimal price, Date date){
		_trades.append(type);
		_trades.append(", ");
		_trades.append(TradingAccount.dateFormatter.format(date));
		_trades.append(", ");
		_trades.append(ticker);
		_trades.append(", ");
		_trades.append(volume);
		_trades.append(", ");
		_trades.append(price);
		_trades.append("\n");
	}
	
	public String getTrades(){
		return _trades.toString();
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
		buffer.append(getSumPositions());
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
