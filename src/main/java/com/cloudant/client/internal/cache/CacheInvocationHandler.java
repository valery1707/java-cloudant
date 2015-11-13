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

import com.cloudant.client.api.Database;
import com.cloudant.client.api.cache.Cache;
import com.cloudant.client.api.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CacheInvocationHandler implements InvocationHandler {

    private final Database db;
    private final Cache<String, Object> cache;

    public CacheInvocationHandler(Database db, Cache<String, Object> cache) {
        this.db = db;
        this.cache = cache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final CacheHint cacheHint;
        InvocationCustomizer customizer = null;
        if ((cacheHint = method.getAnnotation(CacheHint.class)) != null) {
            switch (cacheHint.value()) {
                case GET:
                    customizer = new InvocationCustomizer() {
                        @Override
                        public Object preInvoke(Method method, Object[] args) {
                            return cache.get(String.class.cast(args[cacheHint.index()]));
                        }

                        @Override
                        public void postInvoke(Method method, Object[] args, Object result) {
                            //no-op
                        }

                    };
                    break;
                case PUT:
                    customizer = new InvocationCustomizer() {
                        @Override
                        public Response preInvoke(Method method, Object[] args) {
                            //no-op
                            return null;
                        }

                        @Override
                        public void postInvoke(Method method, Object[] args, Object result) {
                            Response response = invocationResultAsResponse(method, result);
                            if (response != null) {
                                cache.put(response.getId(), args[cacheHint.index()]);
                            }
                        }

                    };
                    break;
                case DELETE:
                    customizer = new InvocationCustomizer() {
                        @Override
                        public Response preInvoke(Method method, Object[] args) {
                            //no-op
                            return null;
                        }

                        @Override
                        public void postInvoke(Method method, Object[] args, Object result) {
                            Response response = invocationResultAsResponse(method, result);
                            if (response != null) {
                                cache.delete(response.getId());
                            }
                        }
                    };
                    break;
            }
        }

        if (customizer != null) {
            Object result = customizer.preInvoke(method, args);
            if (result != null) {
                return result;
            }
        }

        Object result = null;
        try {
            result = method.invoke(db, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        if (customizer != null) {
            customizer.postInvoke(method, args, result);
        }

        return result;
    }

    private Response invocationResultAsResponse(Method method, Object result) {
        if (Response.class.equals(method.getReturnType())) {
            return Response.class.cast(result);
        } else {
            return null;
        }
    }

    interface InvocationCustomizer {

        Object preInvoke(Method method, Object[] args);

        void postInvoke(Method method, Object[] args, Object result);

    }
}
