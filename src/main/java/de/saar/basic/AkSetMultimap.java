/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author koller
 */
public class AkSetMultimap<K,V> implements SetMultimap<K,V>, Serializable {
    private Map<K,Set<V>> delegate;
    private int size = 0;

    public AkSetMultimap() {
        delegate = createMap();
    }
    
    
    protected Set<V> createSet() {
        return new HashSet<V>();
    }
    
    protected Map<K, Set<V>> createMap() {
        return new HashMap<K, Set<V>>();
    }
    

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Object o) {
        return delegate.containsKey((K) o);
    }

    public boolean containsValue(Object o) {
        return values().contains(o);
    }    

    public Set<V> get(K k) {
        return delegate.get(k);
    }
    
    public boolean containsEntry(Object o, Object o1) {
        Collection<V> set = delegate.get(o);
        
        if( set != null && set.contains(o1)) {
            return true;
        }
        
        return false;
    }

    public boolean put(K k, V v) {
        Set<V> set = delegate.get(k);
        
        if( set == null ) {
            set = createSet();
            delegate.put(k, set);
        }
        
        boolean changed = set.add(v);
        if( changed ) {
            size++;
        }
        
        return changed;
    }

    public boolean remove(Object o, Object o1) {
        Collection<V> set = delegate.get(o);
        
        if( set != null ) {
            boolean changed = set.remove(o1);
            if( changed ) {
                if( set.isEmpty() ) {
                    delegate.remove(set);
                }
                
                size--;
                return true;
            }
        }
        
        return false;
    }

    public boolean putAll(K k, Iterable<? extends V> itrbl) {
        boolean ret = false;
        for( V value : itrbl ) {
            ret = put(k,value) || ret;
        }
        
        return ret;
    }

    public boolean putAll(Multimap<? extends K, ? extends V> mltmp) {        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<V> replaceValues(K k, Iterable<? extends V> itrbl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<V> removeAll(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        delegate.clear();
        size = 0;
    }

    public Set<K> keySet() {
        return delegate.keySet();
    }

    public Multiset<K> keys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<V> values() {
        Collection<V> ret = new HashSet<V>();
        
        for( K key : keySet() ) {
            ret.addAll(delegate.get(key));
        }
        
        return ret;
    }

    public Set<Entry<K, V>> entries() {
        Set<Entry<K,V>> ret = new HashSet<Entry<K, V>>();
        
        for( K key : keySet() ) {
            for( V value : delegate.get(key)) {
                ret.add(new AbstractMap.SimpleEntry<K, V>(key, value));
            }
        }
        
        return ret;
    }

    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> ret = new HashMap<K, Collection<V>>();
        ret.putAll(delegate);
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("[");
        boolean first = true;
        
        for( K key : keySet() ) {
            if( first ) {
                first = false;
            } else {
                buf.append(", ");
            }
            
            buf.append(key.toString());
            buf.append("={");
            buf.append(StringTools.join(delegate.get(key), ","));
            buf.append("}");
        }
        
        buf.append("]");
        return buf.toString();
    }
}
