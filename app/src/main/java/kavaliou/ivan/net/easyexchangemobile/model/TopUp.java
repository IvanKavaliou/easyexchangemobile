package kavaliou.ivan.net.easyexchangemobile.model;

import java.io.Serializable;
import java.math.BigDecimal;

import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

@Data
public class TopUp implements Serializable {
    private BigDecimal value;
}