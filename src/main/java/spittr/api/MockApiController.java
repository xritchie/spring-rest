package spittr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import spittr.Spitter;
import spittr.Spittle;
import spittr.amqp.SpittleEventManager;
import spittr.amqp.SpittleEventManagerImpl;

import java.util.Date;

/**
 * Created by shawnritchie on 19/04/15.
 */
@RestController
@RequestMapping("/mock")
public class MockApiController {

    @Autowired
    SpittleEventManager spittleEventManager;

    @RequestMapping(value="/", method= RequestMethod.GET, produces="application/json")
    public Spittle ping() {
        return new Spittle(1L, new Spitter(1L, "username", "password", "fullName",
                "test@gmail.com", true), "test", new Date());
    }

    @RequestMapping(value="/create", method=RequestMethod.GET, produces="application/json")
    public DeferredResult<Spittle> createSpittle() {
        DeferredResult<Spittle> deferredResult = new DeferredResult<Spittle>(3000);

        final String uuid = java.util.UUID.randomUUID().toString();
        Spittle spittle = new Spittle(1L, new Spitter(1L, "username", "password", "fullName",
                "test@gmail.com", true), "test", new Date());

        spittleEventManager.sendSpittleAlert(deferredResult, uuid, spittle);
        deferredResult.onTimeout(new Runnable() {
            public void run() {
                System.out.println("Time out request..:" + uuid);
                ((SpittleEventManagerImpl)spittleEventManager).requests.remove(uuid);
            }
        });

        return deferredResult;
    }
}
