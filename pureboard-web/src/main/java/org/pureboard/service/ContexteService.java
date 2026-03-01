package org.pureboard.service;

import org.springframework.stereotype.Service;

@Service
public class ContexteService {

    private final AnalysePomService analysePomService;

    private final RechercheRepertoireService rechercheRepertoireService;

    private final BitbucketService bitbucketService;

    private final JiraService jiraService;


    public ContexteService(AnalysePomService analysePomService, RechercheRepertoireService rechercheRepertoireService, BitbucketService bitbucketService, JiraService jiraService) {
        this.analysePomService = analysePomService;
        this.rechercheRepertoireService = rechercheRepertoireService;
        this.bitbucketService = bitbucketService;
        this.jiraService = jiraService;
    }

    public AnalysePomService getAnalysePomService() {
        return analysePomService;
    }

    public RechercheRepertoireService getRechercheRepertoireService() {
        return rechercheRepertoireService;
    }

    public BitbucketService getBitbucketService() {
        return bitbucketService;
    }

    public JiraService getJiraService() {
        return jiraService;
    }
}
