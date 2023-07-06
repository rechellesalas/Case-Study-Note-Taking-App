import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class NotesFileMgmt {
    final static String NOTEPATH = "./notes/";
    final static String FILEEXTENSION = ".txt";


    public static String addNotesToPath(String noteName) {
        return NOTEPATH + noteName + FILEEXTENSION;
    }

    public static void checkFile(String noteName) {
        try {
            File noteFile = new File(addNotesToPath(noteName));
            File fileDir = new File(NOTEPATH);
            if (!fileDir.exists())
                fileDir.mkdirs();
            else{
                if (!noteFile.exists()) {
                    noteFile.createNewFile();
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void writeData(String noteData, String noteName) {
        try {
            FileWriter fileWriter = new FileWriter(addNotesToPath(noteName));
            fileWriter.write(noteData);
            fileWriter.close();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static String readData(String noteName) {
        String noteText = "";
        try {
            File noteFile = new File(addNotesToPath(noteName));
            Scanner fileScanner = new Scanner(noteFile);
            if (fileScanner.hasNextLine()) {
                String parsedText = "";
                while (fileScanner.hasNextLine()) {
                    parsedText = parsedText + fileScanner.nextLine(); // This is working, to append text for every loop, but I suggest to use StringBuilder
                }
                noteText = parsedText;
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return noteText;
    }
}
