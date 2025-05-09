
package com.geonpil;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.geonpil.mapper")
public class GeonpilApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeonpilApplication.class, args);
    }
}
