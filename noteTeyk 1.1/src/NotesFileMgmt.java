import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class NotesFileMgmt {
    final static String NOTESTORAGEPATH = "./notes/";
    final static String FILEEXTENSION = ".txt";


    public static String addNotesToPath(String noteName) {

        return NOTESTORAGEPATH + noteName + FILEEXTENSION;
    }

    public static boolean ifFileExists(String newNoteFile) {
        File noteFile;
        boolean doesExists = true;
        try {
            noteFile = new File(addNotesToPath(newNoteFile));
            File noteDir = new File(NOTESTORAGEPATH);

            //Creates directory for note storage if it does not exist.
            if (!noteDir.exists())
                noteDir.mkdirs();

            //Checks if file already exists, if not then create file.
            if (noteFile.exists()) {
                doesExists = false;
            } else {
                doesExists = noteFile.createNewFile();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return doesExists;
    }

    public static boolean onlyCreateFile(String createNewNoteFile){
        File noteFile;
        boolean didItCreateFlag = true;
        try{
            noteFile = new File(addNotesToPath(createNewNoteFile));
            noteFile.delete();
            didItCreateFlag = noteFile.createNewFile();
        }catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        return didItCreateFlag;
    }

    public static void writeContentToFile(String noteContents, String noteName) {
        try {
            FileWriter fileWriter = new FileWriter(addNotesToPath(noteName));
            fileWriter.write(noteContents);
            fileWriter.close();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static String readNoteContent(String selectedNoteName) {
        StringBuilder noteContentSB = new StringBuilder();
        try{
            File myFile = new File(NOTESTORAGEPATH +selectedNoteName+FILEEXTENSION);
            Scanner fileReader = new Scanner(myFile);
            if (fileReader.hasNextLine())
                noteContentSB.append(fileReader.nextLine());
            while(fileReader.hasNextLine()){
                noteContentSB.append("\n");
                noteContentSB.append(fileReader.nextLine());
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return String.valueOf(noteContentSB);
    }


    public static void deleteNoteFile(String noteFileName) {

        try {
            File noteFile = new File(addNotesToPath(noteFileName));
            if (noteFile.delete()) {
                System.out.println(noteFile.getName() + "Deleted Successfully!");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    //This refreshes the notes list.
    public static DefaultListModel<String> refreshNotesLists(){
        DefaultListModel<String> fileNames = null;
        try{
            File noteDir = new File(NOTESTORAGEPATH);
            fileNames = new DefaultListModel<>();
            File[] noteFiles = noteDir.listFiles();
            for (File noteFile : noteFiles) {
                String resultNames = noteFile.getName().split("\\.txt")[0].split("\\(")[0];
                fileNames.addElement(resultNames);
            }
        }catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        return fileNames;
    }
}
