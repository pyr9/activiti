package com.pyr.config;

import static java.util.Arrays.asList;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DemoApplicationConfiguration {

    private Logger logger = LoggerFactory.getLogger(DemoApplicationConfiguration.class);

    @Bean
    public UserDetailsService myUserDetailsService() {

        // 把用户存储在内存中
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();

        // 构建用户信息， Role是角色前缀， Group是组前缀
        String[][] usersGroupsAndRoles = {
                {"bob", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"john", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"hannah", "password", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"other", "password", "ROLE_ACTIVITI_USER", "GROUP_otherTeam"},
                {"system","password","ROLE_ACTIVITI_USER"},
                {"admin", "password", "ROLE_ACTIVITI_ADMIN"},
        };

        // 把用户放在内存中
        for (String[] user : usersGroupsAndRoles) {
            List<String> authoritiesStrings = asList(Arrays.copyOfRange(user, 2, user.length));
            logger.info("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings + "]");
            inMemoryUserDetailsManager.createUser(new User(user[0], passwordEncoder().encode(user[1]),
                    authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
        }


        return inMemoryUserDetailsManager;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

