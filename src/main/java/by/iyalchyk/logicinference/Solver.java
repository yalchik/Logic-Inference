package by.iyalchyk.logicinference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * It performs logical inference in the given knowledge base
 */
public class Solver {

    private KnowledgeBase knowledgeBase;

    /**
     * It contains both facts derived from the given KB and the calculated facts
     */
    private List<Predicate> solverFacts;

    /**
     * Initializes a solver with the given knowledge base
     *
     * @param knowledgeBase knowledge base
     */
    public Solver(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    /**
     * Provides the core functionality to find answers on the given question
     *
     * @param questionString questions string like Name(?,?)
     * @return list with predicates satisfying the given question
     */
    public String ask(String questionString) {
        Predicate questionPredicate = KnowledgeBase.parseFact(questionString);
        if (questionPredicate == null) {
            throw new IllegalArgumentException("Cannot interpret the question");
        }
        return ask(questionPredicate);
    }

    private String ask(Predicate question) {
        solverFacts = new ArrayList<>(knowledgeBase.getFacts()); // copy all known facts from the knowledge base
        List<Predicate> answerPredicates = searchPredicate(question);
        filterByFixedArguments(answerPredicates, question.getAttributes());
        solverFacts = null; // null the solver facts so there will be no mess next time we run the solver
        return answerPredicates.toString();
    }

    /**
     * Filters given predicates list by the fixed arguments (specified in the question).
     * If a predicate does not satisfy the question arguments, it removes them from the solver cache.
     *
     * @param predicates         list of predicates being filtered out
     * @param questionAttributes attributes specified in the question
     */
    private void filterByFixedArguments(List<Predicate> predicates, String[] questionAttributes) {
        for (int i = 0; i < predicates.size(); i++) {
            for (int j = 0; j < questionAttributes.length; j++) {
                if (!questionAttributes[j].equals("?") &&
                        !questionAttributes[j].equals(predicates.get(i).getAttributes()[j])) {
                    predicates.remove(i--);
                    break;
                }
            }
        }
    }

    private List<Predicate> searchPredicate(Predicate quest) {
        solverFacts.addAll(searchRules(quest)); // add facts calculated from the rules
        return searchFactsByName(quest);
    }

    /**
     * Searches existing facts by the predicate name specified in the question
     *
     * @param question question in a predicate form
     * @return list of predicates, which are candidates for the answer
     */
    private List<Predicate> searchFactsByName(Predicate question) {
        List<Predicate> facts = new ArrayList<>();
        for (Predicate predicate : solverFacts) {
            if (question.getName().equals(predicate.getName())) {
                facts.add(predicate);
            }
        }
        return facts;
    }

    /**
     * Calculates new facts based on the rules and searches matching predicates on them
     *
     * @param quest template predicate
     * @return list of answers
     */
    private List<Predicate> searchRules(Predicate quest) {
        // facts - new facts being calculated in the method
        List<Predicate> facts = new ArrayList<>();
        // tables - list of tables for source predicates for each rule
        List<Table> tables = new ArrayList<>();
        for (Rule rule : knowledgeBase.getRules()) {
            if (quest.getName().equals(rule.getInference().getName())) {
                /*
                 * Try to find new facts from this rule:
                 * For each source predicate - build a table consisting of all possible facts for it.
                 */
                for (Predicate cause : rule.getCauses()) {
                    // build a temporary template predicate for the source predicate
                    Predicate tempQuest = new Predicate(cause.getName(), cause.getAttributes().length);
                    // recursively look for the facts for the source predicate and build the table
                    tables.add(new Table(searchPredicate(tempQuest), cause));
                }

                // Now we have to choose the facts matching the attributes. Take the very first table as a basis.
                Table resultTable = tables.get(0);
                // if there are many tables, join them (the same as the JOIN operation in SQL)
                for (int i = 1; i < tables.size(); i++) {
                    resultTable = resultTable.join(tables.get(i));
                }
                // look for the tables projection on the attributes specified in the target predicate
                resultTable = resultTable.projection(Arrays.asList(rule.getInference().getAttributes()));
                // transform the result table into a predicates list and add them into the answer
                facts.addAll(resultTable.toPredicates(rule.getInference().getName()));
            }
        }
        return facts;
    }
}
