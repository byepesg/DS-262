package model;

import util.MusicConstants;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Chord {
    private final Note root;
    private final Set<Note> notes;
    private final String quality; // Ej: "Mayor", "Menor7"

    public Chord(Note root, int[] intervals, String quality) {
        this.root = root;
        this.quality = quality;
        Set<Note> tempNotes = new HashSet<>();
        for (int interval : intervals) {
            tempNotes.add(root.transpose(interval));
        }
        this.notes = Collections.unmodifiableSet(tempNotes);
    }

    public static Chord major(Note root) {
        return new Chord(root, MusicConstants.CHORD_MAJOR, "Mayor");
    }

    public static Chord minor(Note root) {
        return new Chord(root, MusicConstants.CHORD_MINOR, "Menor");
    }

    public static Chord major7(Note root) {
        return new Chord(root, MusicConstants.CHORD_MAJ7, "Mayor 7");
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Note getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.getName() + " " + quality + " " + notes;
    }
}