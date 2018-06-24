package by.iyalchyk.logicinference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

public class LogicInference {

    /**
     * Entry point of the application
     *
     * @param args path to knowledge base and the question
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(String.format("Error: %s\nFormat: %s\nExample: %s",
                    "Wrong input",
                    "java -jar [jar_path] [kb_path] [question]",
                    "java -jar target/LogicInference-1.0-SNAPSHOT.jar examples/test_knowledge_base1.txt M(?,?)"
            ));
            System.exit(1);
        }

        String filename = args[0];
        String question = args[1];

        try {
            String answer = findAnswer(filename, question);
            if (answer != null) {
                System.out.println("Answer: " + answer);
            } else {
                System.out.println("Error: cannot find the answer");
                System.exit(2);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(3);
        } catch (IOException e) {
            System.out.println("Error: network connection issues");
            System.exit(4);
        }
    }

    private static String findAnswer(String filename, String question) throws IOException, ParseException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        KnowledgeBase kb = new KnowledgeBase(lines);
        String answer = new Solver(kb).ask(question);
        return answer;
    }
}
