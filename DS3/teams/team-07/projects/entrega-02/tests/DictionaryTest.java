import java.util.Random;

public class DictionaryTest
{
    public static void run() {
        AVLTreeDictionary<Integer, String> tree = new AVLTreeDictionary<>();
        TestRunner.check(tree.isEmpty() && tree.get(1) == null, "AVL vacío: get devuelve null");

        for (int i = 1; i <= 1023; i++) tree.put(i, "v" + i);
        TestRunner.check(tree.size() == 1023, "AVL guarda 1023 llaves insertadas en orden");
        TestRunner.check(tree.height() <= 15, "altura del AVL con 1023 llaves en orden: " + tree.height() + " (máximo 1.44 log n)");
        TestRunner.check("v500".equals(tree.get(500)), "get encuentra una llave existente");
        TestRunner.check(tree.get(2000) == null, "get de una llave inexistente devuelve null");

        tree.put(500, "nuevo");
        TestRunner.check("nuevo".equals(tree.get(500)) && tree.size() == 1023, "put con llave repetida reemplaza el valor");

        boolean removedAll = true;
        for (int i = 1; i <= 1023; i += 2) removedAll &= tree.remove(i);
        TestRunner.check(removedAll && tree.size() == 511, "remove de las 512 llaves impares");
        TestRunner.check(!tree.containsKey(1) && tree.containsKey(2), "tras remove solo quedan las pares");
        TestRunner.check(!tree.remove(1), "remove de una llave ya eliminada devuelve false");

        Random random = new Random(3);
        AVLTreeDictionary<Integer, Integer> randomTree = new AVLTreeDictionary<>();
        for (int i = 0; i < 10000; i++) randomTree.put(random.nextInt(), i);
        TestRunner.check(randomTree.height() <= 20, "altura con 10000 llaves aleatorias: " + randomTree.height());

        TestRunner.checkThrows(() -> tree.put(null, "x"), "put con llave nula lanza excepción");
    }
}
