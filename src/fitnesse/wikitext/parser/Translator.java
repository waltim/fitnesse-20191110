package fitnesse.wikitext.parser;

public abstract class Translator {

    private SourcePage currentPage;
    protected abstract Translation getTranslation(SymbolType symbolType);

    public SourcePage getPage() { return currentPage; }

    protected Translator(SourcePage currentPage) {
        this.currentPage = currentPage;
    }
    
    public String translateTree(Symbol syntaxTree) {
        StringBuilder result = new StringBuilder();
        syntaxTree.getChildren().forEach((symbol) -> {
            result.append(translate(symbol));
        });
        return result.toString();
    }

    public String translate(Symbol symbol) {
        if (getTranslation(symbol.getType()) != null) {
            return getTranslation(symbol.getType()).toTarget(this, symbol);
        }
        else {
            StringBuilder result = new StringBuilder(symbol.getContent());
            symbol.getChildren().forEach((child) -> {
                result.append(translate(child));
            });
            return result.toString();
        }
    }

    public String formatMessage(String message) {
        return translate(new Symbol(SymbolType.Meta).add(message));
    }
}
