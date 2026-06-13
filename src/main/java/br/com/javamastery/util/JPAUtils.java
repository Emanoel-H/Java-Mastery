package br.com.javamastery.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtils {
    private static Map<String, String> envConfig = new HashMap<>();
    public static final EntityManagerFactory FACTORY;

    static {
        envConfig.put("javax.persistence.jdbc.url", System.getenv("DB_URL"));
        envConfig.put("javax.persistence.jdbc.user", System.getenv("DB_USER"));
        envConfig.put("javax.persistence.jdbc.password", System.getenv("DB_PASS"));

        FACTORY = Persistence.createEntityManagerFactory("firstproject", envConfig);
    }

    public EntityManager getEntityManager(){
        return FACTORY.createEntityManager();
    }
}
