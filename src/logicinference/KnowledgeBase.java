package logicinference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KnowledgeBase {
    
    // произвольное количество пробелов
    public static final Pattern spacesPattern = Pattern.compile("([ ]*)");
    // одна и более заглавных латинских букв
    public static final Pattern namesPattern = Pattern.compile("([A-Z]+)");
    // одна и более строчных латинских букв
    public static final Pattern parameterPattern = Pattern.compile("(([a-z]+)|\\?)");
    // одна строка из строчных латинских букв либо много строк через запятую
    public static final Pattern parametersListPattern = Pattern.compile("(" + parameterPattern + "(," + parameterPattern + ")*)");
    // в скобках одна строка из строчных латинских букв либо много строк через запятую
    public static final Pattern parametersPattern = Pattern.compile("\\(" + parametersListPattern + "\\)");
    // предикат, например A(abc) или ABC(q,w,e)
    public static final Pattern predicatePattern = Pattern.compile("(" + namesPattern + parametersPattern + ")");
    // стрелка '<-', обозначающая правило
    public static final Pattern arrowPattern = Pattern.compile("\\<-");
    // правило, например M(xx,y) <- L(y,xx); P(xx); Q(xx,y,z)
    public static final Pattern rulePattern = Pattern.compile(
            "(" + predicatePattern + spacesPattern + arrowPattern + spacesPattern
            + predicatePattern + "(;" + spacesPattern + predicatePattern + ")*)");
    
    
    /** Факты базы знаний */
    private List<Predicate> facts = new ArrayList<>();
    
    /** Правила базы знаний */
    private List<Rule> rules = new ArrayList<>();
    
    public List<Predicate> getFacts() {
        return facts;
    }
    
    public List<Rule> getRules() {
        return rules;
    }
    
    /**
     * Анализирует входную строку на наличие фактов или правил,
     * вызывает для них соответствующие функции разбора
     * и добавляет в соответствующие им списки разобранные структуры
     * @param line текстовая строка
     */
    public void parse(String line) {
        // Ищем символы "<-"
        Matcher matcher = arrowPattern.matcher(line);
        // Если нашли, то в строке должно быть правило
        // иначе там должен быть факт
        if (matcher.find()) {
            Rule rule = parseRule(line);
            if (rule != null) {
                rules.add(rule);
            }
            else {
                System.out.println("Error: line \"" + line + "\" hasn't been parsed");
            }
        }
        else {
            Predicate fact = parseFact(line);
            if (fact != null) {
                facts.add(fact);
            }
            else {
                System.out.println("Error: line \"" + line + "\" hasn't been parsed");
            }
        }
    }
    
    /**
     * Преобразует текстовую строку, содержащую факт
     * в структуру данных "Предикат"
     * @param line текстовая строка
     * @return полученный факт
     */
    public static Predicate parseFact(String line) {
        Predicate predicate = null;
        Matcher matcher = predicatePattern.matcher(line);
        if (matcher.find()) {
            matcher = namesPattern.matcher(line);
            matcher.find();
            String name = matcher.group();           
            matcher = parametersListPattern.matcher(line);
            matcher.find();
            String[] attributes = matcher.group().split(",");
            predicate = new Predicate(name, attributes);
        }
        return predicate;
    }
    
    /**
     * Преобразует текстовую строку, содержащую правило
     * в структуру данных "Правило"
     * @param line текстовая строка
     * @return полученное правило
     */
    public static Rule parseRule(String line) {
        Rule rule = null;
        Matcher matcher = rulePattern.matcher(line);
        if (matcher.find()) {
            matcher = predicatePattern.matcher(line);
            matcher.find();
            String inferenceString = matcher.group();
            Predicate inference = parseFact(inferenceString);
            List<Predicate> causes = new ArrayList<>();
            while (matcher.find()) {
                causes.add(parseFact(matcher.group()));
            }
            rule = new Rule(inference, causes.toArray(new Predicate[0]));
        }
        return rule;
    }
    
    /**
     * Выводит на консоль содержимое базы знаний
     */
    public void print() {
        System.out.println("Facts: ");
        System.out.println(facts.toString());
        System.out.println("Rules: ");
        System.out.println(rules.toString());
    }
}