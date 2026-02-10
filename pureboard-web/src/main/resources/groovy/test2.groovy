import org.pureboard.dashboard.Card
import org.pureboard.dashboard.TypeCard
import org.pureboard.dto.CardDto
import org.pureboard.dto.ContenuDto
import org.pureboard.dto.TypeContenu
import org.pureboard.properties.CardProperties
import org.pureboard.utils.GroovyCards


class MonScript2 implements GroovyCards {
//    String direBonjour(String nom) {
//        return "Bonjour, $nom !"
//    }

    @Override
    List<Card> getListCard(CardProperties cardProperties) {
        card = new Card();
        card.id = "1";
        card.type = TypeCard.MAVEN;
        card.setTitre("groovy")
        return [card];
    }

    @Override
    void getCard(Card card, CardDto cardDto) {
        contenu = new ContenuDto();
        contenu.setType(TypeContenu.TEXTE);
        contenu.texte = "abc";
        cardDto.contenu = contenu;
    }
}