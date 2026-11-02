package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Arbol de decision binario que asigna el nivel de urgencia de una
 * solicitud. Cada nodo interno formula una pregunta de si o no y cada hoja
 * devuelve un nivel de 1 a 5.
 *
 * Clasificar sigue un unico camino raiz-hoja: O(h) preguntas, sin importar
 * cuantos nodos tenga el arbol. Las demas operaciones (contar, altura,
 * listar el protocolo) recorren todo el arbol: Theta(n).
 *
 * El protocolo por defecto es ilustrativo, inspirado en las escalas de
 * triage de cinco niveles; no es un protocolo clinico real.
 */
public class DecisionTree {

    /** Quien responde las preguntas (el usuario en consola o una prueba). */
    @FunctionalInterface
    public interface Answerer {
        boolean ask(String question);
    }

    private static final class Node {

        final String question;
        final int level;
        final Node yes;
        final Node no;

        private Node(String question, int level, Node yes, Node no) {
            this.question = question;
            this.level = level;
            this.yes = yes;
            this.no = no;
        }

        boolean isLeaf() {
            return question == null;
        }
    }

    private final Node root;

    private DecisionTree(Node root) {
        this.root = root;
    }

    static Node leaf(int level) {
        if (level < TriageSystem.MOST_URGENT || level > TriageSystem.LEAST_URGENT) {
            throw new IllegalArgumentException("Nivel invalido: " + level);
        }
        return new Node(null, level, null, null);
    }

    static Node ask(String question, Node yes, Node no) {
        if (question == null || yes == null || no == null) {
            throw new IllegalArgumentException("Un nodo de pregunta necesita pregunta y dos hijos");
        }
        return new Node(question, 0, yes, no);
    }

    /** Protocolo de ejemplo con 5 preguntas y 6 hojas. */
    public static DecisionTree defaultProtocol() {
        Node root = ask("La persona esta inconsciente o no respira?",
                leaf(1),
                ask("Tiene dificultad respiratoria grave o dolor en el pecho?",
                        leaf(2),
                        ask("Tiene un sangrado abundante que no se detiene?",
                                leaf(2),
                                ask("Tiene fiebre alta (mas de 39 C) o dolor intenso?",
                                        leaf(3),
                                        ask("Tiene dolor moderado o una lesion reciente?",
                                                leaf(4),
                                                leaf(5))))));
        return new DecisionTree(root);
    }

    /** Recorre un camino raiz-hoja segun las respuestas. O(h). */
    public int classify(Answerer answerer) {
        Node current = root;
        while (!current.isLeaf()) {
            current = answerer.ask(current.question) ? current.yes : current.no;
        }
        return current.level;
    }

    public int countNodes() {
        return countNodes(root);
    }

    public int countLeaves() {
        return countLeaves(root);
    }

    /** Altura en aristas (hoja = 0). */
    public int height() {
        return height(root);
    }

    /** Protocolo completo en preorder, con sangria segun la profundidad. */
    public List<String> protocolLines() {
        List<String> out = new ArrayList<>();
        preorder(root, 0, "", out);
        return out;
    }

    private static int countNodes(Node n) {
        if (n == null) {
            return 0;
        }
        return 1 + countNodes(n.yes) + countNodes(n.no);
    }

    private static int countLeaves(Node n) {
        if (n == null) {
            return 0;
        }
        if (n.isLeaf()) {
            return 1;
        }
        return countLeaves(n.yes) + countLeaves(n.no);
    }

    private static int height(Node n) {
        if (n == null) {
            return -1;
        }
        return 1 + Math.max(height(n.yes), height(n.no));
    }

    private static void preorder(Node n, int depth, String label, List<String> out) {
        String indent = "  ".repeat(depth);
        if (n.isLeaf()) {
            out.add(indent + label + "-> Urgencia " + n.level);
            return;
        }
        out.add(indent + label + n.question);
        preorder(n.yes, depth + 1, "[Si] ", out);
        preorder(n.no, depth + 1, "[No] ", out);
    }
}
