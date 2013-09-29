package logicinference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Решатель, выполняющий поисковые операции и логический вывод
 */
public class Solver {    
    
    /** База знаний (прочитанная из файла) */
    private KnowledgeBase knowledgeBase;
    
    /** Факты, полученные как из базы знаний, так и в ходе вычислений */
    private List<Predicate> solverFacts;
    
    /**
     * Инициализирует базу знаний
     * @param _knowledgeBase база знаний
     */
    public Solver(KnowledgeBase _knowledgeBase) {
        knowledgeBase = _knowledgeBase;
    }
    
    /**
     * Входная точка решателя, находит ответы на заданный вопрос
     * @param questionString вопрос в формате Name(?,?)
     * @return список предикатов, являющихся ответами на заданный вопрос
     */
    public String ask(String questionString) {
        String answerString = null;
        // копируем факты из базы знаний
        solverFacts = new ArrayList<>(knowledgeBase.getFacts());
        // парсим вопрос в предикат
        Predicate quest = KnowledgeBase.parseFact(questionString);
        // если вопрос отпарсен корректно, то можно искать ответы
        if (quest != null) {
            List<Predicate> answerPredicates = searchPredicate(quest);
            checkFixedArguments(answerPredicates, quest.getAttributes());
            answerString = answerPredicates.toString();            
        }
        else {
            System.out.println("Error: question \"" + questionString + "\"hasn't been parsed");
        }
        // обнуляем факты решателя, чтобы при следующем вопросе этому же решателю они не мешались
        solverFacts = null;
        return answerString;
    }
    
    /**
     * Удаляет найденные предикаты с неравными аргументами, соответствующими фиксированным атрибутам вопроса
     * @param predicates список предикатов
     * @param questAttributes атрибуты вопроса
     */
    private void checkFixedArguments(List<Predicate> predicates, String[] questAttributes) {
        for (int i = 0; i < predicates.size(); i++) {
            for (int j = 0; j < questAttributes.length; j++) {
                if (!questAttributes[j].equals("?") &&
                        !questAttributes[j].equals(predicates.get(i).getAttributes()[j])) {
                    predicates.remove(i--);
                    break;
                }
            }
        }
    }
    
    /**
     * По заданному шаблонному предикату находит список соответствующих предикатов
     * @param quest шаблонный предикат
     * @return список ответов
     */
    private List<Predicate> searchPredicate(Predicate quest) {
        // добавляем факты, полученные из правил
        solverFacts.addAll(searchRules(quest));
        return searchFacts(quest);
    }
    
    /**
     * По заданному шаблонному предикату находит список соответствующих предикатов
     * среди имеющихся у решателя фактов
     * @param quest шаблонный предикат
     * @return список ответов
     */
    private List<Predicate> searchFacts(Predicate quest) {
        List<Predicate> facts = new ArrayList<>();
        for (Predicate predicate : solverFacts) {
            // если имя факта совпадает с именем шаблонного предиката,
            // то добавляем данный факт в список ответов
            if (quest.getName().equals(predicate.getName())) {
                facts.add(predicate);
            }
        }
        return facts;
    }
    
    /**
     * По заданному шаблонному предикату находит список соответствующих предикатов
     * среди правил, находящихся в базе знаний
     * @param quest шаблонный предикат
     * @return список ответов
     */
    private List<Predicate> searchRules(Predicate quest) {
        // facts - полученные факты в данной функции
        List<Predicate> facts = new ArrayList<>();
        // tables - список таблиц для исходных предикатов каждого правила
        List<Table> tables = new ArrayList<>();
        for (Rule rule : knowledgeBase.getRules()) {
            // Если имя шаблонного предиката совпадает с именем целевого предиката,
            // то пробуем найти новые факты из этого правила
            if (quest.getName().equals(rule.getInference().getName())) {
                // для каждого исходного предиката формируем таблицу,
                // состоящую из всех возможных для него фактов
                for (Predicate cause : rule.getCauses()) {
                    // формируем промежуточный шаблонный предикат для исходного предиката
                    Predicate tempQuest = new Predicate(cause.getName(), cause.getAttributes().length);
                    // рекурсивно находим факты для исходного предиката и формируем таблицу                    
                    tables.add(new Table(searchPredicate(tempQuest), cause));            
                }
                
                // теперь надо отобрать подходящие по атрибутам факты
                // принимаем за основу самую первую таблицу
                Table resultTable = tables.get(0);
                // если таблиц несколько, то объединяем их (операция "JOIN")
                for (int i = 1; i < tables.size(); i++) {          
                    resultTable = resultTable.join(tables.get(i));
                }
                // находим проекцию объединения таблиц по атрибутам, заданным в целевом предикате
                resultTable = resultTable.projection(Arrays.asList(rule.getInference().getAttributes()));
                // преобразуем полученную таблицу в список предикатов и добавляем их в ответ
                facts.addAll(resultTable.toPredicates(rule.getInference().getName()));
            }
        }
        return facts;
    }
}

class Table {
    
    /** Все комбинации атрибутов фактов (факты без имени) */
    public List<List<String>> attributes;
    
    /** Список параметров (или атрибуты таблицы) */
    public List<String> parameters;
    
    /**
     * Инициализирует атрибуты и параметры пустыми списками
     */
    public Table() {
        attributes = new ArrayList<>();
        parameters = new ArrayList<>();
    }
    
    /**
     * Инициализирует атрибуты на основе фактов
     * и параметры на основе целевого предиката
     * @param facts список фактов
     * @param inference целевой предикат
     */
    public Table(List<Predicate> facts, Predicate inference) {
        parameters = new ArrayList<>(Arrays.asList(inference.getAttributes()));
        attributes = new ArrayList<>();
        for (int i = 0; i < facts.size(); i++) {
            attributes.add(new ArrayList<>(Arrays.asList(facts.get(i).getAttributes())));
        }
    }

    /**
     * Конструктор копирования
     * @param table копируемая таблица
     */
    public Table(Table table) {
        attributes = new ArrayList<>();
        parameters = new ArrayList<>(table.parameters);
        for (int i = 0; i < table.attributes.size(); i++) {
            List<String> _attributes = new ArrayList<>(table.attributes.get(i));
            attributes.add(_attributes);
        }
    }    
    
    /**
     * Выполняет декартово произведение двух таблиц (операция "TIMES")
     * и возвращает результат в новой таблице
     * @param table второй операнд
     * @return декартово произведение
     */
    public Table times(Table table) {
        Table newTable = new Table();
        // параметры конкатенируем
        newTable.parameters.addAll(parameters);
        newTable.parameters.addAll(table.parameters);
        
        // находим все комбинации атрибутов
        for (int i = 0; i < attributes.size(); i++) {
            for (int j = 0; j < table.attributes.size(); j++) {
                List<String> _attributes = new ArrayList<>();
                // конкатенируем i-ю строку первой таблицы с j-й строкой второй таблицы
                _attributes.addAll(attributes.get(i));
                _attributes.addAll(table.attributes.get(j));
                newTable.attributes.add(_attributes);
            }
        }
        return newTable;
    }
    
    /**
     * Выполняет объединение двух таблиц (операция "JOIN")
     * и возвращает результат в новой таблице
     * @param table второй операнд
     * @return объединение
     */
    public Table join(Table table) {
        // выполняем декартово произведение
        Table newTable = times(table);
        int size = newTable.parameters.size();
        // Сравниваем i-й и j-й параметры
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {                
                // Если совпадают, то соответствующие столбцы надо склеивать
                if (newTable.parameters.get(i).equals(newTable.parameters.get(j))) {
                    // Склеивание i-го и j-го столбцов
                    // k - номер строки
                    for (int k = 0; k < newTable.attributes.size(); k++) {
                        // Сравниваем [k][i]-й элемент с [k][j]-м элементом
                        // Если они не равны, то удаляем k-ю строку
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
     * Выполняет проекцию таблицы по заданным атрибутам
     * @param _parameters параметры предиката (табличные атрибуты)
     * @return проекция
     */
    public Table projection(List<String> _parameters) {
        Table newTable = new Table();
        // Параметры всегда должны совпадать с запрашиваемыми по содержанию и порядку,
        // поэтому их просто копируем
        newTable.parameters.addAll(_parameters);        
        // Определяем номера столбцов, которые надо внести в новую таблицу
        List<Integer> parametersNumbers = new ArrayList<>();
        for (int i = 0; i < _parameters.size(); i++) {
            for (int j = 0; j < parameters.size(); j++) {
                if (_parameters.get(i).equals(parameters.get(j))) {
                    parametersNumbers.add(j);
                    break;
                }
            }
        }
        // формируем строки таблицы
        for (int i = 0; i < attributes.size(); i++) {
            List<String> _attributes = new ArrayList<>();            
            for (int j = 0; j < parametersNumbers.size(); j++) {
                // если строка стоит в разрешённом столбце, то добавляем её в новую таблицу
                _attributes.add(attributes.get(i).get(parametersNumbers.get(j)));
            }
            newTable.attributes.add(_attributes);
        }
        return newTable;
    }
    
    /**
     * Конвертирует таблицу в список предикатов с указанным именем
     * @param nameOfPredicates имя предикатов
     * @return список предикатов
     */
    public List<Predicate> toPredicates(String nameOfPredicates) {
        List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < attributes.size(); i++) {
            predicates.add(new Predicate(nameOfPredicates, attributes.get(i).toArray(new String[0])));
        }
        return predicates;
    }
    
    /*
    // предназначается для тестирования
    public void print() {
        System.out.println(parameters);
        for (int i = 0; i < attributes.size(); i++) {
            System.out.println(attributes.get(i));     
        }
        System.out.println("=====");
    }
    * */
}