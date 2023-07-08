import javax.swing.*;
import javax.swing.border.*;
import java.awt.Color;
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
        JButton saveButton = new JButton("Save");
        JButton openButton = new JButton("Open");
        JButton deleteButton = new JButton("Delete");
        JButton fontButton = new JButton("Font Size");
        newButton.setBackground(Color.white);
        newButton.setBackground(Color.white);
        newButton.setFocusable(false);
        saveButton.setBackground(Color.white);
        saveButton.setFocusable(false);
        openButton.setBackground(Color.white);
        openButton.setFocusable(false);
        deleteButton.setBackground(Color.white);
        deleteButton.setFocusable(false);
        fontButton.setBackground(Color.white);
        fontButton.setFocusable(false);

        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                // New Button Action
                if (newButton == event.getSource()) {
                    try {
                        String newNoteName;

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
                                        notesFrame.setTitle("Note: " + newNoteName);
                                        notesFrame.revalidate();

                                    } else {
                                        int confirmReplace = JOptionPane.showConfirmDialog(notesFrame, "Would you like to replace the file?", "Replace?", JOptionPane.YES_NO_OPTION);
                                        if (confirmReplace == JOptionPane.YES_OPTION) {
                                            if (onlyCreateFile(newNoteName)) {
                                                JOptionPane.showMessageDialog(notesFrame, "File created successfully!");
                                                notesTextArea.setText("");
                                                notesTextTitle.setText(newNoteName);
                                                notesFrame.setTitle("Note: " + newNoteName);
                                                notesFrame.revalidate();
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


                // Save Button Action
                if (saveButton == event.getSource()) {
                    try {
                        String noteContents = notesTextArea.getText();
                        String noteName = notesTextTitle.getText();

                        if (noteName.isEmpty()) {
                            // Handle the case when no file is clicked
                            JOptionPane.showMessageDialog(notesFrame, "No file clicked!");
                        } else if (checkIfNameValid(noteName)) {
                            if (savedNotes.contains(noteName)) {
                                // Save the notes to the existing file
                                writeContentToFile(noteContents, noteName);
                                JOptionPane.showMessageDialog(notesFrame, "File saved successfully!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(notesFrame, "Invalid Title Name!");
                        }
                    } catch (NullPointerException e) {
                        // Handle any other potential NullPointerException
                        JOptionPane.showMessageDialog(notesFrame, "An error occurred!");
                    }
                }

                //Open Button Action
                if (openButton == event.getSource()) {
                    System.out.println("Open Button is Pressed!"); //Debugging
                    notesTextTitle.setText(selectedNoteTitle);
                    notesTextArea.setText(readNoteContent(selectedNoteTitle));
                    notesFrame.setTitle("Note: " + selectedNoteTitle);
                    notesFrame.revalidate();
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
                            notesFrame.setTitle("Mini Note-taking Application");
                            notesFrame.revalidate();
                        }
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(notesFrame, "No file selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }


                //Font Button
                // Font Button
                if (fontButton == event.getSource()) {
                    String changeFontSize = JOptionPane.showInputDialog(notesFrame, "Enter font size", "Set Font Size", JOptionPane.QUESTION_MESSAGE);

                    try {
                        int textFontSize = Integer.parseInt(changeFontSize);
                        notesTextArea.setFont(new Font(Font.DIALOG, Font.PLAIN, textFontSize));
                        notesFrame.revalidate();
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(notesFrame, "Invalid font size. Please enter a valid integer.");
                    }
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
        notesTextTitle.setEditable(false);

        JScrollPane noteAreaScroll = new JScrollPane(notesTextArea);
        noteAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noteAreaScroll.setBorder(new LineBorder(Color.WHITE, 15));

        TitledBorder noteTitleBorder;
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        noteTitleBorder = BorderFactory.createTitledBorder(lineBorder, "Note name");
        noteTitleBorder.setTitlePosition(TitledBorder.TOP);
        notesTextTitle.setBorder(noteTitleBorder);
        notesTextTitle.setEditable(false);

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

        //Saved Notes (will serve to store note names)
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
        savedNotesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //Adding both the label and list in the panel
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
            } else {
                nameFlag = true;
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