package blackbox.bank;

import java.math.BigDecimal;

public interface IMarketPriceSource {
	BigDecimal getMarketOfficialPrice(String ticker) throws Exception;
}
