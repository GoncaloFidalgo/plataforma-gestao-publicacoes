package pt.ipleiria.estg.dei.ei.dae.plataforma.ejbs;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class ConfigBean {
    @EJB
    private UserBean userBean;
    @PostConstruct
    public void populateDB() {
       userBean.create("goncalovital", "123", "goncalo@gmail.com", "Gonçalo Vital");
    }
}
