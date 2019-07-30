package app;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class GenView extends JFrame implements GenController.LogEntry {

    private JButton fileChooserB;
    private JTextField filePathTF;
    private JButton fileReadB;
    private JTextArea logsTA;
    private JTextArea textTA;
    private JTextField inputTF;
    private JButton classifyB;
    private JMenuItem clearItem;

    GenView() {
        initComponents();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(800, 600);
    }

    private void initComponents(){
        JPanel parentPanel = new JPanel();
        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();

        parentPanel.setLayout(new BorderLayout());
        northPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new GridLayout());
        southPanel.setLayout(new BorderLayout());

        fileChooserB = new JButton("Choose training file");
        filePathTF = new JTextField("src/main/resources/video.txt");
        fileReadB = new JButton("Run");
        logsTA = new JTextArea();
        logsTA.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logsTA);
        textTA = new JTextArea();
        textTA.setEditable(false);
        JScrollPane scrollText = new JScrollPane(textTA);
        inputTF = new JTextField();
        classifyB = new JButton("Classify");

        DefaultCaret caret = (DefaultCaret)logsTA.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);

        northPanel.add(BorderLayout.WEST, fileChooserB);
        northPanel.add(BorderLayout.CENTER, filePathTF);
        northPanel.add(BorderLayout.EAST, fileReadB);
        centerPanel.add(scrollLog);
        centerPanel.add(scrollText);
        southPanel.add(BorderLayout.CENTER, inputTF);
        southPanel.add(BorderLayout.EAST, classifyB);

        parentPanel.add(BorderLayout.NORTH, northPanel);
        parentPanel.add(BorderLayout.CENTER, centerPanel);
        parentPanel.add(BorderLayout.SOUTH, southPanel);

        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        clearItem = new JMenuItem("Clear previous trainings");
        menu.add(clearItem);
        menubar.add(menu);
        this.setJMenuBar(menubar);

        this.add(parentPanel);
    }

    String getFilePath() {
        return filePathTF.getText();
    }

    void setFilePath(String filePath) {
        this.filePathTF.setText(filePath);
    }

    String getQuery(){
        return inputTF.getText();
    }

    void clearQuery(){
        inputTF.setText("");
    }

    void clearText(){
        textTA.setText("");
    }

    void addButtonListener(GenController.TrainerListener buttonListener) {
        this.fileReadB.addActionListener(buttonListener);
    }

    void addChooserListener(GenController.ChooserListener chooserListener) {
        this.fileChooserB.addActionListener(chooserListener);
    }

    void addClassify(GenController.ClassifierListener classifierListener){
        this.classifyB.addActionListener(classifierListener);
    }

    void addMenuListener(GenController.SettingsListener settingsListener) {
        this.clearItem.addActionListener(settingsListener);
    }

    @Override
    public void error(String errorMessage){
        errorMessage = " - ERROR: ".concat(errorMessage);
        log(errorMessage);
    }

    @Override
    public void textLog(String line) {
        textTA.append(line + "\n");
    }

    @Override
    public void log(String logMessage) {
        logsTA.append(logMessage + "\n");
    }
}
