package com.maohua.order;

import com.maohua.order.entity.OrderReturnReasonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Map;

@SpringBootTest
class OrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        System.out.println("Exchange created successfully...............");
    }
    @Test
    void createQueue(){
            //public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments) {

            Queue queue= new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        System.out.println("Queue created successfully...............");

    }
    @Test
    public void createBinding(){
        //    public Binding(String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        System.out.println("Binding created successfully...............");

    }
    @Test
    public void sendMessage(){
//
//        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
//        reasonEntity.setId(1L);
//        reasonEntity.setCreateTime(new Date());
//        reasonEntity.setName("hhh");
        //如果发送的消息是个对象，我们会使用序列化机制，将对象写出去， 所以对象必须实现Serializable

       // rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", "hello world");
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
//        System.out.println("data sent successfuly,  "+reasonEntity);
        //发送的对象是json
        for(int i = 0; i < 10; i++){

            OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
            reasonEntity.setId(1L);
            reasonEntity.setCreateTime(new Date());
            reasonEntity.setName("hhh "+i);
            rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
            System.out.println(reasonEntity);
        }

    }



}
