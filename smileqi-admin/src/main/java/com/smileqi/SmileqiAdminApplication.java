package com.smileqi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.smileqi.*.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SmileqiAdminApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(SmileqiAdminApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  SmileQi启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
