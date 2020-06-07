package kavaliou.ivan.net.easyexchangemobile.model;

import java.io.Serializable;
import java.math.BigDecimal;

import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account implements Serializable {
    private Integer id;
    private User user;
    private CurrencyType currency;
    private BigDecimal value;
}
