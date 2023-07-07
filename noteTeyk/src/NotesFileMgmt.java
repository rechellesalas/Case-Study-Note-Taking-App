import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NotesFileMgmt {
    final static String NOTEPATH = "./notes/";
    final static String FILEEXTENSION = ".txt";


    public static String addNotesToPath(String noteName) {

        return NOTEPATH + noteName + FILEEXTENSION;
    }

    public static boolean ifFileExists(String newNoteFile) {
        File noteFile;
        boolean doesExists = true;
        try {
            noteFile = new File(addNotesToPath(newNoteFile));
            File noteDir = new File(NOTEPATH);

            //Creates directory for storing the notes if it does not exist.
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
        boolean didCreate = true;
        try{
            noteFile = new File(addNotesToPath(createNewNoteFile));
            noteFile.delete();
            didCreate = noteFile.createNewFile();
        }catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        return didCreate;
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

    public static String readData(String selectedNoteName) {
        StringBuilder mySB = new StringBuilder();
        try{
            File myFile = new File(NOTEPATH+selectedNoteName+FILEEXTENSION);
            Scanner myScannerReader = new Scanner(myFile);
            if (myScannerReader.hasNextLine())
                mySB.append(myScannerReader.nextLine());
            while(myScannerReader.hasNextLine()){
                mySB.append("\n");
                mySB.append(myScannerReader.nextLine());
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return String.valueOf(mySB);
    }



//    public static String readData(String selectedNoteName) {
//        String noteText = "";
//        try {
//            File noteFile = new File(addNotesToPath(selectedNoteName));
//            Scanner fileScanner = new Scanner(noteFile);
//            if (fileScanner.hasNextLine()) {
//                String parsedText = "";
//                while (fileScanner.hasNextLine()) {
//                    parsedText = parsedText + fileScanner.nextLine(); // This is working, to append text for every loop, but I suggest to use StringBuilder
//                }
//                noteText = parsedText;
//            }
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//        }
//        return noteText;
//    }

    public static String deleteNoteFile(String noteFileName) {

        try {
            File noteFile = new File(addNotesToPath(noteFileName));
            if (noteFile.delete()) {
                System.out.println(noteFile.getName() + "Deleted Successfully!");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return "Deleted Successfully";
    }

    public static DefaultListModel<String> refreshNotesLists(){
        DefaultListModel<String> fileNames = null;
        try{
            File noteDir = new File(NOTEPATH);
            fileNames = new DefaultListModel<>();
            File[] noteFiles = noteDir.listFiles();
            int noteFilesSize = noteFiles.length;
            for ( int i = 0; i < noteFilesSize; i++) {
                String resultNames= noteFiles[i].getName().split("\\.")[0].split("\\(")[0];
                fileNames.addElement(resultNames);
            }
        }catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        return fileNames;
    }
}
