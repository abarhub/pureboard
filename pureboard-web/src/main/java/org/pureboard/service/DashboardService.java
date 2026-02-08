package org.pureboard.pureboard.service;

import org.apache.commons.collections4.CollectionUtils;
import org.pureboard.pureboard.dashboard.Card;
import org.pureboard.pureboard.dashboard.Dashboard;
import org.pureboard.pureboard.dashboard.ListeDashboard;
import org.pureboard.pureboard.dashboard.TypeDashboard;
import org.pureboard.pureboard.dto.CardDto;
import org.pureboard.pureboard.dto.DashboardDto;
import org.pureboard.pureboard.dto.LabelDto;
import org.pureboard.pureboard.properties.AppProperties;
import org.pureboard.pureboard.properties.DashboardProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class DashboardService {

    private final AppProperties appProperties;
    private final ListeDashboard listeDashboard;
    private final CardService cardService;

    public DashboardService(AppProperties appProperties, CardService cardService) {
        this.appProperties = appProperties;
        this.cardService = cardService;
        listeDashboard = construireListeDashboard();
    }

    private ListeDashboard construireListeDashboard() {
        var listeDashboard = new ListeDashboard();
        if (appProperties == null) {
            throw new IllegalArgumentException("AppProperties cannot be null");
        }
        if (CollectionUtils.isNotEmpty(appProperties.getDashboards())) {
            int id = 1;
            listeDashboard.setDashboards(new ArrayList<>());
            for (var dashboard : appProperties.getDashboards()) {
                var dashboardDto = new Dashboard();
                dashboardDto.setId("dashboard" + id);
                dashboardDto.setNom(dashboard.getTitre());
                if (Objects.equals(dashboard.getType(), "card")) {
                    dashboardDto.setType(TypeDashboard.CARD);
                    //ajouterCard(dashboardDto, dashboard);
                    dashboardDto.setDashboardProperties(dashboard);
                } else {
                    throw new IllegalArgumentException("Type dashboard invalide: " + dashboard.getType());
                }

                listeDashboard.getDashboards().add(dashboardDto);
                id++;
            }
        }
        return listeDashboard;
    }

    private void ajouterCard(Dashboard dashboardDto, DashboardProperties dashboard) {

    }

    public ListeDashboard listeDashboard() {
        return listeDashboard;
    }

    public List<DashboardDto> getListeDashboardDto() {
        var liste = new ArrayList<DashboardDto>();
        for (var dashboard : listeDashboard.getDashboards()) {
            var dto = new DashboardDto();
            dto.setId(dashboard.getId());
            dto.setTitre(dashboard.getNom());
            liste.add(dto);
        }
        return liste;
    }

    public List<LabelDto> getListeCard(String idDashboard) {
        if (idDashboard == null || listeDashboard.getDashboards() == null) {
            return Collections.emptyList();
        }
        var dashboardOpt = listeDashboard.getDashboards().stream()
                .filter(x -> Objects.equals(x.getId(), idDashboard))
                .findAny();
        if (dashboardOpt.isEmpty()) {
            return Collections.emptyList();
        } else {
            var dashboard = dashboardOpt.get();
            var listeCard = calculListeCard(dashboard, dashboard.getDashboardProperties());
            dashboard.setCards(listeCard);
            return listeCard.stream().map(x -> new LabelDto(x.getId(), x.getTitre())).toList();
        }
    }

    private List<Card> calculListeCard(Dashboard dashboard, DashboardProperties dashboardProperties) {
        List<Card> liste = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dashboardProperties.getListeCard())) {
            for (var card : dashboardProperties.getListeCard()) {
                var liste2 = cardService.getListCards(card);
                liste.addAll(liste2);
            }
        }
        return liste;
    }

    public CardDto getCard(String idDashboard, String idCard) {
        var dashboardOpt = listeDashboard.getDashboards().stream()
                .filter(x -> Objects.equals(x.getId(), idDashboard))
                .findAny();
        if (dashboardOpt.isEmpty()) {
            return null;
        } else {
            var dashboard = dashboardOpt.get();
            if (dashboard.getCards() != null) {
                var card = dashboard.getCards().stream().filter(x -> Objects.equals(x.getId(), idCard)).findAny();
                if (card.isPresent()) {
                    return cardService.getCard(dashboard, card.get(), idCard);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
