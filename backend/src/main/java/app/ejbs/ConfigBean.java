package app.ejbs;

import app.entities.Administrator;
import app.exceptions.MyConstraintViolationException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class ConfigBean {
    @EJB
    private AdministratorBean administratorBean;

    @PostConstruct
    public void populateDB()  {
        try {
            administratorBean.create("admin", "123","Bob the Builder", "banana@example.com");
        } catch (MyConstraintViolationException e) {
            throw new RuntimeException(e);
        }
    }
}
