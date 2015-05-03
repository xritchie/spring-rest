package spittr.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import spittr.amqp.SpittleEventManager;
import spittr.amqp.SpittleEventManagerImpl;

import java.util.HashMap;

/**
 * Created by shawnritchie on 26/04/15.
 */
@Configuration
public class AmqpConfig {

    protected final String genericExchangeName = "generic.exchange";

    public final static String responseRoutingKey = "response.routing.key";
    protected final  String responseQueueName = "response.queue";

    protected final String replyRoutingKey = "reply.routing.key";
    protected final String replyQueueName = "reply.queue";

    protected Queue createQueue(String name) {
        return new Queue(name, false, false, false, new HashMap<String, Object>() {{
            put("x-message-ttl", 500);
        }});
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("user");
        connectionFactory.setPassword("user");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager() {
        return new RabbitTransactionManager(connectionFactory());
    }

    @Bean
    public Exchange genericExchange() {
        return new DirectExchange(this.genericExchangeName, false, false);
    }

    @Bean(name = "replyQueue")
    public Queue replyQueue() {
        return createQueue(this.replyQueueName);
    }

    @Bean(name = "replyBinding")
    @Autowired
    public Binding replyBinding(Exchange genericExchange,  @Qualifier("replyQueue") Queue replyQueue) {
        Binding binding =
                BindingBuilder
                        .bind(replyQueue)
                        .to(genericExchange)
                        .with(this.replyRoutingKey)
                        .noargs();

        return binding;
    }

    @Bean(name = "replyTemplate")
    @Autowired
    public RabbitTemplate replyTemplate(Exchange genericExchange) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey(this.replyRoutingKey);
        //Where we will synchronously receive messages from
        template.setExchange(genericExchange.getName());

        return template;
    }

    @Bean(name = "responseQueue")
    public Queue responseQueue() {
        return createQueue(this.responseQueueName);
    }

    @Bean(name = "responseBinding")
    @Autowired
    public Binding responseBinding(Exchange genericExchange,  @Qualifier("responseQueue") Queue responseQueue) {
        Binding binding =
                BindingBuilder
                        .bind(responseQueue)
                        .to(genericExchange)
                        .with(this.responseRoutingKey)
                        .noargs();

        return binding;
    }

    @Bean(name = "responseTemplate")
    @Autowired
    public RabbitTemplate responseTemplate(Exchange genericExchange) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey(this.responseRoutingKey);
        //Where we will synchronously receive messages from
        template.setExchange(genericExchange.getName());

        return template;
    }

    @Bean
    public SpittleEventManager spittleEventManager()
    {
        return new SpittleEventManagerImpl();
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer listenerContainer
        (
                @Qualifier("responseQueue") Queue responseQueue,
                SpittleEventManager spittleEventManager
        )
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(responseQueue);
        container.setMessageListener
            (
                new MessageListenerAdapter
                    (
                            ((SpittleEventManagerImpl)spittleEventManager).new SpittleResponseHandler()
                    )
            );
        return container;
    }
}
