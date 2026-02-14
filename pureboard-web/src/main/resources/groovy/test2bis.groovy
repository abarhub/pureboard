import org.pureboard.dashboard.Card
import org.pureboard.dashboard.TypeCard
import org.pureboard.dto.CardDto
import org.pureboard.dto.ContenuDto
import org.pureboard.dto.TypeContenu
import org.pureboard.dto.TableauDto
import org.pureboard.properties.CardProperties
import org.pureboard.utils.GroovyCards

class MonScript2 implements GroovyCards {
//    String direBonjour(String nom) {
//        return "Bonjour, $nom !"
//    }

    @Override
    List<Card> getListCard(CardProperties cardProperties) {
        var card = new Card();
        card.id = "1";
        card.type = TypeCard.MAVEN;
        card.setTitre("groovy");
        var card2 = new Card();
        card2.id = "2";
        card2.type = TypeCard.MAVEN;
        card2.setTitre("groovy2");
        var card3 = new Card();
        card3.id = "3";
        card3.type = TypeCard.MAVEN;
        card3.setTitre("groovy3");
        var card4 = new Card();
        card4.id = "4";
        card4.type = TypeCard.MAVEN;
        card4.setTitre("groovy4");
        var card5 = new Card();
        card5.id = "5";
        card5.type = TypeCard.MAVEN;
        card5.setTitre("groovy5");
        var card6 = new Card();
        card6.id = "6";
        card6.type = TypeCard.MAVEN;
        card6.setTitre("groovy6");
        var card7 = new Card();
        card7.id = "7";
        card7.type = TypeCard.MAVEN;
        card7.setTitre("groovy7");
        return [card, card2, card3, card4, card5, card6, card7];
    }

    @Override
    void getCard(Card card, CardDto cardDto) {
        if (card.id == '1') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.TEXTE);
            contenu.texte = "abc";
            cardDto.contenu = contenu;
        } else if(card.id == '2') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.TEXTE);
            contenu.texte = "abc2";
            cardDto.contenu = contenu;
        } else if(card.id == '3') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.TABLEAU);
            var tableau=new TableauDto();
            tableau.headers=["aaa","bbb"];
            tableau.lignes=[
                    [getTexte("aaa1"),getTexte('aaa2')],
                    [getTexte("bbb1"),getTexte('bbb2')],
                    [getTexte("ccc1"),getTexte('ccc2')]
            ];
            contenu.tableau = tableau;
            cardDto.contenu = contenu;
        } else if(card.id == '4') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.ICONE);
            contenu.classe = "pi pi-check";
            cardDto.contenu = contenu;
        } else if(card.id == '5') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.COMPOSE);
            contenu.listeContenu = [getTexte("aaa1"),getTexte('aaa2')];
            cardDto.contenu = contenu;
        } else if(card.id == '6') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.LIEN);
            contenu.lien = "https://www.google.fr";
            contenu.texte = "Google";
            cardDto.contenu = contenu;
        } else if(card.id == '7') {
            var contenu = new ContenuDto();
            contenu.setType(TypeContenu.BOUTON);
            contenu.texte = "Ok";
            cardDto.contenu = contenu;
        }

    }

    ContenuDto getTexte(s){
        var contenu = new ContenuDto();
        contenu.setType(TypeContenu.TEXTE);
        contenu.texte = s;
        return contenu;
    }
}