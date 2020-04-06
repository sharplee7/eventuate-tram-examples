package io.eventuate.local.mysql.binlog;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ColumnOrderExtractorTest.Config.class)
public class ColumnOrderExtractorTest extends AbstractColumnOrderExtractorTest {

  @Configuration
  @EnableAutoConfiguration
  public static class Config {
  }
}
