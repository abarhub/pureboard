package org.pureboard.service;

import com.google.common.base.CharMatcher;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.pureboard.dashboard.Card;
import org.pureboard.dashboard.Dashboard;
import org.pureboard.dashboard.TypeCard;
import org.pureboard.dto.CardDto;
import org.pureboard.dto.ContenuDto;
import org.pureboard.dto.TableauDto;
import org.pureboard.dto.TypeContenu;
import org.pureboard.properties.CardProperties;
import org.pureboard.properties.ChampsProperties;
import org.pureboard.utils.GroovyCards;
import org.pureboard.vo.Projet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CardService {

    private final static AtomicLong counter = new AtomicLong(1);
    private static final Logger LOGGER = LoggerFactory.getLogger(CardService.class);
    public static final CharMatcher CHAMPS_SIMPLE = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.anyOf("._?"));

    private final AnalysePomService analysePomService;

    private final RechercheRepertoireService rechercheRepertoireService;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final ContexteService contexteService;

    public CardService(AnalysePomService analysePomService, RechercheRepertoireService rechercheRepertoireService, ContexteService contexteService) {
        this.analysePomService = analysePomService;
        this.rechercheRepertoireService = rechercheRepertoireService;
        this.contexteService = contexteService;
    }

    public List<Card> getListCards(CardProperties cardProperties) {
        List<Card> listeCards = new ArrayList<>();

        if (StringUtils.isNotBlank(cardProperties.getType())) {
            switch (cardProperties.getType()) {
                case "maven":
                    construitListeCardMaven(listeCards, cardProperties);
                    break;
                case "groovy":
                    construitListeCardGroovy(listeCards, cardProperties);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported dashboard type: " + cardProperties.getType());
            }
        } else {
            throw new IllegalArgumentException("Dashboard type cannot be null");
        }

        return listeCards;
    }

    public TableauDto getTableau(CardProperties cardProperties, Dashboard dashboard) {
        if (StringUtils.isNotBlank(cardProperties.getType())) {
            switch (cardProperties.getType()) {
                case "maven":
                    return construitTableauMaven(cardProperties, dashboard);
//                case "groovy":
//                    construitListeCardGroovy(listeCards, cardProperties);
//                    break;
                default:
                    throw new IllegalArgumentException("Unsupported dashboard type: " + cardProperties.getType());
            }
        } else {
            throw new IllegalArgumentException("Dashboard type cannot be null");
        }
    }

    private TableauDto construitTableauMaven(CardProperties cardProperties, Dashboard dashboard) {
        TableauDto res = new TableauDto();

        if (CollectionUtils.isNotEmpty(cardProperties.getRepertoire())) {
            for (String repertoire0 : cardProperties.getRepertoire()) {
                Path repertoire = Path.of(repertoire0);
                if (Files.exists(repertoire)) {
                    try {
                        List<Projet> listeProjets = rechercheRepertoireService.findPomFiles(repertoire, Collections.EMPTY_SET);
                        for (var projet : listeProjets) {
                            if (projet.getFichierPom() != null) {
//                                Card card = new Card();
//                                card.setId("card" + counter.getAndIncrement());
//                                card.setTitre("Projet " + projet.getNom());
//                                card.setType(TypeCard.MAVEN);
//                                card.setCardProperties(cardProperties);
//                                Assert.notNull(projet, "projet null");
//                                card.setPomMaven(Path.of(projet.getFichierPom()));
//                                card.setProjet(projet);
//
//                                listeCards.add(card);

                                analysePomService.analyseProjet(projet);

                                List<List<String>> liste = prepareConstruitionTableau(cardProperties, projet);

                                ajouteTableau(res, liste);

                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Erreur lors de la recherche des projets Maven pour le répertoire {}", repertoire, e);
                    }
                }
            }
        }

        return res;
    }

    private void construitListeCardGroovy(List<Card> listeCards, CardProperties cardProperties) {
        String script = cardProperties.getScript();
        try {
            GroovyClassLoader loader = new GroovyClassLoader();
            Class groovyClass = loader.parseClass(new File(script));
            Object groovyObject = groovyClass.getDeclaredConstructor().newInstance();

            if (groovyObject instanceof GroovyCards groovyCards) {
                groovyCards.setContexteService(contexteService);
                var listeCard = groovyCards.getListCard(cardProperties);
                LOGGER.info("listeCard: {}", listeCard);
                if (listeCard != null) {
                    listeCard.forEach(x -> {
                        x.setObjectGroovy(groovyCards);
                        x.setType(TypeCard.GROOVY);
                    });
                    listeCards.addAll(listeCard);
                }
            } else {
                Method m = groovyClass.getMethod("direBonjour", String.class);
                String resultat = (String) m.invoke(groovyObject, "Alice");
                LOGGER.info("resultat groovy: {}", resultat);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la construction du script Groovy : {}", script, e);
        }
    }

    private void construitListeCardMaven(List<Card> listeCards, CardProperties cardProperties) {

        if (CollectionUtils.isNotEmpty(cardProperties.getRepertoire())) {
            for (String repertoire0 : cardProperties.getRepertoire()) {
                Path repertoire = Path.of(repertoire0);
                if (Files.exists(repertoire)) {
                    try {
                        List<Projet> listeProjets = rechercheRepertoireService.findPomFiles(repertoire, Collections.EMPTY_SET);
                        for (var projet : listeProjets) {
                            if (projet.getFichierPom() != null) {
                                Card card = new Card();
                                card.setId("card" + counter.getAndIncrement());
                                card.setTitre("Projet " + projet.getNom());
                                card.setType(TypeCard.MAVEN);
                                card.setCardProperties(cardProperties);
                                Assert.notNull(projet, "projet null");
                                card.setPomMaven(Path.of(projet.getFichierPom()));
                                card.setProjet(projet);

                                listeCards.add(card);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Erreur lors de la recherche des projets Maven pour le répertoire {}", repertoire, e);
                    }
                }
            }
        }
    }

    public CardDto getCard(Dashboard dashboard, Card card, String idCard) {
        CardDto cardDto = new CardDto();
        cardDto.setId(idCard);
        if (card.getType() == TypeCard.MAVEN) {
            calculCardMaven(card, cardDto);
        } else if (card.getType() == TypeCard.GROOVY) {
            calculCardGroovy(card, cardDto);
        }
        return cardDto;
    }

    private void calculCardGroovy(Card card, CardDto cardDto) {
        if (card.getObjectGroovy() != null) {
            var groovyCards = card.getObjectGroovy();
            Instant date = Instant.now();
            LOGGER.info("appel script groovy {} ...", card.getId());
            groovyCards.getCard(card, cardDto);
            LOGGER.info("appel script groovy {} fini (duree:{})", card.getId(), Duration.between(date, Instant.now()));
        }
    }

    private void calculCardMaven(Card card, CardDto cardDto) {
        var properties = card.getCardProperties();
        var fichierPom = card.getPomMaven();
        if (fichierPom != null) {
            Path p = fichierPom;
            if (Files.exists(p)) {
                try {
                    Projet projet = card.getProjet();
                    if (projet == null) {
                        projet = new Projet();
                        projet.setFichierPom(p.toString());
                        projet.setRepertoire(p.getParent().toString());
                    }
                    analysePomService.analyseProjet(projet);

                    if (projet.getProjetPom() != null) {
                        var pom = projet.getProjetPom();
                        var nom = pom.getNom();
                        var contenu = new ContenuDto();
                        if (false) {
                            contenu.setType(TypeContenu.TEXTE);
                            contenu.setTexte("projet " + nom);
                        } else if (false) {
                            contenu.setType(TypeContenu.TABLEAU);
                            var artifactId = "";
                            var groupId = "";
                            var version = "";
                            if (pom.getArtifact() != null) {
                                if (StringUtils.isNotBlank(pom.getArtifact().artefactId())) {
                                    artifactId = pom.getArtifact().artefactId();
                                }
                                if (StringUtils.isNotBlank(pom.getArtifact().groupId())) {
                                    groupId = pom.getArtifact().groupId();
                                }
                                if (StringUtils.isNotBlank(pom.getArtifact().version())) {
                                    version = pom.getArtifact().version();
                                }
                            }
                            if (nom == null) {
                                nom = "";
                            }
                            var tab = List.of(List.of("nom", nom),
                                    List.of("groupId", groupId),
                                    List.of("artefactId", artifactId),
                                    List.of("version", version));
                            TableauDto tableau = construitTableau(tab);
                            contenu.setTableau(tableau);
                        } else {
                            contenu.setType(TypeContenu.TABLEAU);

                            List<List<String>> liste = prepareConstruitionTableau(properties, projet);

                            TableauDto tableau = construitTableau(liste);
                            contenu.setTableau(tableau);
                        }
                        cardDto.setContenu(contenu);
                    }
                } catch (Exception e) {
                    LOGGER.error("Erreur lors de l'analyse du projet Maven", e);
                }
            }
        }
    }

    private @NonNull List<List<String>> prepareConstruitionTableau(CardProperties properties, Projet projet) {
        List<List<String>> liste = new ArrayList<>();

        if (properties != null && CollectionUtils.isNotEmpty(properties.getChamps())) {


            for (ChampsProperties nomChamps : properties.getChamps()) {
                String valeur = evalueExpression(nomChamps.getExpression(), projet);
                String nomChamps2 = nomChamps.getNom();
                if (StringUtils.isBlank(nomChamps2)) {
                    nomChamps2 = nomChamps(nomChamps.getExpression());
                }
                liste.add(List.of(nomChamps2, valeur));
            }
        }
        return liste;
    }

    private TableauDto construitTableau(List<List<String>> liste) {
        TableauDto tableau = new TableauDto();
        ajouteTableau(tableau, liste);
        return tableau;
    }

    private void ajouteTableau(TableauDto tableau, List<List<String>> liste) {
        List<List<ContenuDto>> listeContenu = new ArrayList<>();
        if (liste != null) {
            for (List<String> ligne : liste) {
                List<ContenuDto> ligneContenu = new ArrayList<>();
                for (String cellule : ligne) {
                    ligneContenu.add(construitTexte(cellule));
                }
                listeContenu.add(ligneContenu);
            }
        }
        if (tableau.getLignes() == null) {
            tableau.setLignes(listeContenu);
        } else {
            tableau.getLignes().addAll(listeContenu);
        }
    }

    private ContenuDto construitTexte(String texte) {
        ContenuDto contenu = new ContenuDto();
        contenu.setType(TypeContenu.TEXTE);
        contenu.setTexte(texte);
        return contenu;
    }

    private String nomChamps(String nomChamps) {
        String nomChamps2 = "";
        if (nomChamps == null) {
            return nomChamps2;
        }
        if (CHAMPS_SIMPLE.matchesAllOf(nomChamps)) {
            int pos = StringUtils.lastIndexOf(nomChamps, '.');
            if (pos >= 0) {
                nomChamps2 = nomChamps.substring(pos + 1);
            } else {
                nomChamps2 = nomChamps;
            }
        } else {
            nomChamps2 = nomChamps;
        }
        return nomChamps2;
    }

    private String evalueExpression(String expression, Projet projet) {
        Expression exp = parser.parseExpression(expression);
        Object valeur = null;
        try {
            valeur = exp.getValue(projet);
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'évaluation de l'expression : {}", expression, e);
            return "???";
        }
        if (valeur == null) {
            return "";
        } else {
            return valeur.toString();
        }
    }
}
