package org.pureboard.utils;

import org.pureboard.dashboard.Card;
import org.pureboard.dto.CardDto;
import org.pureboard.dto.TableauDto;
import org.pureboard.properties.CardProperties;
import org.pureboard.service.ContexteService;

import java.util.List;

public interface GroovyCards {

    default List<Card> getListCard(CardProperties cardProperties){
        return List.of();
    }

    default void getCard(Card card, CardDto cardDto){

    }

    default void setContexteService(ContexteService contexteService) {
    }

    default TableauDto getTableau(CardProperties cardProperties){
        return null;
    }
}
