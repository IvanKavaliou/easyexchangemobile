package kavaliou.ivan.net.easyexchangemobile.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class User implements Serializable {
    private Integer id;
    private String email;
    private String password;
    private Date registred;
    private boolean enabled;
    private BigDecimal balance;
}
