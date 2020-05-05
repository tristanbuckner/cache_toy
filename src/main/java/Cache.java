import java.util.HashMap;
import java.util.Optional;

public class Cache<K, V> {
    private int maxSize;
    private HashMap<K, Node<K, V>> nodes = new HashMap<>();
    private Node<K, V> oldest;
    private Node<K, V> newest;

    /**
     * Constructs a bounded cache with the supplied max size
     * @param maxSize
     */
    public Cache(int maxSize) {
        this.maxSize = maxSize;
    }

    synchronized public void put(K key, V value) {
        if (nodes.containsKey(key)) {
            nodes.get(key).setValue(value);
        } else {

            Node<K, V> node = new Node<>(key, value);

            //Set both to the first node if the cache is empty, perform an eviction if the max size is reached, or
            // just add the node
            if (nodes.size() == 0) {
                oldest = node;
                newest = node;
            } else if (nodes.size() == maxSize) {
                K keyToRemove = popOldest().key;
                nodes.remove(keyToRemove);
                addNewest(node);
            } else {
                addNewest(node);
            }

            nodes.put(key, node);

        }
    }

    /**
     * Get the value corresponding to the supplied key or return null
     * @param key
     * @return
     */
    synchronized public V get(K key) {
        return Optional.ofNullable(nodes.get(key)).map(Node::getValue).orElse(null);
    }

    /**
     * Use the nodes map to jump to the desired key in constant time and detach it from the linked list
     * @param key
     */
    synchronized public void remove(K key) {
        //Clean up newest and oldest when removing all items
        if (newest != null && newest.key.equals(key)) {
            newest = newest.older;
        }

        if (oldest != null && oldest.key.equals(key)) {
            oldest = oldest.newer;
        }

        if (nodes.containsKey(key)) {
            Node<K, V> oldNode = nodes.get(key);
            oldNode.deLink();
            nodes.remove(key);

        }
    }

    /**
     * Pop the oldest element and detach the reference from the adjacent entry
     * @return the oldest element in the cache
     */
    private Node<K, V> popOldest() {
        Node<K, V> returnValue = oldest;

        oldest = oldest.newer;
        oldest.setOlder(null);

        return returnValue;
    }

    /**
     * Add a new node and make the previous newest node point to this new node
     * @param newNode
     */
    private void addNewest(Node<K, V> newNode) {
        newNode.setOlder(newest);
        newest.setNewer(newNode);
        newest = newNode;

    }

    @Override
    public String toString() {
        return "Cache{" +
                "capacity=" + maxSize +
                ", pairs new to old=[" + (newest != null ? newest.newToOld() : "empty") + "]" +
                '}';
    }

    /**
     * Simple Node for doubly linked list operations for cache evictions
     * @param <K>
     * @param <V>
     */

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

        public void deLink() {
            if (older != null ) {
                older.setNewer(newer);
            }

            if (newer != null) {
                newer.setOlder(older);
            }
        }

    }
}
