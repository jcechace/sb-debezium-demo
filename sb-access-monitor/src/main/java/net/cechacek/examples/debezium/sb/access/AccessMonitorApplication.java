package net.cechacek.examples.debezium.sb.access;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.debezium.inbound.DebeziumMessageProducer;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class,
				DataSourceTransactionManagerAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class
		}
)
@EnableIntegration
public class AccessMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessMonitorApplication.class, args);
	}

	@Bean
    public MessageChannel debeziumInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageProducer debeziumMessageProducer(
			DebeziumEngine.Builder<ChangeEvent<byte[], byte[]>> engineBuilder,
			MessageChannel debeziumInputChannel
	) {
		var debeziumMessageProducer = new DebeziumMessageProducer(engineBuilder);
		debeziumMessageProducer.setOutputChannel(debeziumInputChannel);
		return debeziumMessageProducer;
	}
}
