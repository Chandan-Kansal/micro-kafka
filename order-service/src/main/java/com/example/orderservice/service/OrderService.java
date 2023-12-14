package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest){
        Order order= new Order();
        order.setOrderNumber(UUID.randomUUID().toString());


        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        List<String> quantities = new ArrayList<>();
        for(int i=0;i<order.getOrderLineItemsList().size();i++){
            quantities.add(orderLineItems.get(i).getQuantity().toString());
        }



        //  call Inventory service to check if product is present in inventory
        Optional<Boolean> allProductsInStock = Optional.ofNullable(webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).queryParam("quantity", quantities).build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());

        if(allProductsInStock.isEmpty()) {
            System.out.println("***************************************************");
        }
        else if(allProductsInStock.get()){
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic","Order placed - {}",(order.getOrderNumber()));
        }else{
            kafkaTemplate.send("notificationTopic","Item OutOfStock - {}",("Order Failed"));
            throw new IllegalArgumentException("Product is  not in stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
