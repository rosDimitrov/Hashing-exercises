package linearProbingHashDictionary;

import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashedDictionary<K, V> implements DictionaryADT<K, V> {

	private TableEntry<K, V>[] hashTable;
	private int numberOfEntries;
	private int locationsUsed; // number of table locations not null
	private static final int DEFAULT_SIZE = 101; // must be prime
	private static final double MAX_LOAD_FACTOR = 0.5;

	public HashedDictionary(int tableSize) {
		hashTable = new TableEntry[tableSize];
		numberOfEntries = 0;
		locationsUsed = 0;
	}

	public HashedDictionary() {
		this(DEFAULT_SIZE);
	}

	@Override
	public V add(K key, V value) {

		V oldValue; // value to return

		int index = getHashIndex(key);
		index = probe(index, key); // check for and resolve collision

		if ((hashTable[index] == null) || hashTable[index].isRemoved()) {

			// key not found, so insert new entry
			hashTable[index] = new TableEntry<K, V>(key, value);
			numberOfEntries++;
			locationsUsed++;
			oldValue = null;
		} else {

			// key found; get old value for return and then replace it
			oldValue = hashTable[index].getValue();
			hashTable[index].setValue(value);
		}

		return oldValue;

	}

	@Override
	public V remove(K key) {
		V removedValue = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1) {

			// key found: mark the entry as removed and return its value
			removedValue = hashTable[index].getValue();
			hashTable[index].setToRemoved(); // sets inTable to false
			numberOfEntries--;
		}
		// else key not found, return null
		return removedValue;
	}

	private int getHashIndex(K key) {
		int hashIndex = key.hashCode() % hashTable.length;
		if (hashIndex < 0)
			hashIndex = hashIndex + hashTable.length;

		return hashIndex;
	}

	@Override
	public V getValue(K key) {
		V result = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1) {
			result = hashTable[index].getValue(); // key found
		}
		// else key not found and return null
		return result;
	}

	@Override
	public boolean contains(K key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<K> getKeyIterator() {
		return new KeyIterator();
	}

	@Override
	public Iterator<V> getValueIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	private int locate(int index, K key) { // traverses clusters and check if
											// the key is there

		// if there, get the number of the place, else get - 1;

		boolean found = false;

		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].getInTable()
					&& key.equals(hashTable[index].getKey())) {
				found = true;
			} else {
				index = (index + 1) % hashTable.length;
			}
		}

		int result = -1;
		if (found) {
			result = index;
		}
		return result;

	}

	private int probe(int index, K key) {

		boolean found = false;
		int removedStateIndex = -1; //

		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].getInTable()) {
				if (key.equals(hashTable[index].getKey())) {
					found = true; // key found
				} else { // follow probe sequence
					index = (index + 1) % hashTable.length;
				}
			} else { // skip entries that were removed

				// save index of first location in removed state
				if (removedStateIndex == -1) {
					removedStateIndex = index;
				}
				index = (index + 1) % hashTable.length;
			}
		}

		// Assertion: either key or null is found at hashTable[index]
		if (found || (removedStateIndex == -1)) {
			return index;
		} else {
			return removedStateIndex; // index of an available location
		}

	}

	private class KeyIterator implements Iterator<K> {

		private int currentIndex; // current position in hash table
		private int numberLeft;

		private KeyIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		}

		@Override
		public boolean hasNext() {
			return numberLeft > 0;
		}

		@Override
		public K next() {

			K result = null;
			if (hasNext()) {
				// find index of next entry
				while ((hashTable[currentIndex] == null)
						|| hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				} // end while
				result = hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			} else {
				throw new NoSuchElementException();
			}
			return result;

		}

		@Override
		public void remove() {
			throw new UnsupportedAddressTypeException();

		}

	}

	private class TableEntry<S, T> {

		private S key;
		private T value;
		private boolean inTable;

		private TableEntry(S searchKey, T dataValue) {
			key = searchKey;
			value = dataValue;
			inTable = true;
		}

		public S getKey() {
			return key;
		}

		public void setKey(S key) {
			this.key = key;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		private boolean getInTable() {
			return inTable; // returns whether it's in the table;
		}

		private boolean isRemoved() {
			return inTable != true; // checks if the element is available(in the
									// table).
			// if it is removed, I can place elements there.
		}

		private void setToInTable() {
			inTable = true;
		}

		private void setToRemoved() {
			inTable = false;
		}
	}

}
