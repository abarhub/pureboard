package org.pureboard.service;

import org.springframework.stereotype.Service;

@Service
public class ContexteService {

    private final AnalysePomService analysePomService;

    private final RechercheRepertoireService rechercheRepertoireService;

    public ContexteService(AnalysePomService analysePomService, RechercheRepertoireService rechercheRepertoireService) {
        this.analysePomService = analysePomService;
        this.rechercheRepertoireService = rechercheRepertoireService;
    }

    public AnalysePomService getAnalysePomService() {
        return analysePomService;
    }

    public RechercheRepertoireService getRechercheRepertoireService() {
        return rechercheRepertoireService;
    }
}
