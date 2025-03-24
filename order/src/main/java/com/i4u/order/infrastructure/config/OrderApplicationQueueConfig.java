package com.i4u.order.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OrderApplicationQueueConfig {

	private final ConnectionFactory connectionFactory;

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter()); // JSON 직렬화
		rabbitTemplate.setReplyTimeout(20000); // 응답 대기 시간 10초로 설정
		return rabbitTemplate;
	}

	@Value("${i4u.exchange}")
	private String exchange;

	@Value("${i4u.queue.delivery}")
	private String queueDelivery;

	@Value("${i4u.err.exchange}")
	private String exchangeErr;

	@Value("${i4u.err.queue.order}")
	private String queueErrOrder;

	// 정상 동작 빈
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(exchange);
	}

	@Bean
	public Queue queueDelivery() {
		return new Queue(queueDelivery);
	}

	@Bean
	Binding bindingDelivery() {
		return BindingBuilder.bind(queueDelivery()).to(exchange()).with(queueDelivery);
	}

	// 오류 동작 빈
	@Bean
	public TopicExchange exchangeErr() {
		return new TopicExchange(exchangeErr);
	}

	@Bean
	public Queue queueErrOrder() {
		return new Queue(queueErrOrder);
	}

	@Bean
	public Binding bindingErrOrder() {
		return BindingBuilder.bind(queueErrOrder()).to(exchangeErr()).with(queueErrOrder);
	}

}