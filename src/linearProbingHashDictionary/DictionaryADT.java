package linearProbingHashDictionary;

import java.util.Iterator;

public interface DictionaryADT<K, V> {

	public V add(K key, V value);

	public V remove(K key);

	public V getValue(K key);

	public boolean contains(K key);

	public Iterator<K> getKeyIterator();

	public Iterator<V> getValueIterator();

	public boolean isEmpty();

	public int getSize();

	public void clear();

}
