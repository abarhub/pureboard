import org.pureboard.dashboard.Card
import org.pureboard.dashboard.TypeCard
import org.pureboard.dto.CardDto
import org.pureboard.dto.ContenuDto
import org.pureboard.dto.TableauDto
import org.pureboard.dto.TypeContenu
import org.pureboard.properties.CardProperties
import org.pureboard.utils.GroovyCards
import org.pureboard.service.ContexteService
import org.pureboard.properties.ChampsProperties
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.pureboard.vo.Projet

import java.util.concurrent.atomic.AtomicLong

class MonScript3 implements GroovyCards {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonScript3.class);

    private ContexteService contexteService;

    private AtomicLong counter = new AtomicLong(1);

    void setContexteService(ContexteService contexteService) {
        this.contexteService = contexteService;
    }

    @Override
    List<Card> getListCard(CardProperties cardProperties) {

        var recherche = contexteService.getRechercheRepertoireService();
        var listeRepertoires = cardProperties.parametres.get("repertoires");
        var liste2 = new ArrayList<Path>();
        var listeProjet2=new ArrayList<Projet>();
        if (listeRepertoires != null) {
            for (int i = 0; i < listeRepertoires.size(); i++) {
                if(listeRepertoires.get(""+i)!=null) {
                    Path p = Paths.get(listeRepertoires.get(""+i));
                    var listeProjet = recherche.findPomFiles(p, null);
                    if (listeProjet != null) {
                        listeProjet2.addAll(listeProjet);
                    }
                }


            }
        }

        List<Card> listeCards =new ArrayList<>();

        for(int i=0;i<listeProjet2.size();i++){

            Projet projet=listeProjet2.get(i);

            if (projet.getFichierPom() != null) {
                Card2 card = new Card2();
                card.setId("card" + counter.getAndIncrement());
                card.setTitre("Projet " + projet.getNom());
                card.setType(TypeCard.MAVEN);
                card.setCardProperties(cardProperties);
                card.setPomMaven(Path.of(projet.getFichierPom()));
                card.setProjet(projet);

                listeCards.add(card);
            }

        }

//        var listeProjet = recherche.findPomFiles("D:/projet/pureboard", null);



//        var card = new Card();
//        card.id = "1";
//        card.type = TypeCard.MAVEN;
//        card.setTitre("groovy");
//        var card2 = new Card();
//        card2.id = "2";
//        card2.type = TypeCard.MAVEN;
//        card2.setTitre("groovy2");
//        var card3 = new Card();
//        card3.id = "3";
//        card3.type = TypeCard.MAVEN;
//        card3.setTitre("groovy3");
//        var card4 = new Card();
//        card4.id = "4";
//        card4.type = TypeCard.MAVEN;
//        card4.setTitre("groovy4");
//        var card5 = new Card();
//        card5.id = "5";
//        card5.type = TypeCard.MAVEN;
//        card5.setTitre("groovy5");
//        var card6 = new Card();
//        card6.id = "6";
//        card6.type = TypeCard.MAVEN;
//        card6.setTitre("groovy6");
//        var card7 = new Card();
//        card7.id = "7";
//        card7.type = TypeCard.MAVEN;
//        card7.setTitre("groovy7");
//        return [card, card2, card3, card4, card5, card6, card7];

        return listeCards;
    }

    @Override
    void getCard(Card card, CardDto cardDto) {

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
                    var analysePomService = contexteService.getAnalysePomService();
                    analysePomService.analyseProjet(projet);

                    if (projet.getProjetPom() != null) {
                        var pom = projet.getProjetPom();
                        var nom = pom.getNom();
                        var contenu = new ContenuDto();
                        if (false) {
                            contenu.setType(TypeContenu.TEXTE);
                            contenu.setTexte("projet " + nom);
                        } else if (true) {
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

//        var contenu = new ContenuDto();
//        contenu.setType(TypeContenu.TEXTE);
//        contenu.texte = "abc";
//        cardDto.contenu = contenu;

//        if (card.id == '1') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.TEXTE);
//            contenu.texte = "abc";
//            cardDto.contenu = contenu;
//        } else if (card.id == '2') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.TEXTE);
//            contenu.texte = "abc2";
//            cardDto.contenu = contenu;
//        } else if (card.id == '3') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.TABLEAU);
//            var tableau = new TableauDto();
//            tableau.headers = ["aaa", "bbb"];
//            tableau.lignes = [
//                    [getTexte("aaa1"), getTexte('aaa2')],
//                    [getTexte("bbb1"), getTexte('bbb2')],
//                    [getTexte("ccc1"), getTexte('ccc2')]
//            ];
//            contenu.tableau = tableau;
//            cardDto.contenu = contenu;
//        } else if (card.id == '4') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.ICONE);
//            contenu.classe = "pi pi-check";
//            cardDto.contenu = contenu;
//        } else if (card.id == '5') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.COMPOSE);
//            contenu.listeContenu = [getTexte("aaa1"), getTexte('aaa2')];
//            cardDto.contenu = contenu;
//        } else if (card.id == '6') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.LIEN);
//            contenu.lien = "https://www.google.fr";
//            contenu.texte = "Google";
//            cardDto.contenu = contenu;
//        } else if (card.id == '7') {
//            var contenu = new ContenuDto();
//            contenu.setType(TypeContenu.BOUTON);
//            contenu.texte = "Ok";
//            cardDto.contenu = contenu;
//        }

    }

    ContenuDto getTexte(s) {
        var contenu = new ContenuDto();
        contenu.setType(TypeContenu.TEXTE);
        contenu.texte = s;
        return contenu;
    }


    private TableauDto construitTableau(List<List<String>> liste) {
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
        TableauDto tableau = new TableauDto();
        tableau.setLignes(listeContenu);
        return tableau;
    }

    private ContenuDto construitTexte(String texte) {
        ContenuDto contenu = new ContenuDto();
        contenu.setType(TypeContenu.TEXTE);
        contenu.setTexte(texte);
        return contenu;
    }

    public class Card2 extends Card {

    }

}