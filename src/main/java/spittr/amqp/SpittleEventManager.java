package spittr.amqp;

import org.springframework.web.context.request.async.DeferredResult;
import spittr.Spittle;

/**
 * Created by shawnritchie on 22/04/15.
 */
public interface SpittleEventManager {
    void sendSpittleAlert(DeferredResult<Spittle> deferredResult, String uuid, Spittle spittle);
}
