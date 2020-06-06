package kavaliou.ivan.net.easyexchangemobile.model;

import java.util.Date;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String email;
    private String password;
    private Date registred;
    private boolean enabled;
}
