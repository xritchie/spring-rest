package spittr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import spittr.Spitter;
import spittr.Spittle;
import spittr.data.SpittleRepository;
import spittr.exceptions.*;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Created by shawnritchie on 18/04/15.
 */
@RestController
@RequestMapping("/spittles")
public class SpittleApiController {
    private static final String MAX_LONG_AS_STRING = "9223372036854775807";

    private SpittleRepository spittleRepository;

    @Autowired
    public SpittleApiController(SpittleRepository spittleRepository) {
        this.spittleRepository = spittleRepository;
    }

    @RequestMapping(method= RequestMethod.GET, produces="application/json")
    public List<Spittle> spittles(
            @RequestParam(value="max", defaultValue=MAX_LONG_AS_STRING) long max,
            @RequestParam(value="count", defaultValue="20") int count) {
        return spittleRepository.findSpittles(max, count);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET, produces="application/json")
    public Spittle spittleById(@PathVariable Long id) {
        Spittle spittle = spittleRepository.findOne(id);
        return spittle;
    }

    @RequestMapping(method=RequestMethod.POST, consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Spittle> saveSpittle(@RequestBody Spittle spittle, UriComponentsBuilder ucb) {
        Spittle saved = spittleRepository.save(spittle);

        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path("/spittles/")
                .path(String.valueOf(saved.getId()))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        ResponseEntity<Spittle> responseEntity = new ResponseEntity<Spittle>(saved, headers, HttpStatus.CREATED);
        return responseEntity;
    }

    @ExceptionHandler(SpittleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody spittr.exceptions.Error spittleNotFound(SpittleNotFoundException e) {
        long spittleId = e.getSpittleId();
        return new spittr.exceptions.Error(4, "Spittle [" + spittleId + "] not found");
    }
}
