package cn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages ={ "cn.**"})
@MapperScan(basePackages = "cn.mapper")
public class App extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

    }

//    @Bean
//    public SqlSessionFactory sessionFactory() throws Exception{
//        SqlSessionFactory sqlSessionFactory =
//                new SqlSessionFactoryBean().getObject();
//        return sqlSessionFactory;
//    }


}
