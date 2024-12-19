package com.collabflow.config;

import com.collabflow.service.AuditListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AuditListenerContextProvider implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // Injecter le contexte de Spring dans la classe AuditListener
        AuditListener.setApplicationContext(applicationContext);
    }
}
