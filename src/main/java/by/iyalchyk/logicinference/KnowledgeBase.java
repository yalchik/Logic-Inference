package by.iyalchyk.logicinference;

import org.apache.commons.text.TextStringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class KnowledgeBase {

    // any number of spaces
    public static final Pattern SPACES_PATTERN = Pattern.compile("([ ]*)");

    // one or more upper-case latin letters
    public static final Pattern NAMES_PATTERN = Pattern.compile("([A-Z]+)");

    // one or more lower-case latin letters
    public static final Pattern PARAMETER_PATTERN = Pattern.compile("(([a-z]+)|\\?)");

    // one or more comma-separated parameters
    public static final Pattern PARAMETERS_LIST_PATTERN = Pattern.compile("(" + PARAMETER_PATTERN + "(," + PARAMETER_PATTERN + ")*)");

    // comma-separated parameters with brackets
    public static final Pattern PARAMETERS_PATTERN = Pattern.compile("\\(" + PARAMETERS_LIST_PATTERN + "\\)");

    // predicate like 'A(abc)' or 'ABC(q,w,e)'
    public static final Pattern PREDICATE_PATTERN = Pattern.compile("(" + NAMES_PATTERN + PARAMETERS_PATTERN + ")");

    // arrow '<-', it means a 'rule'
    public static final Pattern ARROW_PATTERN = Pattern.compile("<-");

    // rule like 'M(xx,y) <- L(y,xx); P(xx); Q(xx,y,z)'
    public static final Pattern RULE_PATTERN = Pattern.compile(
            "(" + PREDICATE_PATTERN + SPACES_PATTERN + ARROW_PATTERN + SPACES_PATTERN
                    + PREDICATE_PATTERN + "(;" + SPACES_PATTERN + PREDICATE_PATTERN + ")*)");


    private List<Predicate> facts = new ArrayList<>();

    private List<Rule> rules = new ArrayList<>();

    /**
     * Parses knowledge base from the given text to internal collections.
     *
     * @param kbLines list of input strings representing facts or rules.
     * @throws ParseException occurs when it cannot parse some line.
     */
    public KnowledgeBase(List<String> kbLines) throws ParseException {
        int x = 0;
        for (String line : kbLines) {
            parse(line, x++);
        }
    }

    /**
     * Converts the given string to a Predicate object
     *
     * @param line string to parse
     * @return parsed predicate or null
     */
    public static Predicate parseFact(String line) {
        Predicate predicate = null;
        Matcher matcher = PREDICATE_PATTERN.matcher(line);
        if (matcher.find()) {
            matcher = NAMES_PATTERN.matcher(line);
            matcher.find();
            String name = matcher.group();
            matcher = PARAMETERS_LIST_PATTERN.matcher(line);
            matcher.find();
            String[] attributes = matcher.group().split(",");
            predicate = new Predicate(name, attributes);
        }
        return predicate;
    }

    /**
     * Converts the given string to a Rule object
     *
     * @param line string to parse
     * @return parsed rule or null
     */
    public static Rule parseRule(String line) {
        Rule rule = null;
        Matcher matcher = RULE_PATTERN.matcher(line);
        if (matcher.find()) {
            matcher = PREDICATE_PATTERN.matcher(line);
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
     * Analyzed input line and checks whether it contains a fact or a rule,
     * parses it and adds to an appropriate collection.
     *
     * @param line   string to parse
     * @param offset line offset in the whole input data.
     * @throws ParseException occurs when it cannot parse the given line.
     */
    private void parse(String line, int offset) throws ParseException {
        Matcher matcher = ARROW_PATTERN.matcher(line);
        // If we found a '<-', there should be a rule, otherwise it is a fact.
        if (matcher.find()) {
            Rule rule = parseRule(line);
            if (rule == null) {
                throw new ParseException("Cannot parse line \"" + line + "\"", offset);
            }
            rules.add(rule);
        } else {
            Predicate fact = parseFact(line);
            if (fact == null) {
                throw new ParseException("Cannot parse line \"" + line + "\"", offset);
            }
            facts.add(fact);
        }
    }

    public List<Predicate> getFacts() {
        return facts;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        TextStringBuilder stringBuilder = new TextStringBuilder();
        stringBuilder.appendln("Facts: ");
        stringBuilder.appendln("Facts: ");
        stringBuilder.appendln(facts.toString());
        stringBuilder.appendln("Rules: ");
        stringBuilder.appendln(rules.toString());
        return stringBuilder.build();
    }
}