package app;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

class MLMagic implements Parser.ParserResults {

    private MLLog logger;
    private Bag bagOfWords;

    MLMagic(MLLog logger, Bag bagOfWords) {
        this.logger = logger;
        this.bagOfWords = bagOfWords;
    }

    void summary(){
        SortedSet<String> sortedSet = new TreeSet<>(bagOfWords.keySet());
        for (String pair : sortedSet) {
            String[] pairS = pair.split("_");
            logger.mlLog(String.format("P(%s | %s) = %s/%s", pairS[0], pairS[1], bagOfWords.get(pair),
                    bagOfWords.getUniverseCountForPair(pair) + bagOfWords.getCategoryCount() * bagOfWords.smoother));
        }
        logger.mlLog("- - - - - - - - -");
        logger.mlLog("Vocabulary size: " + bagOfWords.getVocabularySize());
        logger.mlLog("Category count: " + bagOfWords.getCategoryCount());

        for (String key : bagOfWords.getUniverseKeySet()){
            logger.mlLog(String.format("P(%s) = %s/%s", key, bagOfWords.getUniverseCountFor(key), bagOfWords.getUniverseSize()));
        }
    }

    void classify(String query){
        logger.mlLog(String.format("\nAnalysing \"%s\"\n", query));
        SortedSet<String> classSet = new TreeSet<>(bagOfWords.getUniverseKeySet());
        ArrayList<Double> classSums = makeCompleteLogSummation(query.split(" "), classSet);
//        ArrayList<Double> classSums = makeLogSummation(query.split(" "), classSet);
        classSums = exponientiate(classSums);
        logger.mlLog(String.format("\nNormalize \"%s\"", query));
        String maxClass = normalizeAndGetMax(classSums, classSet);
        for (String word : query.split(" ")){
            if (!word.isEmpty()){
                correctPair(word, maxClass);
                wordFound(word);
                tagFound(maxClass);
            }
        }
        logger.mlLog("- - - - - - - - -");
        logger.mlLog("Vocabulary size: " + bagOfWords.getVocabularySize());
        logger.mlLog("Category count: " + bagOfWords.getCategoryCount());

        for (String key : bagOfWords.getUniverseKeySet()){
            logger.mlLog(String.format("P(%s) = %s/%s", key, bagOfWords.getUniverseCountFor(key), bagOfWords.getUniverseSize()));
        }
    }

    private ArrayList<Double> makeLogSummation(String[] queryWords, SortedSet<String> classSet){
        ArrayList<Double> classSums = new ArrayList<>();
        for (String classifier : classSet){
            double logSum = calcPriorProbabilityLog(classifier);
            logger.mlLog(String.format("ln( P(%s) ) = %s", classifier, logSum));
            for (String word : queryWords){
                word = Parser.removePunctuation(word).trim();
                logSum += calcProbabilityLog(word, classifier);
                logger.mlLog(String.format("+ ln( P(%s | %s) ) = %s", word, classifier, logSum));
            }
            classSums.add(logSum);
        }
        return classSums;
    }

    private ArrayList<Double> makeCompleteLogSummation(String[] queryWords, SortedSet<String> classSet){
        ArrayList<Double> classSums = new ArrayList<>();
        ArrayList<String> vocabulary = bagOfWords.getVocabulary();
        for (String classifier : classSet){
            double logSum = calcPriorProbabilityLog(classifier);
            logger.mlLog(String.format("ln( P(%s) ) = %s", classifier, logSum));
            for (String word: vocabulary){
                boolean wordIsInQuery = false;
                for (String queryWord : queryWords){
                    queryWord = Parser.removePunctuation(queryWord).trim();
                    if (queryWord.equals(word)){
                        wordIsInQuery = true;
                        break;
                    }
                }
                if (wordIsInQuery){
                    logSum += calcProbabilityLog(word, classifier);
                    logger.mlLog(String.format("+ ln( P(%s | %s) ) = %s", word, classifier, logSum));
                }
                else {
                    logSum += calcNegProbabilityLog(word, classifier);
                    logger.mlLog(String.format("+ ln( P(%s | ~%s) ) = %s", word, classifier, logSum));
                }
            }
            classSums.add(logSum);
        }
        return classSums;
    }

    private double calcProbabilityLog(String word, String classifier){
        double numerator = bagOfWords.get(word + "_" + classifier);
        double denominator = (double) bagOfWords.getUniverseCountFor(classifier) + bagOfWords.getCategoryCount() * bagOfWords.smoother;
        return Math.log(numerator / denominator);
    }

    private double calcNegProbabilityLog(String word, String classifier){
        double numerator = bagOfWords.get(word + "_" + classifier);
        double denominator = (double) bagOfWords.getUniverseCountFor(classifier) + bagOfWords.getCategoryCount() * bagOfWords.smoother;
        return Math.log(1 - (numerator / denominator));
    }

    private double calcPriorProbabilityLog(String classifier){
        double numerator = bagOfWords.getUniverseCountFor(classifier);
        double denominator = bagOfWords.getUniverseSize();
        return Math.log(numerator / denominator);
    }

    private String normalizeAndGetMax(ArrayList<Double> classSums, SortedSet<String> classSet){
        double summation = summation(classSums);
        double maxVal = 0;
        String maxClass = "";
        int i = 0;
        for (String classifier : classSet){
            logger.mlLog(String.format("%s / %s", classSums.get(i), summation));
            double probability = classSums.get(i) / summation;
            logger.mlLog(String.format("P(%s | Π Wi ) = %s", classifier, probability));
            if (maxVal < probability){
                maxClass = classifier;
                maxVal = probability;
            }
            i++;
        }
        logger.mlLog(String.format("%s is the most likely outcome with  %.2f%%", maxClass, maxVal * 100));
        return maxClass;
    }

    private double summation(ArrayList<Double> doubleArrayList){
        double sum = 0;
        for (Double val : doubleArrayList){
            sum += val;
        }
        return sum;
    }

    private ArrayList<Double> exponientiate(ArrayList<Double> logArrayList){
        int i = 0;
        for (Double ln : logArrayList){
            logArrayList.set(i, Math.exp(ln));
            i++;
        }
        return logArrayList;
    }

    @Override
    public void correctPair(String word, String tag) {
        String key = word + "_" + tag;
        Integer i = bagOfWords.get(key);
        if (i == null){
            bagOfWords.put(key, 1);
        }
        else {
            i++;
            bagOfWords.put(key, i);
        }
    }

    @Override
    public void tagFound(String tag) {
        bagOfWords.foundTag(tag);
    }

    @Override
    public void wordFound(String word) {
        bagOfWords.foundWord(word);
    }

    @Override
    public void errorFound(String message) {
        logger.mlError(message);
    }

    public interface MLLog {
        void mlLog(String logMessage);

        void mlError(String errorMessage);
    }

}
