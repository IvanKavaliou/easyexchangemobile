package kavaliou.ivan.net.easyexchangemobile.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction implements Serializable {
    private Integer id;
    private Account account;
    private BigDecimal value;
    private TransactionType transaction;
    private Date date;
}
