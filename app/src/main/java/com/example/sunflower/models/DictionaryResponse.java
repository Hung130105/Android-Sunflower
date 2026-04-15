package com.example.sunflower.models;

import java.util.List;

public class DictionaryResponse {
    private String status;
    private String source;
    private DictionaryData data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public DictionaryData getData() { return data; }
    public void setData(DictionaryData data) { this.data = data; }

    public static class DictionaryData {
        private String word;
        private String phonetic;
        private String audio;
        private List<Meaning> meanings;

        public String getWord() { return word; }
        public void setWord(String word) { this.word = word; }
        public String getPhonetic() { return phonetic; }
        public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
        public String getAudio() { return audio; }
        public void setAudio(String audio) { this.audio = audio; }
        public List<Meaning> getMeanings() { return meanings; }
        public void setMeanings(List<Meaning> meanings) { this.meanings = meanings; }
    }

    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;

        public String getPartOfSpeech() { return partOfSpeech; }
        public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
        public List<Definition> getDefinitions() { return definitions; }
        public void setDefinitions(List<Definition> definitions) { this.definitions = definitions; }
    }

    public static class Definition {
        private String definition;
        private String example;
        private List<String> synonyms;
        private List<String> antonyms;

        public String getDefinition() { return definition; }
        public void setDefinition(String definition) { this.definition = definition; }
        public String getExample() { return example; }
        public void setExample(String example) { this.example = example; }
        public List<String> getSynonyms() { return synonyms; }
        public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }
        public List<String> getAntonyms() { return antonyms; }
        public void setAntonyms(List<String> antonyms) { this.antonyms = antonyms; }
    }
}