package slimeknights.tconstruct.library.utils;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implementation of a multimap using a single key and a backing collection of values
 * @param <K>  Key type
 * @param <V>  Value type
 */
@RequiredArgsConstructor
public class SingleKeyMultimap<K,V> implements Multimap<K,V> {
  @Getter
  @Nonnull
  private final K key;
  @Getter
  @Nonnull
  private final Collection<V> values;

  /** Validates the given key is the correct one for this map */
  private void validateKey(@Nullable Object key) {
    if (key != this.key) {
      throw new IllegalArgumentException("Cannot modify a single key multimap with mismatching key");
    }
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public boolean containsKey(@Nullable Object key) {
    return key == this.key;
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean containsValue(@Nullable Object value) {
    return values.contains(value);
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
    return key == this.key && values.contains(value);
  }

  @Override
  public boolean put(@Nullable K key, @Nullable V value) {
    this.validateKey(key);
    return values.add(value);
  }

  @SuppressWarnings("SuspiciousMethodCalls")
  @Override
  public boolean remove(@Nullable Object key, @Nullable Object value) {
    this.validateKey(key);
    return values.remove(value);
  }

  @Override
  public boolean putAll(@Nullable K key, Iterable<? extends V> values) {
    this.validateKey(key);
    boolean didChange = false;
    for (V value : values) {
      didChange |= this.values.add(value);
    }
    return didChange;
  }

  @Override
  public boolean putAll(Multimap<? extends K,? extends V> multimap) {
    boolean didChange = false;
    for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
      didChange |= put(entry.getKey(), entry.getValue());
    }
    return didChange;
  }

  @Override
  public Collection<V> removeAll(@Nullable Object key) {
    Collection<V> values = ImmutableList.copyOf(this.values);
    this.values.clear();
    return values;
  }

  @Override
  public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
    this.validateKey(key);
    Collection<V> returnValues = removeAll(key);
    putAll(key, values);
    return returnValues;
  }

  @Override
  public void clear() {
    this.values.clear();
  }

  @Override
  public Collection<V> get(@Nullable K key) {
    if (key == this.key) {
      return values;
    }
    return Collections.emptyList();
  }

  /** Cached unmodifiable view of {@link #values()} */
  private transient Collection<V> unmodifiableValues;

  @Override
  public Collection<V> values() {
    if (unmodifiableValues == null) {
      unmodifiableValues = Collections.unmodifiableCollection(values);
    }
    return unmodifiableValues;
  }

  /** Cached set of the single key */
  private transient Set<K> keySet;

  @Override
  public Set<K> keySet() {
    if (keySet == null) {
      keySet = Collections.singleton(this.key);
    }
    return keySet;
  }

  /** Cached multiset of the single key */
  private transient Multiset<K> keyMultiset;

  @Override
  public Multiset<K> keys() {
    if (keyMultiset == null) {
      keyMultiset = ImmutableMultiset.of(key);
    }
    return keyMultiset;
  }

  /** Cached map of all values */
  private transient Map<K,Collection<V>> asMap;

  @Override
  public Map<K,Collection<V>> asMap() {
    if (asMap == null) {
      asMap = ImmutableMap.of(key, values);
    }
    return asMap;
  }

  /** Cached entries collection */
  private transient Collection<Entry<K,V>> entries;

  @Override
  public Collection<Entry<K,V>> entries() {
    if (entries == null) {
      entries = new SingleKeyEntries();
    }
    return entries;
  }

  private class SingleKeyEntries extends AbstractCollection<Entry<K,V>> {
    private SingleKeyMultimap<K,V> getMap() {
      return SingleKeyMultimap.this;
    }

    @Override
    public Iterator<Entry<K,V>> iterator() {
      return new SingleKeyIterator();
    }

    @Override
    public boolean contains(@Nullable Object o) {
      if (o instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        return SingleKeyMultimap.this.containsEntry(entry.getKey(), entry.getValue());
      }
      return false;
    }

    @Override
    public boolean remove(@Nullable Object o) {
      if (o instanceof Entry) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        return SingleKeyMultimap.this.remove(entry.getKey(), entry.getValue());
      }
      return false;
    }

    @Override
    public int size() {
      return values.size();
    }

    @Override
    public void clear() {
      SingleKeyMultimap.this.clear();
    }

    @Override
    public int hashCode() {
      return 31 * SingleKeyMultimap.this.values.hashCode() + SingleKeyMultimap.this.key.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj.getClass() != this.getClass()) {
        return false;
      }
      SingleKeyMultimap<?,?> otherMap = ((SingleKeyEntries)obj).getMap();
      return otherMap.key.equals(SingleKeyMultimap.this.key) && otherMap.values.equals(SingleKeyMultimap.this.values);
    }
  }

  @AllArgsConstructor
  private class SingleKeyEntry implements Map.Entry<K,V> {
    private V value;

    @Override
    public K getKey() {
      return SingleKeyMultimap.this.key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      V oldValue = this.value;
      if (!SingleKeyMultimap.this.values.remove(this.value)) {
        throw new IllegalStateException("Entry already removed");
      }
      // if failing to add, attempt to restore the state
      if (!SingleKeyMultimap.this.values.add(this.value)) {
        try {
          if (!SingleKeyMultimap.this.values.add(oldValue)) {
            throw new IllegalStateException("Failed to restore collection after failing to add new value");
          }
        } catch (Exception ex) {
          throw new IllegalStateException("Failed to restore collection after failing to add new value", ex);
        }
        throw new IllegalArgumentException("Failed to add entry");
      }
      this.value = value;
      return oldValue;
    }
  }

  private class SingleKeyIterator extends AbstractIterator<Entry<K,V>> {
    private final Iterator<V> values;
    private SingleKeyIterator() {
      this.values = SingleKeyMultimap.this.values.iterator();
    }

    @Override
    protected Entry<K,V> computeNext() {
      if (!values.hasNext()) {
        return endOfData();
      }
      return new SingleKeyEntry(values.next());
    }
  }
}
