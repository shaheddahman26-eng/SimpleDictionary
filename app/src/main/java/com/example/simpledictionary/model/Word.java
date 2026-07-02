package com.example.simpledictionary.model;

import java.io.Serializable;

/**
 * Simple data model representing a dictionary entry.
 * Implements Serializable so lists of Word objects can be
 * saved into a Bundle during onSaveInstanceState().
 */
public class Word implements Serializable {

    private String word;
    private String meaning;

    public Word(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
