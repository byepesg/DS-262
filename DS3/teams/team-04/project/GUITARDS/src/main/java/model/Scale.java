package model;

import util.MusicConstants;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Scale {
    private final Note root;
    private final Set<Note> notes;
    private final String name;

    public Scale(Note root, int[] intervals, String name) {
        this.root = root;
        this.name = name;
        // Usamos un HashSet porque Note ya tiene equals y hashCode por pitchClass
        Set<Note> tempNotes = new HashSet<>();
        for (int interval : intervals) {
            tempNotes.add(root.transpose(interval));
        }
        // Congelamos el Set para que sea inmutable (buena práctica)
        this.notes = Collections.unmodifiableSet(tempNotes);
    }

    // Factory Method para facilitar el uso
    public static Scale major(Note root) {
        return new Scale(root, MusicConstants.SCALE_MAJOR, "Mayor");
    }

    public static Scale minorNatural(Note root) {
        return new Scale(root, MusicConstants.SCALE_MINOR_NATURAL, "Menor Natural");
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Note getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.getName() + " " + name + " " + notes;
    }
}