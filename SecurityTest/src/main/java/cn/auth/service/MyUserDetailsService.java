package cn.auth.service;
import cn.auth.model.UserInfo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



@Component
@Service
public class MyUserDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

//这里可以通过数据库来查找到实际的用户信息，这里我们先模拟下,后续我们用数据库来实现
        if (username.equals("admin")) {
            //假设返回的用户信息如下;
            UserInfo user = new UserInfo("admin", "123456", "ROLE_ADMIN", true, true, true, true);
            return user;
        }
        if (username.equals("user")) {
            //假设返回的用户信息如下;
            UserInfo user = new UserInfo("user", "123456", "ROLE_USER", true, true, true, true);
            return user;
        }

        return null;
    }
}
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
//
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    // BrowerSecurityConfig
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        logger.info("用户的用户名: {}", username);
//        // TODO 根据用户名，查找到对应的密码，与权限
//        String password = passwordEncoder.encode("123456");
//        logger.info("password: {}", password);
//
//        // 封装用户信息，并返回。参数分别是：用户名，密码，用户权限
//        User user = new User(username, password,
//                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
//        return user;
//    }

