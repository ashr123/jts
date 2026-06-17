/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DoubleKeyMap<K1 extends Comparable<K1>, K2 extends Comparable<K2>, V>
{
	private final Map<K1, Map<K2, V>> topMap = new TreeMap<>();

	public void put(K1 key1, K2 key2, V value)
	{
		Map<K2, V> keyMap = topMap.get(key1);
		if (keyMap == null)
			keyMap = createKeyMap(key1);
		keyMap.put(key2, value);
	}

	private Map<K2, V> createKeyMap(K1 key1)
	{
		Map<K2, V> map = new TreeMap<>();
		topMap.put(key1, map);
		return map;
	}

	public V get(K1 key1, K2 key2)
	{
		Map<K2, V> keyMap = topMap.get(key1);
		if (keyMap == null) return null;
		return keyMap.get(key2);
	}

	public Set<K1> keySet()
	{
		return topMap.keySet();
	}
	public Set<K2> keySet(K1 key)
	{
		Map<K2, V> keyMap = topMap.get(key);
		if (keyMap == null) return new TreeSet<>();
		return keyMap.keySet();
	}

	public Collection<V> values(K1 key1)
	{
		Map<K2, V> keyMap = topMap.get(key1);
		if (keyMap == null) return new ArrayList<>();
		return keyMap.values();
	}
}
