package logicinference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Начальный класс,
 * @author Илья
 */
public class LogicInference {

    /**
     * Entry point of the application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(correctRun("bats\\in1.txt", "M(?,w)"));
        }
        else
        if (args.length == 2) {
            System.out.println(correctRun(args[0], args[1]));
        }
        else {
            System.out.println("Input format: java -jar <program_name>.jar <filename> <question>");
            System.out.println("Example: java -jar logic.jar D:\\021701\\lois\\kb.txt M(?,?)");
        }
    }
    
    /**
     * Задаёт вопрос решателю при корректном запуске приложения
     * @param filename имя файла, содержащего базу знаний
     * @param question вопрос
     * @return ответ
     */
    public static String correctRun(String filename, String question) {
        KnowledgeBase kb = new KnowledgeBase();
        try (BufferedReader reader = new BufferedReader(
                                         new InputStreamReader(
                                             new FileInputStream(
                                                 new File(filename)), "UTF8"))) {
            // парсим базу знаний
            String buffer;
            while( ( buffer = reader.readLine() ) != null ) {
                kb.parse(buffer);
            }
            //kb.print();

            // Задаём вопрос решателю
            String answer = new Solver(kb).ask(question);
            // Если ответ получен, то выводим в консоль
            if (answer != null) {
                System.out.println("Answer: " + answer);
            }
        }
        catch (IOException e) { 
            System.out.println("Error: file hasn't been read.");
        }
        return "";
    }
}
