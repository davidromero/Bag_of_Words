package app;

import java.io.*;

class Parser {

    private ParserLog logger;
    private String filePath;
    private ParserResults results;

    Parser(ParserLog logger, ParserResults results, String filePath) {
        this.logger = logger;
        this.filePath = filePath;
        this.results = results;
    }

    void run(){
        logger.pLog("\nChecking file \n");
        File f = checkFile();
        if (f != null){
            readFile();
        }
    }

    private boolean checkLine(String s){
        s = s.trim();
        if (!s.equals("")){
            if (!s.contains("|")){
                results.errorFound("Pipe not found, ignored");
            }
            else {
                String left, right;
                int pipePos = s.indexOf('|');
                left = s.substring(0, pipePos).trim();
                right = s.substring(pipePos + 1, s.length()).trim();
                if (left.isEmpty() | right.isEmpty()){
                    results.errorFound(s + " is not valid, ignored");
                }
                else {
                    if (right.split(" ").length > 1){
                        results.errorFound(s + " has more than 1 tag, ignored");
                    }
                    else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void useLine(String s){
        if (checkLine(s)){
            String left, tag;
            int pipePos = s.indexOf('|');
            left = s.substring(0, pipePos).trim();
            tag = s.substring(pipePos + 1, s.length()).trim();
            String[] wordVector = left.split(" ");
            for (String word : wordVector) {
                word = removePunctuation(word);
                if (!word.isEmpty()){
                    results.correctPair(word, tag);
                    results.wordFound(word);
                }
            }
            results.tagFound(tag);
        }
    }

    static String removePunctuation(String s) {
        StringBuilder res = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if(Character.isLetterOrDigit(c))
                res.append(c);
        }
        return res.toString();
    }

    private void readFile(){
        InputStream ins = null;
        Reader r = null;
        BufferedReader br = null;
        try {
            String s;
            ins = new FileInputStream(filePath);
            r = new InputStreamReader(ins, "UTF-8");
            br = new BufferedReader(r);
            while ((s = br.readLine()) != null) {
                logger.readLine(s);
                useLine(s.toLowerCase());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert br != null;
                br.close();
                r.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File checkFile(){
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory()) {
            if (f.getName().toLowerCase().endsWith(".txt")){
                return f;
            }
        }
        logger.pError("File does not exist");
        return null;
    }

    public interface ParserResults{
        void correctPair(String word, String tag);

        void tagFound(String tag);

        void wordFound(String word);

        void errorFound(String message);
    }

    public interface ParserLog {
        void pLog(String logMessage);

        void pError(String errorMessage);

        void readLine(String line);
    }

}
