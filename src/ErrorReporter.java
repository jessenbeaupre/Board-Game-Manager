import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class ErrorReporter{

    //creates a constant path to the errorlog file
    private static final File errorFile = new File("errorLog.txt");

    static <T extends Exception>void log(T t){
        //uses a generic for any type of exception and prints them to a file for later review
        try {
            PrintWriter pWriter = new PrintWriter(new FileWriter(errorFile, true));
            Date date = new Date();
            date.getTime();
            pWriter.append(date.toString()).append(": ").append(t.toString()).append("\n");
            pWriter.flush();
            pWriter.close();

        }catch (Exception ignored){

        }
    }

}
