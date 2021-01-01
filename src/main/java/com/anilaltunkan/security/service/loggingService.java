package com.anilaltunkan.security.service;

import com.anilaltunkan.security.AuthenticationTestApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class loggingService {

    private Object principal;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationTestApplication.class);

    public void writeLogging(String message) {

        try {
            this.principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.info("---------------------------------------------------------------------------------------------------");
            logger.info(message + " >> By \"" + ((UserDetails) principal).getUsername() + "\"");
        }
        catch (Exception ex){}
    }
}
