package blackbox.bank;

import java.math.BigDecimal;

public class Position {

	private String _ticker;
	private BigDecimal _quantity;
	private BigDecimal _price;
	
	public Position(String ticker, BigDecimal quantity, BigDecimal price) {
		this._ticker = ticker;
		this._quantity = quantity;
		this._price = price;
	}

	public String getTicker() {
		return _ticker;
	}

	public void setTicker(String ticker) {
		this._ticker = ticker;
	}

	public BigDecimal getQuantity() {
		return _quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this._quantity = quantity;
	}

	public BigDecimal getPrice() {
		return _price;
	}

	public void setPrice(BigDecimal price) {
		this._price = price;
	}	

}
