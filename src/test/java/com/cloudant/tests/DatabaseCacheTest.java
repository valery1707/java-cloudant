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

package com.cloudant.tests;

import static org.junit.Assert.assertEquals;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.cache.Cache;
import com.cloudant.client.api.cache.CacheDatabaseDecorator;
import com.cloudant.client.api.model.Response;
import com.cloudant.test.main.RequiresDB;
import com.cloudant.tests.util.CloudantClientResource;
import com.cloudant.tests.util.DatabaseResource;
import com.cloudant.tests.util.TestCache;
import com.cloudant.tests.util.Utils;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.RuleChain;

@Category(RequiresDB.class)
public class DatabaseCacheTest {

    public static CloudantClientResource clientResource = new CloudantClientResource();
    public static DatabaseResource dbResource = new DatabaseResource(clientResource);
    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(clientResource).around(dbResource);

    private Foo foo;
    private Cache<String, Object> cache;
    private Database db;

    @Before
    public void setUp() {
        //use a new cache for each test
        db = CacheDatabaseDecorator.cacheDatabase(dbResource.get(), (cache = new TestCache()));
        //generate a Foo
        foo = new Foo(Utils.generateUUID());
    }

    /**
     * Test that a post inserts a document into the cache
     */
    @Test
    public void testCachePut() {
        db.post(foo);
        assertEquals("The cache should contain 1 entry", 1, cache.size());
        assertEquals("The entry in the cache should match the one posted", foo, cache.get(foo
                .get_id()));
    }

    /**
     * Test that a find retrieves a document from the cache
     */
    @Test
    public void testCacheGet() {
        //put directly in the cache not the DB to validate that we actually get from the cache
        //(we'd get a 404 if we go as far as the DB)
        cache.put(foo.get_id(), foo);
        //do a db.find, but expect the return from the cache
        Foo cachedFoo = db.find(Foo.class, foo.get_id());
        assertEquals("The retrieved document should match", foo, cachedFoo);
    }

    /**
     * Test that a db remove also removes the entry from the cache
     */
    @Test
    public void testCacheDelete() {
        //to remove the document needs to exist in the DB so post first
        Response r = db.post(foo);
        //assert that the entry was cached
        assertEquals("The cache should contain 1 entry", 1, cache.size());
        //to remove we need a rev, so set it based on the response
        foo.set_rev(r.getRev());
        //now call db.remove
        db.remove(foo);
        //assert that the cache is now empty
        assertEquals("The cache should be empty", 0, cache.size());
    }
}
