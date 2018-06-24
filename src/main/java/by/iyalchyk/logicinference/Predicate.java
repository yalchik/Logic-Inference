package by.iyalchyk.logicinference;

/**
 * Represents facts like X(a,b,c)
 * or templates like X(?,?) or X(a,?)
 */
public class Predicate {

    private final String name;

    private final String[] attributes;

    /**
     * Initializes a predicate template with the given name and specific number of default attributes
     *
     * @param name               predicate name
     * @param numberOfAttributes number of attributes of the predicate
     */
    public Predicate(String name, int numberOfAttributes) {
        this.name = name;
        attributes = new String[numberOfAttributes];
        for (int i = 0; i < numberOfAttributes; i++) {
            attributes[i] = "?";
        }
    }

    /**
     * Initializes a predicate template with the given name and attributes
     *
     * @param name       predicate name
     * @param attributes predicate attributes
     */
    public Predicate(String name, String[] attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append("(");
        for (int i = 0; i < attributes.length - 1; i++) {
            stringBuilder.append(attributes[i]);
            stringBuilder.append(",");
        }
        stringBuilder.append(attributes[attributes.length - 1]);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public String[] getAttributes() {
        return attributes;
    }
}