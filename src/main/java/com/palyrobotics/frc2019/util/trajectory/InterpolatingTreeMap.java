package com.palyrobotics.frc2019.util.trajectory;

import java.util.Map;
import java.util.TreeMap;

/**
 * Interpolating Tree Maps are used to get values at points that are not defined by making a guess from points that are defined. This uses linear interpolation.
 *
 * @param <K> The type of the key (must implement {@link InverseInterpolable})
 * @param <V> The type of the value (must implement {@link Interpolable})
 */
public class InterpolatingTreeMap<K extends InverseInterpolable<K> & Comparable<K>, V extends Interpolable<V>> extends TreeMap<K, V> {

    private int mMaxSize;

    public InterpolatingTreeMap(int maximumSize) {
        mMaxSize = maximumSize;
    }

    public InterpolatingTreeMap() {
        this(0);
    }

    /**
     * Inserts a key value pair, and trims the tree if a max size is specified
     *
     * @param key   Key for inserted data
     * @param value Value for inserted data
     * @return the value
     */
    @Override
    public V put(K key, V value) {
        if (mMaxSize > 0 && mMaxSize <= size()) {
            // "Prune" the tree if it is oversize
            K first = firstKey();
            remove(first);
        }
        super.put(key, value);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
    }

    /**
     * @param key Lookup for a value (does not have to exist)
     * @return V or null; V if it is {@link Interpolable} or exists, null if it is at a bound and cannot average
     */
    public V getInterpolated(K key) {
        V gotValue = get(key);
        if (gotValue == null) {
            /* Get surrounding keys for interpolation */
            K topBound = ceilingKey(key);
            K bottomBound = floorKey(key);
            /* If attempting interpolation at ends of tree, return the nearest data point */
            if (topBound == null && bottomBound == null) {
                return null;
            } else if (topBound == null) {
                return get(bottomBound);
            } else if (bottomBound == null) {
                return get(topBound);
            }
            /* Get surrounding values for interpolation */
            V topElem = get(topBound);
            V bottomElem = get(bottomBound);
            return bottomElem.interpolate(topElem, bottomBound.inverseInterpolate(topBound, key));
        } else {
            return gotValue;
        }
    }
}
