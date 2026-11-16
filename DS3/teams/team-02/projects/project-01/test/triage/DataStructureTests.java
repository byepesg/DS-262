package triage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Pruebas unitarias de las estructuras de datos propias: LinkedQueue,
 * LinkedStack, HashTable y LevelPriorityQueue.
 */
final class DataStructureTests {

    private DataStructureTests() {
    }

    static void runAll() {
        linkedQueue();
        linkedStack();
        hashTable();
        levelPriorityQueue();
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T value : iterable) {
            list.add(value);
        }
        return list;
    }

    // ------------------------------------------------------------------
    private static void linkedQueue() {
        String s = "LinkedQueue";

        Check.run(s, "cola vacia: dequeue y peek lanzan excepcion", () -> {
            LinkedQueue<Integer> q = new LinkedQueue<>();
            Check.isTrue(q.isEmpty(), "debe iniciar vacia");
            Check.equal(0, q.size(), "tamano inicial");
            Check.throwsException(NoSuchElementException.class, q::dequeue, "dequeue en vacia");
            Check.throwsException(NoSuchElementException.class, q::peek, "peek en vacia");
        });

        Check.run(s, "orden FIFO", () -> {
            LinkedQueue<Integer> q = new LinkedQueue<>();
            for (int i = 0; i < 5; i++) {
                q.enqueue(i);
            }
            for (int i = 0; i < 5; i++) {
                Check.equal(i, q.dequeue(), "elemento " + i);
            }
            Check.isTrue(q.isEmpty(), "vacia al final");
        });

        Check.run(s, "enqueueFront inserta al frente", () -> {
            LinkedQueue<String> q = new LinkedQueue<>();
            q.enqueue("b");
            q.enqueueFront("a");
            q.enqueue("c");
            Check.equal(List.of("a", "b", "c"), toList(q), "orden");
            LinkedQueue<String> empty = new LinkedQueue<>();
            empty.enqueueFront("x");
            Check.equal("x", empty.peek(), "frente en cola vacia");
            Check.equal("x", empty.dequeue(), "unico elemento");
            Check.isTrue(empty.isEmpty(), "vuelve a quedar vacia");
        });

        Check.run(s, "remove(nodo) en cabeza, medio, final y unico", () -> {
            LinkedQueue<String> q = new LinkedQueue<>();
            LinkedQueue.Node<String> a = q.enqueue("a");
            LinkedQueue.Node<String> b = q.enqueue("b");
            LinkedQueue.Node<String> c = q.enqueue("c");
            LinkedQueue.Node<String> d = q.enqueue("d");
            q.remove(b);
            Check.equal(List.of("a", "c", "d"), toList(q), "tras quitar el del medio");
            q.remove(a);
            Check.equal(List.of("c", "d"), toList(q), "tras quitar la cabeza");
            q.remove(d);
            Check.equal(List.of("c"), toList(q), "tras quitar el final");
            q.enqueue("e");
            Check.equal(List.of("c", "e"), toList(q), "el final se actualizo bien");
            q.remove(c);
            Check.equal("e", q.peek(), "nueva cabeza");
            Check.equal(1, q.size(), "tamano");
        });

        Check.run(s, "remove de un nodo ya retirado o ajeno lanza excepcion", () -> {
            LinkedQueue<String> q = new LinkedQueue<>();
            LinkedQueue<String> other = new LinkedQueue<>();
            LinkedQueue.Node<String> a = q.enqueue("a");
            LinkedQueue.Node<String> foreign = other.enqueue("z");
            Check.throwsException(IllegalArgumentException.class, () -> q.remove(foreign), "nodo ajeno");
            q.remove(a);
            Check.throwsException(IllegalArgumentException.class, () -> q.remove(a), "nodo ya retirado");
            Check.throwsException(IllegalArgumentException.class, () -> q.remove(null), "nodo null");
            Check.isTrue(q.isEmpty(), "sigue vacia");
            Check.equal(1, other.size(), "la otra cola no cambia");
        });

        Check.run(s, "dequeue invalida el localizador del nodo retirado", () -> {
            LinkedQueue<String> q = new LinkedQueue<>();
            LinkedQueue.Node<String> a = q.enqueue("a");
            q.enqueue("b");
            q.dequeue();
            Check.throwsException(IllegalArgumentException.class, () -> q.remove(a), "nodo desencolado");
            Check.equal(List.of("b"), toList(q), "contenido intacto");
        });

        Check.run(s, "iterador no modifica la cola y falla al agotarse", () -> {
            LinkedQueue<Integer> q = new LinkedQueue<>();
            q.enqueue(1);
            q.enqueue(2);
            Iterator<Integer> it = q.iterator();
            Check.equal(1, it.next(), "primero");
            Check.equal(2, it.next(), "segundo");
            Check.isTrue(!it.hasNext(), "sin mas");
            Check.throwsException(NoSuchElementException.class, it::next, "next agotado");
            Check.equal(2, q.size(), "tamano intacto");
        });

        Check.run(s, "operaciones mixtas aleatorias contra java.util.LinkedList", () -> {
            Random rnd = new Random(7);
            LinkedQueue<Integer> q = new LinkedQueue<>();
            java.util.LinkedList<Integer> model = new java.util.LinkedList<>();
            List<LinkedQueue.Node<Integer>> nodes = new ArrayList<>();
            for (int step = 0; step < 20_000; step++) {
                int op = rnd.nextInt(4);
                if (op == 0 || model.isEmpty()) {
                    nodes.add(q.enqueue(step));
                    model.addLast(step);
                } else if (op == 1) {
                    nodes.add(q.enqueueFront(step));
                    model.addFirst(step);
                } else if (op == 2) {
                    Check.equal(model.removeFirst(), q.dequeue(), "dequeue en paso " + step);
                } else {
                    LinkedQueue.Node<Integer> node = nodes.get(rnd.nextInt(nodes.size()));
                    if (model.contains(node.value())) {
                        q.remove(node);
                        model.remove(node.value());
                    }
                }
                Check.equal(model.size(), q.size(), "tamano en paso " + step);
            }
            Check.equal(model, toList(q), "contenido final");
        });
    }

    // ------------------------------------------------------------------
    private static void linkedStack() {
        String s = "LinkedStack";

        Check.run(s, "pila vacia: pop y peek lanzan excepcion", () -> {
            LinkedStack<Integer> st = new LinkedStack<>();
            Check.isTrue(st.isEmpty(), "debe iniciar vacia");
            Check.throwsException(NoSuchElementException.class, st::pop, "pop en vacia");
            Check.throwsException(NoSuchElementException.class, st::peek, "peek en vacia");
        });

        Check.run(s, "orden LIFO", () -> {
            LinkedStack<Integer> st = new LinkedStack<>();
            for (int i = 0; i < 5; i++) {
                st.push(i);
            }
            Check.equal(4, st.peek(), "tope");
            for (int i = 4; i >= 0; i--) {
                Check.equal(i, st.pop(), "elemento " + i);
            }
            Check.isTrue(st.isEmpty(), "vacia al final");
        });

        Check.run(s, "iterador recorre del tope al fondo sin modificar", () -> {
            LinkedStack<String> st = new LinkedStack<>();
            st.push("a");
            st.push("b");
            st.push("c");
            Check.equal(List.of("c", "b", "a"), toList(st), "orden del iterador");
            Check.equal(3, st.size(), "tamano intacto");
            Check.equal("c", st.peek(), "tope intacto");
        });
    }

    // ------------------------------------------------------------------
    private static void hashTable() {
        String s = "HashTable";

        Check.run(s, "put, get, containsKey y reemplazo", () -> {
            HashTable<String, Integer> h = new HashTable<>();
            Check.equal(null, h.put("a", 1), "put nuevo devuelve null");
            Check.equal(1, h.get("a"), "get");
            Check.isTrue(h.containsKey("a"), "containsKey");
            Check.equal(1, h.put("a", 2), "put existente devuelve el anterior");
            Check.equal(2, h.get("a"), "valor reemplazado");
            Check.equal(1, h.size(), "reemplazar no cambia el tamano");
            Check.equal(null, h.get("zz"), "clave ausente");
        });

        Check.run(s, "remove de clave presente y ausente", () -> {
            HashTable<String, Integer> h = new HashTable<>();
            h.put("a", 1);
            h.put("b", 2);
            Check.equal(1, h.remove("a"), "remove presente");
            Check.equal(null, h.remove("a"), "remove repetido");
            Check.isTrue(!h.containsKey("a"), "ya no esta");
            Check.equal(1, h.size(), "tamano");
        });

        Check.run(s, "claves null se rechazan", () -> {
            HashTable<String, Integer> h = new HashTable<>();
            Check.throwsException(IllegalArgumentException.class, () -> h.put(null, 1), "put null");
            Check.throwsException(IllegalArgumentException.class, () -> h.get(null), "get null");
        });

        Check.run(s, "colisiones: claves con el mismo hashCode", () -> {
            // "Aa" y "BB" tienen el mismo String.hashCode() (2112).
            HashTable<String, Integer> h = new HashTable<>(4);
            h.put("Aa", 1);
            h.put("BB", 2);
            h.put("AaAa", 3);
            h.put("BBBB", 4);
            h.put("AaBB", 5);
            Check.equal(1, h.get("Aa"), "Aa");
            Check.equal(2, h.get("BB"), "BB");
            Check.equal(5, h.get("AaBB"), "AaBB");
            Check.equal(2, h.remove("BB"), "remove dentro de la cadena");
            Check.equal(1, h.get("Aa"), "el resto de la cadena sigue");
            Check.equal(4, h.size(), "tamano");
        });

        Check.run(s, "redimensiona y conserva todas las entradas", () -> {
            HashTable<String, Integer> h = new HashTable<>(2);
            int n = 10_000;
            for (int i = 0; i < n; i++) {
                h.put("id-" + i, i);
            }
            Check.equal(n, h.size(), "tamano");
            Check.isTrue(h.capacity() >= n / 0.75, "capacidad >= n / factor de carga, fue " + h.capacity());
            Check.isTrue(h.longestChain() <= 8, "cadena mas larga corta, fue " + h.longestChain());
            for (int i = 0; i < n; i++) {
                Check.equal(i, h.get("id-" + i), "entrada " + i);
            }
        });

        Check.run(s, "operaciones aleatorias contra java.util.HashMap", () -> {
            Random rnd = new Random(11);
            HashTable<String, Integer> h = new HashTable<>();
            Map<String, Integer> model = new HashMap<>();
            for (int step = 0; step < 50_000; step++) {
                String key = "k" + rnd.nextInt(2_000);
                int op = rnd.nextInt(3);
                if (op == 0) {
                    Check.equal(model.put(key, step), h.put(key, step), "put " + key);
                } else if (op == 1) {
                    Check.equal(model.remove(key), h.remove(key), "remove " + key);
                } else {
                    Check.equal(model.get(key), h.get(key), "get " + key);
                }
            }
            Check.equal(model.size(), h.size(), "tamano final");
        });
    }

    // ------------------------------------------------------------------
    private static void levelPriorityQueue() {
        String s = "LevelPriorityQueue";

        Check.run(s, "atiende por nivel y desempata por llegada", () -> {
            LevelPriorityQueue pq = new LevelPriorityQueue(5);
            pq.insert(new Ticket("A", "a", 3, 0));
            pq.insert(new Ticket("B", "b", 1, 1));
            pq.insert(new Ticket("C", "c", 5, 2));
            pq.insert(new Ticket("D", "d", 1, 3));
            StringBuilder order = new StringBuilder();
            while (!pq.isEmpty()) {
                order.append(pq.removeNext().getId());
            }
            Check.equal("BDAC", order.toString(), "orden de atencion");
        });

        Check.run(s, "cola vacia lanza excepcion", () -> {
            LevelPriorityQueue pq = new LevelPriorityQueue(5);
            Check.throwsException(NoSuchElementException.class, pq::removeNext, "removeNext");
            Check.throwsException(NoSuchElementException.class, pq::peekNext, "peekNext");
        });

        Check.run(s, "remove por localizador y linearFindById", () -> {
            LevelPriorityQueue pq = new LevelPriorityQueue(5);
            pq.insert(new Ticket("A", "a", 2, 0));
            LinkedQueue.Node<Ticket> b = pq.insert(new Ticket("B", "b", 2, 1));
            pq.insert(new Ticket("C", "c", 2, 2));
            Check.equal("B", pq.remove(b).getId(), "ticket retirado");
            Check.equal(null, pq.linearFindById("B"), "ya no se encuentra");
            Check.equal("C", pq.linearFindById("C").getId(), "C sigue");
            Check.equal(2, pq.size(), "tamano");
            Check.equal(2, pq.sizeOfLevel(2), "tamano del nivel");
        });

        Check.run(s, "insertFront y orden de servicio", () -> {
            LevelPriorityQueue pq = new LevelPriorityQueue(5);
            pq.insert(new Ticket("X", "x", 4, 5));
            pq.insert(new Ticket("Y", "y", 2, 6));
            pq.insertFront(new Ticket("W", "w", 4, 1));
            List<String> ids = new ArrayList<>();
            for (Ticket t : pq.toListInServiceOrder()) {
                ids.add(t.getId());
            }
            Check.equal(List.of("Y", "W", "X"), ids, "orden");
        });

        Check.run(s, "urgencia fuera de rango se rechaza", () -> {
            LevelPriorityQueue pq = new LevelPriorityQueue(5);
            Check.throwsException(IllegalArgumentException.class,
                    () -> pq.insert(new Ticket("Z", "z", 6, 0)), "urgencia 6");
            Check.throwsException(IllegalArgumentException.class,
                    () -> pq.insert(new Ticket("Z", "z", 0, 0)), "urgencia 0");
            Check.equal(0, pq.size(), "no se inserto nada");
        });
    }
}
