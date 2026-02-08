package org.pureboard.pureboard.controler;

import org.pureboard.pureboard.dto.CardDto;
import org.pureboard.pureboard.dto.DashboardDto;
import org.pureboard.pureboard.dto.LabelDto;
import org.pureboard.pureboard.service.CardService;
import org.pureboard.pureboard.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dashboard")
public class DashboardControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardControler.class);

    private final DashboardService dashboardService;

    private final CardService cardService;

    public DashboardControler(DashboardService dashboardService, CardService cardService) {
        this.dashboardService = dashboardService;
        this.cardService = cardService;
    }

    @GetMapping(path = "/liste-dashboard", produces = "application/json")
    public List<DashboardDto> listeDashboard(/*@PathVariable String groupId,
                                       @PathVariable String nomProjet,
                                       @RequestParam String commitInitial,
                                       @RequestParam String commitFinal*/) throws Exception {
        LOGGER.info("listeDashboard");
        return dashboardService.getListeDashboardDto();
    }

    @GetMapping(path = "/liste-card/{idDashboard}", produces = "application/json")
    public List<LabelDto> listeCard(@PathVariable String idDashboard) throws Exception {
        LOGGER.info("liste-card {}", idDashboard);
        return dashboardService.getListeCard(idDashboard);
    }


    @GetMapping(path = "/card/{idDashboard}/{idCard}", produces = "application/json")
    public CardDto card(@PathVariable String idDashboard, @PathVariable String idCard) throws Exception {
        LOGGER.info("card {}", idCard);
        return dashboardService.getCard(idDashboard, idCard);
    }
}
