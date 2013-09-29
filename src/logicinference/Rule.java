package logicinference;

/**
 * Правило (структурная часть базы знаний)
 */
public class Rule {
    
    /** Целевой предикат */
    private Predicate inference;
    
    /** Исходные предикаты */
    private Predicate[] causes;

    /**
     * Инициализирует целевой предикат и исходные предикаты заданными зачениями
     * @param _inference целевой предикат
     * @param _causes исходные предикаты
     */
    public Rule(Predicate _inference, Predicate[] _causes) {
        inference = _inference;
        causes = _causes;
    }

    public Predicate getInference() {
        return inference;
    }
    
    public Predicate[] getCauses() {
        return causes;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(inference.toString());
        stringBuilder.append(" <- ");
        for (int i = 0; i < causes.length - 1; i++) {
            stringBuilder.append(causes[i]);
            stringBuilder.append("; ");
        }
        stringBuilder.append(causes[causes.length - 1]);
        return stringBuilder.toString();
    }
}