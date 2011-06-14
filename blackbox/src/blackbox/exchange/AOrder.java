package blackbox.exchange;

import java.math.BigDecimal;
import java.util.Date;

public abstract class AOrder implements IOrder {

	static private long _seq = 1;
	private String _orderId;
	private String _accoundId;
	private String _ticker;
	private BigDecimal _size;
	private OrderDirection _direction;
	private Date _expiry;
		
	public AOrder(String accoundId, String ticker, BigDecimal size,
			OrderDirection direction, Date expiry) {
		this._orderId = "ORDER_"+_seq++;
		this._accoundId = accoundId;
		this._ticker = ticker;
		this._size = size;
		this._direction = direction;
		this._expiry = expiry;
	}

	@Override
	public String getOrderId() {
		return _orderId;
	}	

	@Override
	public String getAccountId() {
		return _accoundId;
	}

	@Override
	public String getTicker() {
		return _ticker;
	}

	@Override
	public BigDecimal getSize() {
		return _size;
	}

	@Override
	public OrderDirection getDirection() {
		return _direction;
	}

	@Override
	public Date getExpiry() {
		return _expiry;
	}
	
	@Override
	public void cancel() {
	}

}
