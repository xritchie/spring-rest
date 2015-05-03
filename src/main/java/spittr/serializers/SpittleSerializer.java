package spittr.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import spittr.Spittle;

import java.io.IOException;

/**
 * Created by shawnritchie on 20/04/15.
 */
public class SpittleSerializer extends JsonSerializer<Spittle> {
    @Override
    public void serialize(Spittle value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        if (value != null) {
            jgen.writeNumberField("id", value.getId() != null ? value.getId().longValue() : 0L);
            jgen.writeStringField("message", value.getMessage());
            jgen.writeObjectField("posted", value.getPostedTime());
            jgen.writeObjectField("spitter", value.getSpitter());
        }
        jgen.writeEndObject();
    }
}
