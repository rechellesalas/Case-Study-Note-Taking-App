import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends NotesFileMgmt {

    static JFrame notesFrame;
    static JMenuBar notesMenuBar;
    static JTextArea notesTextArea;
    static JList<String> savedNotesList;
    static DefaultListModel<String> savedNotes;
    static String selectedNoteTitle;

    public static void initializeUI() {
        notesFrame = new JFrame("Note-taking Application");
        notesFrame.setSize(900, 600);
        notesFrame.setLayout(new BorderLayout());
        notesFrame.setLocationRelativeTo(null);

        //Buttons Initialization
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
                    System.out.println("New Button is Pressed!"); //Debugging
                    String newNoteName = JOptionPane.showInputDialog(notesFrame, "Enter a New Name for the New Note");
                    if (ifFileExists(newNoteName)) {
                        JOptionPane.showMessageDialog(notesFrame, "File created successfully!");
                        savedNotes.addElement(newNoteName);
                    } else {
                        int confirmReplace = JOptionPane.showConfirmDialog(notesFrame, "Would you like to replace the file?", "Replace?", JOptionPane.YES_NO_OPTION);
                        if (confirmReplace == JOptionPane.YES_OPTION){
                            if(onlyCreateFile(newNoteName)){
                                JOptionPane.showMessageDialog(notesFrame, "File created successfully!");
                                if(savedNotes.contains(newNoteName.toLowerCase())){
                                    savedNotes.removeElement(newNoteName);
                                    savedNotes.addElement(newNoteName);
                                }else{
                                    savedNotes.addElement(newNoteName);
                                }
                            }
                        }
                    }

                }

                if (saveButton == event.getSource()) {
                    System.out.println("Save Button is Pressed!"); //Debugging
                    String noteContents = String.valueOf(notesTextArea.getText());
                    saveNotesToFile(noteContents, selectedNoteTitle);
                }
                if (openButton == event.getSource()) {
                    System.out.println("Open Button is Pressed!"); //Debugging
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

        //Text Area Initialization
        notesTextArea = new JTextArea();
        notesTextArea.setText("This is a text");
        notesTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        notesTextArea.setBorder(new LineBorder(Color.WHITE, 15));
        JPanel notesAreaPane = new JPanel();
        notesAreaPane.setLayout(new BorderLayout());
        notesAreaPane.add(notesTextArea, BorderLayout.CENTER);


        //Menu Bar Initialization
        notesMenuBar = new JMenuBar();
        notesMenuBar.add(newButton);
        notesMenuBar.add(saveButton);
        notesMenuBar.add(openButton);
        notesMenuBar.add(deleteButton);

        //Saved Notes Panel and Components Initialization
        JPanel savedNotesPanel = new JPanel();
        savedNotesPanel.setLayout(new BorderLayout());
        savedNotesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel savedNotesLabel = new JLabel("Saved Notes");
        savedNotesLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); //Padding

        savedNotes = new DefaultListModel<>();
        savedNotes = refreshNotesLists();

        //Notes List
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

//        Split Panel
        JSplitPane notesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        notesPanel.setLeftComponent(savedNotesPanel);
        notesPanel.setRightComponent(notesAreaPane);
        notesPanel.setDividerLocation(250);

        //Visibility Initialization
        notesFrame.add(notesPanel, BorderLayout.CENTER);
        notesFrame.add(notesMenuBar, BorderLayout.NORTH);
        notesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        notesFrame.setVisible(true);
    }

    public static void main(String[] args) {
        initializeUI();

    }


    private static void saveNotesToFile(String notes, String title) {
        writeData(notes, title);
        String noteContents = readData(title);
    }
}