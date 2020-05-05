import java.util.HashMap;
import java.util.Optional;

public class Cache<K, V> {
    private int capacity;
    private HashMap<K, Node<K, V>> nodes = new HashMap<>();
    private Node<K, V> oldest;
    private Node<K, V> newest;


    public Cache(int capacity) {
        this.capacity = capacity;
    }

    synchronized public void put(K key, V value) {
        if (nodes.containsKey(key)) {
            nodes.get(key).setValue(value);
        } else {

            Node<K, V> node = new Node<>(key, value);

            if (nodes.size() == 0) {
                oldest = node;
                newest = node;
            } else if (nodes.size() == capacity) {
                K keyToRemove = popOldest().key;
                nodes.remove(keyToRemove);
                addNewest(node);
            } else {
                addNewest(node);
            }

            nodes.put(key, node);

        }
    }

    synchronized public V get(K key) {
        return Optional.ofNullable(nodes.get(key)).map(Node::getValue).orElse(null);
    }

    synchronized public void remove(K key) {
        if (newest != null && newest.key.equals(key)) {
            newest = newest.older;
        }

        if (oldest != null && oldest.key.equals(key)) {
            oldest = oldest.newer;
        }

        if (nodes.containsKey(key)) {
            Node<K, V> oldNode = nodes.get(key);
            oldNode.delink();
            nodes.remove(key);

        }
    }

    private Node<K, V> popOldest() {
        Node<K, V> returnValue = oldest;

        oldest = oldest.newer;
        oldest.setOlder(null);

        return returnValue;
    }

    private void addNewest(Node<K, V> newNode) {
        newNode.setOlder(newest);
        newest.setNewer(newNode);
        newest = newNode;

    }

    @Override
    public String toString() {
        return "Cache{" +
                "capacity=" + capacity +
                ", pairs new to old=[" + (newest != null ? newest.newToOld() : "empty") + "]" +
                '}';
    }

    private static class Node<K, V> {
        private Node<K, V> older;
        private Node<K, V> newer;
        private final K key;
        private V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public String newToOld() {
            return "("+ key + " -> " + value + ")" + ", " + Optional.ofNullable(older).map(Node::newToOld).orElse("Nil");
        }

        public void setOlder(Node<K, V> older) {
            this.older = older;
        }

        public void setNewer(Node<K, V> newer) {
            this.newer = newer;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public void delink() {
            if (older != null ) {
                older.setNewer(newer);
            }

            if (newer != null) {
                newer.setOlder(older);
            }
        }

    }
}
