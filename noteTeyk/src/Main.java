import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends NotesFileMgmt {
    final static Pattern invalidChars = Pattern.compile("[\\\\/:*?\"<>|]");
    static JFrame notesFrame;
    static JMenuBar notesMenuBar;
    static JTextArea notesTextArea;
    static JTextArea notesTextTitle;
    static JList<String> savedNotesList;
    static DefaultListModel<String> savedNotes;
    static String selectedNoteTitle;

    public static void initializeUI() {
        notesFrame = new JFrame("Note-taking Application");
        notesFrame.setSize(900, 600);
        notesFrame.setLayout(new BorderLayout());
        notesFrame.setLocationRelativeTo(null);

        //a. Buttons Initialization
        JButton newButton = new JButton("New");
        newButton.setBackground(Color.white);
        newButton.setFocusable(false);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(Color.white);
        saveButton.setFocusable(false);
        JButton openButton = new JButton("Open");
        openButton.setBackground(Color.white);
        openButton.setFocusable(false);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.white);
        deleteButton.setFocusable(false);

        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                if (newButton == event.getSource()) {
                    String newNoteName;
                    System.out.println("New Button is Pressed!"); //Debugging
                    while (true) {
                        newNoteName = JOptionPane.showInputDialog(notesFrame, "Enter a New Name for the New Note");
                        Matcher nameMatcher = invalidChars.matcher(newNoteName);
                        if (nameMatcher.find()) {
                            JOptionPane.showMessageDialog(notesFrame, "Invalid File Name!");
                        } else {
                            break;
                        }
                    }

                    if (ifFileExists(newNoteName)) {
                        JOptionPane.showMessageDialog(notesFrame, "File created successfully!");
                        savedNotes.addElement(newNoteName);
                        notesTextArea.setText("");
                        notesTextTitle.setText(newNoteName);
                    } else {
                        int confirmReplace = JOptionPane.showConfirmDialog(notesFrame, "Would you like to replace the file?", "Replace?", JOptionPane.YES_NO_OPTION);
                        if (confirmReplace == JOptionPane.YES_OPTION) {
                            if (onlyCreateFile(newNoteName)) {
                                JOptionPane.showMessageDialog(notesFrame, "File created successfully!");
                                if (savedNotes.contains(newNoteName.toLowerCase())) {
                                    savedNotes.removeElement(newNoteName);
                                    savedNotes.addElement(newNoteName);
                                    notesTextArea.setText("");
                                } else {
                                    savedNotes.addElement(newNoteName);
                                    notesTextArea.setText("");
                                    notesTextTitle.setText(newNoteName);
                                }
                            }
                        }
                    }

                }

                if (saveButton == event.getSource()) {
                    System.out.println("Save Button is Pressed!"); //Debugging
                    String noteContents = String.valueOf(notesTextArea.getText());
                    saveNotesToFile(noteContents, notesTextTitle.getText());

                    int savedNotesSize = savedNotes.getSize();
                    boolean flag = false;
                    for (int i = 0; i < savedNotesSize; i++) {
                        if (savedNotes.elementAt(i).equalsIgnoreCase(notesTextTitle.getText())) {
                            flag = false;
                            break;
                        } else {
                            flag = true;
                        }
                    }
                    if (flag){
                        savedNotes.addElement(notesTextTitle.getText());
                    }

                }
                if (openButton == event.getSource()) {
                    System.out.println("Open Button is Pressed!"); //Debugging
                    notesTextTitle.setText(selectedNoteTitle);
                    notesTextArea.setText(readData(selectedNoteTitle));
                }
                if (deleteButton == event.getSource()) {
                    int answer = JOptionPane.showConfirmDialog(notesFrame, "Are you sure you want to delete " + selectedNoteTitle + "?", "Delete File?", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        deleteNoteFile(selectedNoteTitle);
                        savedNotes.removeElement(selectedNoteTitle);
                    }
                }
            }
        };

        newButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);
        openButton.addActionListener(buttonListener);
        deleteButton.addActionListener(buttonListener);

        //b. Text Area Initialization
        notesTextArea = new JTextArea();
        notesTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        notesTextArea.setBorder(new LineBorder(Color.WHITE, 15));
        notesTextTitle = new JTextArea();
        notesTextTitle.setFont(new Font("Arial", Font.BOLD, 20));
        JPanel notesAreaPane = new JPanel();
        notesAreaPane.setLayout(new BorderLayout());
        notesAreaPane.add(notesTextTitle, BorderLayout.NORTH);
        notesAreaPane.add(notesTextArea, BorderLayout.CENTER);


        //c. Menu Bar Initialization
        notesMenuBar = new JMenuBar();
        notesMenuBar.add(newButton);
        notesMenuBar.add(saveButton);
        notesMenuBar.add(openButton);
        notesMenuBar.add(deleteButton);

        //d. Saved Notes Panel and Components Initialization
        JPanel savedNotesPanel = new JPanel();
        savedNotesPanel.setLayout(new BorderLayout());
        savedNotesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel savedNotesLabel = new JLabel("Saved Notes");
        savedNotesLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); //Padding

        //Saved Notes
        savedNotes = new DefaultListModel<>();
        savedNotes = refreshNotesLists();

        //The List
        savedNotesList = new JList<>(savedNotes);
        savedNotesList.setLayoutOrientation(JList.VERTICAL);
        savedNotesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savedNotesList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        savedNotesList.setFont(new Font("Arial", Font.PLAIN, 15));
        savedNotesList.setFixedCellHeight(25);
        savedNotesList.setFixedCellWidth(90);
        JScrollPane savedNotesScrollPane = new JScrollPane(savedNotesList);
        savedNotesPanel.add(savedNotesLabel, BorderLayout.NORTH);
        savedNotesPanel.add(savedNotesScrollPane, BorderLayout.CENTER);
        ListSelectionListener noteListSelection = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listEvent) {
                if (!listEvent.getValueIsAdjusting()) {
                    selectedNoteTitle = savedNotesList.getSelectedValue();
                }
            }
        };
        savedNotesList.addListSelectionListener(noteListSelection);

        //e. Split Panel
        JSplitPane notesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        notesPanel.setLeftComponent(savedNotesPanel);
        notesPanel.setRightComponent(notesAreaPane);
        notesPanel.setDividerLocation(250);

        //f. Visibility Initialization
        notesFrame.add(notesPanel, BorderLayout.CENTER);
        notesFrame.add(notesMenuBar, BorderLayout.NORTH);
        notesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notesFrame.setVisible(true);
    }

    public static void main(String[] args) {
        initializeUI();

    }


    private static void saveNotesToFile(String notes, String title) {
        writeDataToNotes(notes, title);
        String noteContents = readData(title);
    }
}
