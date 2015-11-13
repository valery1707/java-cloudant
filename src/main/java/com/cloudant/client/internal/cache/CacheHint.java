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

package com.cloudant.client.internal.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheHint {

    enum Operation {
        /**
         * Get the entry from the cache.
         */
        GET,
        /**
         * Put the entry in the cache.
         */
        PUT,
        /**
         * Delete the entry from the cache.
         */
        DELETE,
        /**
         * Ignore the cache for this method. This does not need to be explicitly indicated.
         */
        IGNORE
    }

    /**
     * @return the cache Operation type
     */
    Operation value();

    /**
     * Identifies the method signature paramter index of the object that is of relevance to caching.
     * <P>
     * For {@link com.cloudant.client.internal.cache.CacheHint.Operation#GET} this will be the key.
     * </P>
     * <P>
     * For {@link com.cloudant.client.internal.cache.CacheHint.Operation#PUT} this will be the
     * value.
     * </P>
     * <P>
     * For {@link com.cloudant.client.internal.cache.CacheHint.Operation#DELETE} this will be the
     * value because a revision ID is needed in addition to the key.
     * </P>
     * <P>
     * Default is 0, index of the first parameter in the method signature.
     * </P>
     *
     * @return the index of the signature parameter
     */
    int index() default 0;
}
