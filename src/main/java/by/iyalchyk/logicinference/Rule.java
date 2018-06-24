package by.iyalchyk.logicinference;

/**
 * Represents rules like L(y,x) <- K(x,y)
 */
public class Rule {

    /**
     * Target predicate
     */
    private Predicate inference;

    /**
     * Source predicates
     */
    private Predicate[] causes;

    /**
     * Initializes a rule with the given target and source predicates
     *
     * @param inference target predicate
     * @param causes    source predicates
     */
    public Rule(Predicate inference, Predicate[] causes) {
        this.inference = inference;
        this.causes = causes;
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