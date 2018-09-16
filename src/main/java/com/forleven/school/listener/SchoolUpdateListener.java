package com.forleven.school.listener;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.forleven.common.event.UpdatingEvent;
import com.forleven.school.model.School;

@Slf4j
@Component
public class SchoolUpdateListener {

    @EventListener
    public void handleEvent(UpdatingEvent<School> schoolCreationEvent) {
        log.info("Receive a listener when create school");
    }
}
