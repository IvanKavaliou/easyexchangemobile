package kavaliou.ivan.net.easyexchangemobile.model;

import java.math.BigDecimal;

import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import lombok.Data;

@Data
public class OperationDTO {
    private CurrencyType currency;
    private BigDecimal value;
}
