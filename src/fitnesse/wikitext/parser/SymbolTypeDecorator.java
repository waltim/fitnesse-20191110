package fitnesse.wikitext.parser;

public abstract class SymbolTypeDecorator extends SymbolType implements Translation{
    protected SymbolType baseSymbolType;

    public SymbolTypeDecorator(String symbolTypeName, SymbolType baseSymbolType) {
        super(symbolTypeName);
        this.baseSymbolType = baseSymbolType;
        this.wikiRule(baseSymbolType.getWikiRule());
        baseSymbolType.getWikiMatchers().forEach((m) -> {
            this.wikiMatcher(m);
        });
        this.htmlTranslation(this);
    }

    public abstract SymbolType isApplicable(Translator translator);
}

