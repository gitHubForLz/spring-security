package cn.auth.conf;

import cn.auth.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationProvider provider;  //注入我们自己的AuthenticationProvider
    @Autowired
    private DataSource dataSource;   //是在application.properites
    @Autowired
    private MyUserDetailsService userDetailService;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TODO Auto-generated method stub
        http
                .formLogin()//.loginPage("/login")            //登录页面的表单
                .loginProcessingUrl("/login/form")          //表单提交过来的action地址
                .failureUrl("/login-error")
                .permitAll()                        //表单登录，permitAll()表示这个不需要验证 登录页面，登录失败页面 // 设置所有人都可以访问登录页面
                .and()
                .rememberMe()
                    .rememberMeParameter("remember-me")
                    .userDetailsService(userDetailService)
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(600)          //设置10分钟
                .and()
                //.authorizeRequests().antMatchers("/whoim").hasRole("ADMIN")// 第一种 定义哪些URL需要被保护、哪些不需要被保护
                .authorizeRequests().anyRequest().access("@rbacService.hasPermission(request,authentication)")    //第二种 自定义通过用户 角色 资源控制
                //.anyRequest().authenticated()// 任何请求,登录后可以访问.
                .and()
                .csrf().disable();                   // 关闭csrf防护
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // TODO Auto-generated method stub
        auth.authenticationProvider(provider);//第二种查数据库中的
//                .inMemoryAuthentication()
//                .passwordEncoder(
//                        new BCryptPasswordEncoder()).withUser("user").password(new BCryptPasswordEncoder().encode("123456")).roles("USER")
//                .and()
//                .passwordEncoder(
//                        new BCryptPasswordEncoder()).withUser("admin").password(new BCryptPasswordEncoder().encode("123456")).roles("ADMIN");
    }
  /*
  前期配置
    spring-boot-starter-thymeleaf 将html页面之间放到 tempLata下面
    其他静态资源static
  第一、配置依赖后即可生效
        .formLogin()//.loginPage("/login")，不配置就默认的页面，里面可以选择自己的登录页面、表单提交页面、错误地址
  第二、配置用户角色
       1. AuthenticationManagerBuilder auth 配置临时角色写死
       2.数据库得到信息 处理返回UserInfo 认证MyAuthenticationProvider 注入MyUserInfo 得到用户 角色信息 通过
  第三、配置UserInfo
        实现 Serializable, UserDetails接口 重写某些方法
  第四、配置权限
       .authorizeRequests().anyRequest().access("@rbacService.hasPermission(request,authentication)")
       自定义RBAC服务，属性AntPathMatcher 匹配urls中与与请求url 返回 permission权限
  第五、记住功能 保存token 写到cookies 临时不能关闭浏览器按器，自己页login页面写参数，接收重启服务器后可继续访问
        依赖  DataSource  spring-boot-starter-jdbc
             连接Mysql     MYsql-connect-java
             抽象类DaoSupport模板              >spring-jdbc

    ======================================分割线   详情见下面===============================



  .and().withUser("test").password("test123").roles("ADMIN");  这样我们就有了一个用户名为test,密码为test123的用户了。
    第一种的只是让我们体验了一下Spring Security而已，我们接下来就要提供自定义的用户认证机制及处理过程。
    在讲这个之前，我们需要知道spring security的原理，spring security的原理就是使用很多的拦截器对URL进行拦截，以此来管理登录验证和用户权限验证。

    用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现，而且AuthenticationManager会调用ProviderManager来获取用户验证信息
    （不同的Provider调用的服务不同，因为这些信息可以是在数据库上，可以是在LDAP服务器上，可以是xml配置文件上等），
    如果验证通过后会将用户的权限信息封装一个User放到spring的全局缓存SecurityContextHolder中，以备后面访问资源时使用。

    所以我们要自定义用户的校验机制的话，我们只要实现自己的AuthenticationProvider就可以了。在用AuthenticationProvider 这个之前，我们需要提供一个获取用户信息的服务，实现
    UserDetailsService 接口
    用户名密码->(Authentication(未认证)  ->  AuthenticationManager ->AuthenticationProvider->UserDetailService->UserDetails->Authentication(已认证）
     了解了这个原理之后，我们就开始写代码

     改造权限
     之前的代码我们用户的权限没有加以利用，现在我们添加权限的用法。
       之前的登录验证通俗的说，就是来判断你是谁（认证），而权限控制就是用来确定：你能做什么或者不能做什么（权限）

        在讲这个之前，我们简单说下，对于一些资源不需要权限认证的，那么就可以在Config中添加 过滤条件，如：

        .antMatchers 这里也可以限定HttpMethod的不同要求不同的权限（用于适用于Restful风格的API).
           如：Post需要 管理员权限，get 需要user权限，我们可以这么个改造，同时也可以通过通配符来是实现 如：/user/1 这种带参数的URL
        .antMatchers("/whoim").hasRole("ADMIN")
      .antMatchers(HttpMethod.POST,"/user/*").hasRole("ADMIN")
      .antMatchers(HttpMethod.GET,"/user/*").hasRole("USER")

      Spring Security 的校验的原理：左手配置信息，右手登录后的用户信息，中间投票器。
 从我们的配置信息中获取相关的URL和需要的权限信息，然后获得登录后的用户信息，
然后经过：AccessDecisionManager 来验证，这里面有多个投票器：AccessDecisionVoter，（默认有几种实现：比如：1票否决（只要有一个不同意，就没有权限），全票通过，才算通过；只要有1个通过，就全部通过。类似这种的。
WebExpressionVoter 是Spring Security默认提供的的web开发的投票器。（表达式的投票器）
Spring Security 默认的是 AffirmativeBased   只要有一个通过，就通过。
有兴趣的可以 从FilterSecurityInterceptor这个过滤器入口，来查看这个流程。
内嵌的表达式有：permitAll  denyAll   等等。每一个权限表达式都对应一个方法。
如果需要同时满足多个要求的，不能连写如 ，我们有个URL需要管理员权限也同时要限定IP的话，
不能：.hasRole("ADMIN").hasIPAddress("192.168.1.1");
而是需要用access方法
.access("hasRole('ADMIN') and hasIpAddress('192.168.1.1')");这种。

那我们可以自己写权限表达式吗？ 可以，稍后。。。这些都是硬编码的实现，都是在代码中写入的，这样的灵活性不够。所以我们接下来继续改造

改造4、添加基于RBAC(role-Based-access control)权限控制
这个大家可以去百度一下，一般都是由 3个部分组成，一个是用户，一个是角色 ，一个是资源（菜单，按钮），然后就是 用户和角色的关联表，角色和资源的关联表
核心就是判断当前的用户所拥有的URL是否和当前访问的URL是否匹配

改造5、记住我的功能Remeber me
本质是通过token来读取用户信息，所以服务端需要存储下token信息 根据官方的文档，token可以通过数据库存储 数据库脚本
     */

}

