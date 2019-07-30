package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Bag extends HashMap<String, Integer> {

    private HashMap<String, Integer> universe;
    private ArrayList<String> vocabulary;
    final int smoother = 1;

    Bag() {
        this.universe = new HashMap<>();
        this.vocabulary = new ArrayList<>();
    }

    @Override
    public Integer put(String key, Integer value) {
        return super.put(key, value);
    }

    @Override
    public Integer get(Object key) {
        if (!this.containsKey(key)){
            this.put((String) key, smoother);
        }
        return super.get(key);
    }

    void foundTag(String tag){
        Integer i = universe.get(tag);
        if (i == null){
            universe.put(tag, 1);
        }
        else {
            i++;
            universe.put(tag, i);
        }
    }

    void foundWord(String word){
        if (!vocabulary.contains(word)){
            vocabulary.add(word);
        }
    }

    public ArrayList<String> getVocabulary() {
        return vocabulary;
    }

    private String getTagFromPair(String key){
        String[] cleanWord = key.split("_");
        return cleanWord[1];
    }

    Set<String> getUniverseKeySet(){
        return universe.keySet();
    }

    int getUniverseCountFor(String key){
        return universe.get(key);
    }

    int getUniverseCountForPair(String pair){
        return universe.get(getTagFromPair(pair));
    }

    int getVocabularySize(){
        return vocabulary.size();
    }

    int getCategoryCount(){
        return universe.size();
    }

    int getUniverseSize(){
        int sum = 0;
        for (String key : universe.keySet()){
            sum += getUniverseCountFor(key);
        }
        return sum;
    }

    void destroyUniverse(){
        this.clear();
        this.universe.clear();
        this.vocabulary.clear();
    }

}
