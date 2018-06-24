package by.iyalchyk.logicinference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {

    /**
     * All possible attributes combinations for all facts (excluding the facts' names)
     */
    private final List<List<String>> attributes;

    /**
     * Table columns
     */
    private final List<String> parameters;

    public Table() {
        attributes = new ArrayList<>();
        parameters = new ArrayList<>();
    }

    /**
     * Initializes attributes based on facts and parameters based on the target predicate
     *
     * @param facts     list of known facts
     * @param inference target predicate
     */
    public Table(List<Predicate> facts, Predicate inference) {
        parameters = new ArrayList<>(Arrays.asList(inference.getAttributes()));
        attributes = new ArrayList<>();
        for (int i = 0; i < facts.size(); i++) {
            attributes.add(new ArrayList<>(Arrays.asList(facts.get(i).getAttributes())));
        }
    }

    /**
     * Calculates the cartesian product of the current table with the given one.
     * Similar to the operation "TIMES" in SQL
     *
     * @param table second operand
     * @return cartesian product of two tables
     */
    public Table times(Table table) {
        Table newTable = new Table();
        newTable.parameters.addAll(parameters);
        newTable.parameters.addAll(table.parameters);

        // get all attributes combinations
        for (int i = 0; i < attributes.size(); i++) {
            for (int j = 0; j < table.attributes.size(); j++) {
                List<String> _attributes = new ArrayList<>();
                // concatenate i-th row of the first table with the j-th row of the second table
                _attributes.addAll(attributes.get(i));
                _attributes.addAll(table.attributes.get(j));
                newTable.attributes.add(_attributes);
            }
        }
        return newTable;
    }

    /**
     * Calculates the join result of the current table with the given one
     *
     * @param table second operand
     * @return join result
     */
    public Table join(Table table) {
        // calculate the cartesian product
        Table newTable = times(table);
        int size = newTable.parameters.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                // if the i-th and j-th parameters (columns) match, combine them
                if (newTable.parameters.get(i).equals(newTable.parameters.get(j))) {
                    // k - row number
                    for (int k = 0; k < newTable.attributes.size(); k++) {
                        // compare [k][i]-th element with the [k][j]-th: if they are different - remove the k-th row
                        if (!newTable.attributes.get(k).get(i).equals(newTable.attributes.get(k).get(j))) {
                            newTable.attributes.remove(k--);
                        }
                    }
                }
            }
        }
        return newTable;
    }

    /**
     * Calculates the projection of the current table on the given attributes list.
     *
     * @param _parameters predicate parameters (table attributes)
     * @return projection result
     */
    public Table projection(List<String> _parameters) {
        Table newTable = new Table();
        // parameters of the projected table must always match the requested parameters on the order and content
        newTable.parameters.addAll(_parameters);
        // get column numbers of the columns which we need for the projected table
        List<Integer> parametersNumbers = new ArrayList<>();
        for (int i = 0; i < _parameters.size(); i++) {
            for (int j = 0; j < parameters.size(); j++) {
                if (_parameters.get(i).equals(parameters.get(j))) {
                    parametersNumbers.add(j);
                    break;
                }
            }
        }
        // build projected table rows
        for (int i = 0; i < attributes.size(); i++) {
            List<String> _attributes = new ArrayList<>();
            for (int j = 0; j < parametersNumbers.size(); j++) {
                // if the row is in the allowed column - add it to the projected table
                _attributes.add(attributes.get(i).get(parametersNumbers.get(j)));
            }
            newTable.attributes.add(_attributes);
        }
        return newTable;
    }

    /**
     * Converts the table into a predicates list with the given name
     *
     * @param nameOfPredicates name for the predicates
     * @return predicates list
     */
    public List<Predicate> toPredicates(String nameOfPredicates) {
        List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < attributes.size(); i++) {
            predicates.add(new Predicate(nameOfPredicates, attributes.get(i).toArray(new String[0])));
        }
        return predicates;
    }

}
