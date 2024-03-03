package br.com.himax.saga.orchestration.product.core.mapper;

import br.com.himax.saga.orchestration.product.api.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class JsonHelper {
    private final ObjectMapper mapper;

    public String toJson(Object object){
        try{
            return mapper.writeValueAsString(object);
        }catch (Exception e){
            log.error(object.toString());
            log.error(e.getMessage());
            return "";
        }
    }

    public Event toEvent(String json){
        try{
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, Event.class);
        }catch (Exception e){
            log.error(json);
            log.error(e.getMessage());
            return null;
        }
    }
}
