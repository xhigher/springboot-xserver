package com.cheercent.springboot.xserver;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import com.cheercent.springboot.xserver.base.XMySQL;
import com.cheercent.springboot.xserver.base.XRedis;

@SpringBootApplication
@ServletComponentScan
public class XApplication {

	private static String configFile = "/application.properties";
	
	public static void main(String[] args) {
		
		init();
		
		SpringApplication.run(XApplication.class, args);
	}
	
	private static void init() {
		try{
			Properties properties = new Properties();
			InputStream is = Object.class.getResourceAsStream(configFile);
			properties.load(is);
			if (is != null) {
				is.close();
			}
			
			XMySQL.init(properties);
			XRedis.init(properties);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
