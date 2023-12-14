package org.example.notificationservice;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Objects;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class,args);
    }
    @Autowired
    private NotificationRepository notificationRepository;
    @KafkaListener(topics="notificationTopic")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent){
        String message = "";
        if(Objects.equals(orderPlacedEvent.getOrderNumber(), "Order Failed")){
            message = "Order Not Placed, Item Out of Stock";
        }else{
            message = "Order Placed";
        }
        notificationRepository.save(new Notification(orderPlacedEvent.getOrderNumber(),message));
        log.info("Received Notification for order - {}",orderPlacedEvent.getOrderNumber());
    }
}