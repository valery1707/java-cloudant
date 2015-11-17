/*
 * Copyright (c) 2015 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.tests.util;

import com.cloudant.client.api.cache.Cache;
import com.cloudant.client.api.cache.Stats;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map based cache implementation for testing
 */
public class TestCache implements Cache<String, Object> {

    private final Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String,
            Object>());

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    @Override
    public void deleteAll(List<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public Map<String, Object> getAll(List<String> keys) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            if (keys.contains(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(Map<String, Object> map) {
        cache.putAll(map);
    }

    @Override
    public long size() {
        return cache.size();
    }

    /**
     * Not implemented for testing
     *
     * @return {@code null}
     */
    @Override
    public Stats getStatistics() {
        return null;
    }
}
