import org.pureboard.utils.GroovyCards


class MonScript2 implements GroovyCards {
//    String direBonjour(String nom) {
//        return "Bonjour, $nom !"
//    }

    @Override
    List<String> listeCard() {
        return ["aaa"]
    }

    @Override
    void getCard(String param) {
        print("card groovy "+param)
    }
}