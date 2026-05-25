package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderCreatedEvent;
import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.model.OrderPublishEvent;
import com.cristovantamayo.order.model.OrderPublishStatusEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Validator validator;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.rabbitmq.order-exchange:orders.events.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.order-routing-key:orders.created}")
    private String routingKey;

    public void publishOrderCreated(OrderCreatedEvent orderPayload, OrderDTO orderDTO) {

        validate(orderPayload);

        UUID uniqueMessageUUID = UUID.randomUUID();
        String uniqueMessageId = uniqueMessageUUID.toString();

        registerMessageEventPending(orderDTO, uniqueMessageUUID);

        // 1. Instancia o CorrelationData informando o ID único
        CorrelationData correlationData = new CorrelationData(uniqueMessageId);

        // 2 e 3. [CAPTURA UNIFICADA: Confirms e Returns] - Escuta a resposta de rede assíncrona
        correlationData.getFuture().whenComplete((confirm, throwable) -> {
            if (throwable != null) {
                this.onPublishFailed(uniqueMessageId,
                        "Falha crítica de comunicação: " + throwable.getMessage(),
                        uniqueMessageUUID, orderDTO);
                return;
            }

            if (confirm != null) {

                // CORREÇÃO SPRING BOOT 4: Uso do método .ack() diretamente
                if (confirm.ack()) {
                    // 🟢 A Exchange aceitou a mensagem!

                    if (correlationData.getReturned() != null) {
                        this.onReturnedMessage(
                                uniqueMessageId,
                                correlationData.getReturned(),
                                uniqueMessageUUID,
                                orderDTO);

                    } else {
                        this.onPublishConfirmed(uniqueMessageUUID, orderDTO);
                    }

                } else {
                    // 🔴 A Exchange recusou a mensagem
                    this.onPublishFailed(uniqueMessageId,
                            confirm.reason(), uniqueMessageUUID, orderDTO);
                }
            }
        });


        System.out.println("📤 [Produtor] Disparando envio da mensagem ID: " + uniqueMessageId);

        // 4. Executa o envio passando o objeto de escuta acoplado
        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                orderPayload,
                correlationData
        );
    }

    private void registerMessageEventPending(OrderDTO orderDTO, UUID uniqueMessageUUID) {
        eventPublisher.publishEvent(OrderPublishEvent.builder()
                .messageId(uniqueMessageUUID)
                .correlationId(UUID.randomUUID().toString())
                .routingKey(routingKey)
                .orderDTO(orderDTO)
                .status(OrderPublishStatusEnum.PENDING)
                .build());
    }

    private void validate(OrderCreatedEvent event) {
        Set<ConstraintViolation<OrderCreatedEvent>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    // --- MÉTODOS DE CONSUMO DOS CALLBACKS NO PRODUTOR ---

    private void onPublishConfirmed(UUID messageId, OrderDTO orderDTO) {
        System.out.printf("✔ [Callback Produtor] onPublishConfirmed -> Ordem ID [%s] entregue à Exchange com sucesso!%n", messageId);

        // Regra de Negócio: Atualizar status do evento para 'ENVIADO' no banco de dados, se aplicável.
        eventPublisher.publishEvent(OrderPublishEvent.builder()
                .messageId(messageId)
                .correlationId(messageId.toString())
                .orderDTO(orderDTO)
                .status(OrderPublishStatusEnum.CONFIRMED)
                .routingKey(routingKey)
                .applyRollback(false)
                .build()
        );
    }

    private void onPublishFailed(String messageId, String reason,
                                 UUID uniqueMessageUUID, OrderDTO orderDTO) {
        System.err.println("❌ [Callback Produtor] onPublishFailed -> Falha ao publicar ordem ID [" + messageId + "]. Motivo: " + reason);
        // Regra de Negócio: Disparar um mecanismo de re-tentativa (Retry) ou alertar suporte.
        eventPublisher.publishEvent(OrderPublishEvent.builder()
                .messageId(uniqueMessageUUID)
                .orderDTO(orderDTO)
                .messageError(reason)
                .status(OrderPublishStatusEnum.FAILED)
                .routingKey(routingKey)
                .applyRollback(true)
                .build());
    }

    private void onReturnedMessage(String messageId, @Nullable ReturnedMessage returned,
                                   UUID uniqueMessageUUID, OrderDTO orderDTO) {
        String messageError = format("🚨 [Callback Produtor] onReturnedMessage -> Mensagem ID [%s] DEVOLVIDA por falta de fila correspondente!", messageId);
        System.err.println(messageError);

        String messageErrorDetail = "";
        String routingKey = "";

        if(returned != null){
            messageErrorDetail = format("Detalhe técnico - Exchange: %s | Rota: %s", returned.getExchange(), returned.getRoutingKey());
            System.err.println(messageErrorDetail);
            messageError += format(" - %s", messageErrorDetail);
            routingKey = returned.getRoutingKey();
        }

        // Regra de Negócio: Mover para uma fila de erro manual (Dead Letter / Caixa de saída de falhas).
        eventPublisher.publishEvent(OrderPublishEvent.builder()
                .messageId(uniqueMessageUUID)
                .orderDTO(orderDTO)
                .status(OrderPublishStatusEnum.RETURNED)
                .routingKey(routingKey)
                .messageError(messageError)
                .applyRollback(true)
                .build());
    }

}
