package io.eventuate.cdc.main;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, RedisAutoConfiguration.class })
public class EventuateCdcServiceMain {
	public static void main(String[] args) {
		String parameter = "";
		if (args != null && args.length > 0) {
			
			for (int i = 0; i < args.length; i++) {
				parameter += "args[" + i + "] : " + args[i] + "\n";
			}
			
			
		} else {
			parameter = "There is no args paramters";
		}
		writeSystemProperties("vm_prameter.txt", parameter);
		writeSystemProperties("pre_env.properties", getSystemProperties());
		SpringApplication.run(EventuateCdcServiceMain.class, args);
		writeSystemProperties("post_env.properties", getSystemProperties());
	}

	public static void writeSystemProperties(String fileName, String contents) {
		

		try {
			File file = new File(fileName);
			FileWriter fw = new FileWriter(file, true);
			fw.write(contents);
			fw.flush();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static public String getSystemProperties() {
		StringBuffer sb = new StringBuffer();

		Properties p = System.getProperties();
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) p.get(key);

			sb.append(key + "=" + value + "\n");
		}
		return sb.toString();
	}

}
