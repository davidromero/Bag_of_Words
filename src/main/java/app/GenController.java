package app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GenController implements MLMagic.MLLog, Parser.ParserLog {

    private LogEntry logger;
    private Bag bagOfWords;
    private MLMagic magic;
    private GenView theView;
    private GenModel theModel;

    GenController(GenView theView, GenModel theModel) {
        this.theView = theView;
        this.theModel = theModel;
        this.logger = theView;

        this.theView.addButtonListener(new TrainerListener());
        this.theView.addChooserListener(new ChooserListener());
        this.theView.addMenuListener(new SettingsListener());
        this.theView.addClassify(new ClassifierListener());
        startProgram();
    }

    private void startProgram(){
        logger.log("Starting program...");
        theModel.loadProperties();
        bagOfWords = new Bag();
        magic = new MLMagic(this, bagOfWords);
        logger.log("Waiting for training data..");
    }

    private void startParser(String filePath) {
        Parser parser = new Parser(this, magic, filePath);
        parser.run();
    }

    @Override
    public void mlLog(String logMessage) {
        logger.log(logMessage);
    }

    @Override
    public void mlError(String errorMessage) {
        logger.error(errorMessage);
    }

    @Override
    public void pLog(String logMessage) {
        logger.log(logMessage);
    }

    @Override
    public void pError(String errorMessage) {
        logger.error(errorMessage);
    }

    @Override
    public void readLine(String line) {
        logger.textLog(line);
    }

    public interface LogEntry {
        void log(String logMessage);

        void error(String errorMessage);

        void textLog(String line);
    }

    class ChooserListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String filePath = "";

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(theView);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().getPath();
                logger.log(filePath + " selected");
                theModel.setFilePath(filePath);
                theView.setFilePath(filePath);
            }
        }
    }

    class TrainerListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            theView.clearText();
            theModel.setFilePath(theView.getFilePath());
            startParser(theModel.getFilePath());
            magic.summary();
        }
    }

    class ClassifierListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String query = theView.getQuery();
            if (!query.trim().equals(""))
               magic.classify(query);
        }
    }

    class SettingsListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            logger.log("- - - - - - - - - -");
            logger.log("Clearing training data");
            bagOfWords.destroyUniverse();
            theView.clearText();
            logger.log("Cleared");
        }
    }

}