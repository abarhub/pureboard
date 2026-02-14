package org.pureboard.utils;

import org.pureboard.dashboard.Card;
import org.pureboard.dto.CardDto;
import org.pureboard.properties.CardProperties;
import org.pureboard.service.ContexteService;

import java.util.List;

public interface GroovyCards {

    List<Card> getListCard(CardProperties cardProperties);

    void getCard(Card card, CardDto cardDto);

    default void setContexteService(ContexteService contexteService) {
    }
}
