package com.bpm.measurementstorageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RabbitMQConfiguration {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing.measurement}")
    private String measurementRoutingKey;
    @Value("${rabbitmq.routing.availability}")
    private String availabilityRoutingKey;
    @Value("${rabbitmq.queue.measurement}")
    private String measurementQueueName;
    @Value("${rabbitmq.queue.availability}")
    private String availabilityQueueName;

    @Bean
    public TopicExchange exchange() {
        return ExchangeBuilder.topicExchange(exchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public Queue measurementQueue() {
        return QueueBuilder.durable(measurementQueueName).build();
    }

    @Bean
    public Queue availabilityQueue() {
        return QueueBuilder.durable(availabilityQueueName).build();
    }

    @Bean
    public Binding measurementBinding() {
        return BindingBuilder.bind(measurementQueue())
                .to(exchange())
                .with(measurementRoutingKey);
    }

    @Bean
    public Binding availabilityBinding() {
        return BindingBuilder.bind(availabilityQueue())
                .to(exchange())
                .with(availabilityRoutingKey);
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
