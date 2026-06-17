package com.contact;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 联系人管理系统启动类
 *
 * @author Contact Manager
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.contact.mapper")
public class ContactApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactApplication.class, args);
        System.out.println("==========================================");
        System.out.println("   联系人管理系统启动成功！");
        System.out.println("   API文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("==========================================");
    }
}
