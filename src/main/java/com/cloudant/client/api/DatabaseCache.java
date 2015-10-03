/**
 * 
 */
package com.cloudant.client.api;

import static org.lightcouch.internal.CouchDbUtil.assertNotEmpty;
import static org.lightcouch.internal.CouchDbUtil.close;
import static org.lightcouch.internal.CouchDbUtil.createPost;
import static org.lightcouch.internal.CouchDbUtil.getResponse;
import static org.lightcouch.internal.URIBuilder.buildUri;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.lightcouch.CouchDatabase;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import com.cloudant.client.api.model.Params;

import client.Cache;
import client.CacheType;
import client.RedisCache;
import client.InProcessCache;
import client.Util;

/**
 * Contains a Database Public API implementation with a cache.
 * 
 * @author Arun Iyengar
 */
public class DatabaseCache {

    private CouchDatabase db;
    private CloudantClient client;
    private Cache<String, Object> cache;

    /**
     * Constructor for in-process cache
     * 
     * @param database
     *            : data structure with information about the database
     *            connection
     * @param cacheSize
     *            : maximum number of objects allowed in cache
     */
    public DatabaseCache(Database database, long cacheSize) {
        client = database.getClient();
        db = database.getDb();
        cache = new InProcessCache<String, Object>(cacheSize);
     }

    /**
     * Constructor for remote-process cache
     * 
     * @param database
     *            : data structure with information about the database
     *            connection
     * @param cacheSize
     *            : maximum number of objects allowed in cache
     * @param host
     *            post where cache is running
     * @param port
     *            port number
     */
    public DatabaseCache(Database database, long cacheSize, String host, int port) {
        client = database.getClient();
        db = database.getDb();
        cache = new RedisCache<String, Object>(host, port);
    }

    /**
     * Constructor for remote-process cache
     * 
     * @param database
     *            : data structure with information about the database connection
     * @param cacheSize
     *            : maximum number of objects allowed in cache
     * @param host
     *            post where cache is running
     * @param port
     *            port number
     * @param timeout
     *            number of seconds before an idle connection is closed
     */
    public DatabaseCache(Database database, long cacheSize, String host, int port, int timeout) {
        client = database.getClient();
        db = database.getDb();
        cache = new RedisCache<String, Object>(host, port, timeout);
    }

    /**
     * Constructor for arbitrary cache.  Existing cache instance is passed as a parameter
     * 
     * @param database
     *            : data structure with information about the database connection
     * @param cacheInstance
     *            : cache instance which has already been created and initialized
     */
    public DatabaseCache(Database database, Cache<String, Object> cacheInstance) {
        client = database.getClient();
        db = database.getDb();
        cache = cacheInstance;
    }

    
    /**
     * put an object into the cache
     * 
     * @param <T>
     *            Object type.
     * @param id
     *            The document id.
     * @param object
     *            : object to cache
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     */
    public <T> void cachePut(String id, T object, long lifetime) {
        cache.put(id, object, Util.getTime() + lifetime);
    }

    /**
     * Return value of cached object (or null if not present)
     * 
     * @param <T>
     *            Object type.
     * @param id
     *            The document id.
     * @param classType
     *            The class of type T.
     * @return value of object
     */
    public <T> T cacheGet(Class<T> classType, String id) {
        return classType.cast(cache.get(id));
    }

    /**
     * Return an object from the cache, if present
     * 
     * @param id
     *            The document id.
     */
    public void cacheDelete(String id) {
        cache.delete(id);
    }

    /**
     * Removes all objects from the cache
     */
    public void cacheClear() {
        cache.clear();
    }

    /**
     * Returns the cache so that the application can mange it using the Cache
     * API methods
     */
    public Cache<String, Object> getCache() {
        return cache;
    }

    /**
     * Finds an Object of the specified type.
     * 
     * @param <T>
     *            Object type.
     * @param classType
     *            The class of type T.
     * @param id
     *            The document id.
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     * @throws NoDocumentException
     *             If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id, long lifetime) {
        T value = classType.cast(cache.get(id));
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id);
            cache.put(id, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * Finds an Object of the specified type.
     * 
     * @param <T>
     *            Object type.
     * @param classType
     *            The class of type T.
     * @param id
     *            The document id.
     * @param params
     *            Extra parameters to append.
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     * @throws NoDocumentException
     *             If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id, long lifetime,
            Params params) {
        assertNotEmpty(params, "params");
        T value = classType.cast(cache.get(id));
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id, params.getInternalParams());
            cache.put(id, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * This method finds any document given a URI.
     * <p>
     * The URI must be URI-encoded.
     * 
     * @param classType
     *            The class of type T.
     * @param uri
     *            The URI as string.
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     */
    public <T> T findAny(Class<T> classType, String uri, long lifetime) {
        T value = classType.cast(cache.get(uri));
        if (value != null) {
            return value;
        } else {
            value = db.findAny(classType, uri);
            cache.put(uri, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * Checks if a document exist in the database.
     * 
     * @param id
     *            The document _id field.
     * @return true If the document is found, false otherwise.
     */
    public boolean contains(String id) {
        if (cache.get(id) != null) {
            return true;
        }
        else {
            return db.contains(id);
        }
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to save
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @throws DocumentConflictException
     *             If a conflict is detected during the save.
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response save(String id, T object,
            long lifetime) {
        Response couchDbResponse = db.save(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        cache.put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to save
     * @param writeQuorum
     *            the write Quorum
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @throws DocumentConflictException
     *             If a conflict is detected during the save.
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response save(String id, T object, int writeQuorum,
            long lifetime) {
        Response couchDbResponse = client.put(getDBUri(), object, true,
                writeQuorum, client.getGson());
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        cache.put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request.
     * <p>
     * The database will be responsible for generating the document id.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to save
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response post(String id, T object,
            long lifetime) {
        Response couchDbResponse = db.post(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        cache.put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request with
     * specificied write quorum
     * <p>
     * The database will be responsible for generating the document id.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to save
     * @param writeQuorum
     *            the write Quorum
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response post(String id, T object, int writeQuorum,
            long lifetime) {
        assertNotEmpty(object, "object");
        HttpResponse response = null;
        try {
            URI uri = buildUri(getDBUri()).query("w", writeQuorum).build();
            response = client.executeRequest(createPost(uri, client.getGson()
                    .toJson(object), "application/json"));
            Response couchDbResponse = getResponse(response, Response.class,
                    client.getGson());
            com.cloudant.client.api.model.Response cloudantResponse = new com.cloudant.client.api.model.Response(
                    couchDbResponse);
            cache.put(id, object, Util.getTime() + lifetime);
            return cloudantResponse;
        } finally {
            close(response);
        }
    }

    /**
     * Saves a document with <tt>batch=ok</tt> query param.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to save.
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     */
    public <T> void batch(String id, T object, long lifetime) {
        db.batch(object);
        cache.put(id, object, Util.getTime() + lifetime);
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to update
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @throws DocumentConflictException
     *             If a conflict is detected during the update.
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response update(String id,
            T object, long lifetime) {
        Response couchDbResponse = db.update(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        cache.put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     * 
     * @param id
     *            : This method caches "object" using key "id"
     * @param object
     *            The object to update
     * @param writeQuorum
     *            the write Quorum
     * @param lifetime
     *            : lifetime of the object for the cache in milliseconds
     * @throws DocumentConflictException
     *             If a conflict is detected during the update.
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response update(String id,
            T object, int writeQuorum, long lifetime) {
        Response couchDbResponse = client.put(getDBUri(), object, false,
                writeQuorum, client.getGson());
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        cache.put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Removes a document from the database.  
     * <p>
     * The object must have the correct <code>_id</code> and <code>_rev</code>
     * values.
     * 
     * @param object
     *            The document to remove as object.
     * @param id
     *            : key identifying object to remove from cache
     * @throws NoDocumentException
     *             If the document is not found in the database.
     * @return {@link Response}
     */
    public <T> com.cloudant.client.api.model.Response remove(String id, T object) {
        cache.delete(id);
        Response couchDbResponse = db.remove(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        return response;
    }

    /**
     * @return The database URI.
     */
    public URI getDBUri() {
        return db.getDBUri();
    }

}
