package br.com.himax.saga.orchestration.order.core.mapper;

import br.com.himax.saga.orchestration.order.domain.document.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            return mapper.readValue(json, Event.class);
        }catch (Exception e){
            log.error(json);
            log.error(e.getMessage());
            return null;
        }
    }
}
