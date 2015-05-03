package spittr.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.async.DeferredResult;
import spittr.Spittle;
import spittr.config.AmqpConfig;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by shawnritchie on 22/04/15.
 */
public class SpittleEventManagerImpl implements SpittleEventManager {

    public ConcurrentHashMap<String, DeferredResult<Spittle>> requests =
            new ConcurrentHashMap<String, DeferredResult<Spittle>>();

    @Autowired
    @Qualifier("replyTemplate")
    private RabbitTemplate rabbit;

    public void sendSpittleAlert(DeferredResult<Spittle> deferredResult, String uuid, Spittle spittle) {

        Message message = MessageBuilder
                .withBody(SerializationUtils.serialize(spittle))
                .setHeader("uuidrequest", uuid)
                .setCorrelationId(AmqpConfig.responseRoutingKey.getBytes())
                .build();

        requests.put(uuid, deferredResult);

        rabbit.convertAndSend(message);
    }

    public class SpittleResponseHandler implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try
            {
                System.out.println("Recieved: Response");
                String uuid = (String) message.getMessageProperties().getHeaders().get("uuidrequest");
                Spittle spittle = (Spittle) org.springframework.util.SerializationUtils.deserialize(message.getBody());

                System.out.println("uuid: " + uuid);
                System.out.println("spittle: " + spittle.getMessage());

                DeferredResult<Spittle> deferredResult = requests.get(uuid);

                if (deferredResult != null)
                    deferredResult.setResult((Spittle) org.springframework.util.SerializationUtils.deserialize(message.getBody()));

                System.out.println(deferredResult.getResult().toString());

                requests.remove(uuid);
            }
            catch(Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
