package logicinference;

/**
 * Предикат (структурная часть базы знаний)
 */
public class Predicate {
    
    /** Имя предиката */
    private String name;
    
    /** Атрибуты предиката */
    private String[] attributes;    

    /**
     * Инициализирует шаблон предиката с заданным именем и количеством атрибутов
     * @param _name имя предиката
     * @param numberOfAttributes количество атрибутов
     */
    public Predicate(String _name, int numberOfAttributes) {
        name = _name;
        attributes = new String[numberOfAttributes];
        for (int i = 0; i < numberOfAttributes; i++) {
            attributes[i] = "?";
        }
    }
    
    /**
     * Инициализирует имя предиката и его атрибуты заданными значениями
     * @param _name имя предиката
     * @param _attributes атрибуты предиката
     */
    public Predicate(String _name, String[] _attributes) {
        name = _name;
        attributes = _attributes;
    }
    
    public String getName() {
        return name;
    }
    
    public String[] getAttributes() {
        return attributes;
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
}