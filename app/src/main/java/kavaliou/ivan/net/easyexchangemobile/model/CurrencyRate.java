package kavaliou.ivan.net.easyexchangemobile.model;

import java.io.Serializable;
import java.math.BigDecimal;

import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyRate implements Serializable {
    private String currency;
    private CurrencyType code;
    private BigDecimal bid;
    private BigDecimal ask;
}
