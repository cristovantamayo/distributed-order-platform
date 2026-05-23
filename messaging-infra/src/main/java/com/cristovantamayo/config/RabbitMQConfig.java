package com.cristovantamayo.config;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setClassMapper(new ClassMapper() {
            @Override
            public void fromClass(Class<?> clazz, MessageProperties properties) {
                // Kept empty to strip out __TypeId__
            }

            @Override
            public Class<?> toClass(MessageProperties properties) {
                return Object.class;
            }
        });
        return converter;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        // ATIVAÇÃO: Habilita o Publisher Returns
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(messageConverter());

        // OBRIGATÓRIO: Força o broker a devolver a mensagem se ela não for roteada
        rabbitTemplate.setMandatory(true);

        // CALLBACK: Define o comportamento ao receber uma mensagem retornada
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("Mensagem recusada pelo broker!");
            System.err.println("Código de erro: " + returned.getReplyCode());
            System.err.println("Motivo: " + returned.getReplyText());
            System.err.println("Exchange: " + returned.getExchange());
            System.err.println("Routing Key: " + returned.getRoutingKey());
            System.err.println("Conteúdo: " + new String(returned.getMessage().getBody()));
        });

        // NOVO: Confirmações (Para saber se a Exchange recebeu com sucesso)
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("✔ Mensagem entregue com sucesso à Exchange!");
            } else {
                System.err.println("❌ Falha na entrega! A Exchange rejeitou a mensagem.");
                System.err.println("Motivo do descarte: " + cause);
            }
        });

        return rabbitTemplate;
    }
}
