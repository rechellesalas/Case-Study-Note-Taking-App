import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends NotesFileMgmt {
    static JFrame notesFrame;
    static JMenuBar notesMenuBar;
    static JTextArea notesTextArea;
    static JTextArea notesTextTitle;
    static JList<String> savedNotesList;
    static DefaultListModel<String> savedNotes;
    static String selectedNoteTitle;
    static int textFontSize = 12;

    public static void initializeUI() {
        notesFrame = new JFrame("Mini Note-taking Application");
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

        JButton fontButton = new JButton("Font");
        fontButton.setBackground(Color.white);
        fontButton.setFocusable(false);

        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                 // New Button Action
                if (newButton == event.getSource()) {
                    try {
                        String newNoteName;
                        System.out.println("New Button is Pressed!"); // Debugging

                        boolean validFileName = false;
                        while (!validFileName) {
                            newNoteName = JOptionPane.showInputDialog(notesFrame, "Enter a New Name for the New Note");
                            if (newNoteName != null && !newNoteName.trim().isEmpty()) {
                                if (checkIfNameValid(newNoteName)) {
                                    validFileName = true;
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
                                                notesTextTitle.setText(newNoteName);
                                                int savedNotesSize = savedNotes.getSize();
                                                boolean existsFlag = false;
                                                for (int i = 0; i < savedNotesSize; i++) {
                                                    if (savedNotes.elementAt(i).equalsIgnoreCase(newNoteName)) {
                                                        existsFlag = false;
                                                        break;
                                                    } else {
                                                        existsFlag = true;
                                                    }
                                                }
                                                if (existsFlag) {
                                                    savedNotes.addElement(newNoteName);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(notesFrame, "Invalid File Name!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(notesFrame, "Enter a File Name!");
                                // User canceled or provided an empty file name
                                break;
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(notesFrame, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                //Save Button Action
                if (saveButton == event.getSource()) {
                    String noteContents = String.valueOf(notesTextArea.getText());
                    if (checkIfNameValid(notesTextTitle.getText())) {
                        int savedNotesSize = savedNotes.getSize();
                        if (checkIfNameInList(notesTextTitle.getText(), savedNotesSize)) {
                            savedNotes.addElement(notesTextTitle.getText());
                        }
                        JOptionPane.showMessageDialog(notesFrame, "File saved successfully!");
                        writeContentToFile(noteContents, notesTextTitle.getText());
                    } else {
                        JOptionPane.showMessageDialog(notesFrame, "Invalid Title Name To Save for Filename");
                    }
                }

                //Open Button Action
                if (openButton == event.getSource()) {
                    notesTextTitle.setText(selectedNoteTitle);
                    notesTextArea.setText(readNoteContent(selectedNoteTitle));
                }

                 // Delete Button Action
                if (deleteButton == event.getSource()) {
                    try {
                        if (selectedNoteTitle == null) {
                            throw new IllegalArgumentException("No file selected.");
                        }

                        int answer = JOptionPane.showConfirmDialog(notesFrame, "Are you sure you want to delete " + selectedNoteTitle + "?", "Delete File?", JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            deleteNoteFile(selectedNoteTitle);
                            savedNotes.removeElement(selectedNoteTitle);

                            // Clear the UI components
                            notesTextArea.setText("");
                            notesTextTitle.setText("");
                        }
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(notesFrame, "No file selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                //Font Button
                if (fontButton == event.getSource()) {
                    String changeFontSize = JOptionPane.showInputDialog(notesFrame, "Enter font size", "Set Font Size", JOptionPane.QUESTION_MESSAGE);
                    textFontSize = Integer.parseInt(changeFontSize);
                    notesTextArea.setFont(new Font(Font.DIALOG, Font.PLAIN, textFontSize));
                    notesFrame.revalidate();
                }
            }
        };

        newButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);
        openButton.addActionListener(buttonListener);
        deleteButton.addActionListener(buttonListener);
        fontButton.addActionListener(buttonListener);

        //b. Text Area Initialization
        notesTextArea = new JTextArea();
        notesTextArea.setFont(new Font("Arial", Font.PLAIN, textFontSize));
        notesTextArea.setLineWrap(true);
        notesTextTitle = new JTextArea();
        notesTextTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JScrollPane noteAreaScroll = new JScrollPane(notesTextArea);
        noteAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noteAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        noteAreaScroll.setBorder(new LineBorder(Color.WHITE, 15));

        TitledBorder noteTitleBorder;
        Border lioneBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        noteTitleBorder = BorderFactory.createTitledBorder(lioneBorder, "File name");
        noteTitleBorder.setTitlePosition(TitledBorder.TOP);
        notesTextTitle.setBorder(noteTitleBorder);

        JPanel notesAreaPane = new JPanel();
        notesAreaPane.setLayout(new BorderLayout());
        notesAreaPane.add(notesTextTitle, BorderLayout.NORTH);
        notesAreaPane.add(noteAreaScroll, BorderLayout.CENTER);


        //c. Menu Bar Initialization
        notesMenuBar = new JMenuBar();
        notesMenuBar.add(newButton);
        notesMenuBar.add(saveButton);
        notesMenuBar.add(openButton);
        notesMenuBar.add(deleteButton);
        notesMenuBar.add(fontButton);

        //d. Saved Notes Panel and Components Initialization
        JPanel savedNotesPanel = new JPanel();
        savedNotesPanel.setLayout(new BorderLayout());
        savedNotesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel savedNotesLabel = new JLabel("Saved Notes");
        savedNotesLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); //Padding

        //d.a. Saved Notes
        savedNotes = new DefaultListModel<>();
        savedNotes = refreshNotesLists();

        //d.b. The List
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

        ListSelectionListener noteListSelection = listEvent -> {
            if (!listEvent.getValueIsAdjusting()) {
                selectedNoteTitle = savedNotesList.getSelectedValue();
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

    private static boolean checkIfNameValid(String name) {
        Pattern invalidChars = Pattern.compile("[\\\\/:*?\"<>|]");
        Matcher nameMatcher = invalidChars.matcher(name);
        boolean nameFlag = true;
        try {
            if (nameMatcher.find()) {
                nameFlag = false;
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return nameFlag;
    }

    private static boolean checkIfNameInList(String targetName, int listSize) {
        boolean existsFlag = true;

        for (int i = 0; i < listSize; i++) {
            if (savedNotes.elementAt(i).equalsIgnoreCase(targetName)) {
                existsFlag = false;
                break;
            }
        }
        return existsFlag;
    }
}
