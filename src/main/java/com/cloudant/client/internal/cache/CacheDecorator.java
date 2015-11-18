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

import com.cloudant.client.api.cache.Cache;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.internal.decorator.Decorator;

import java.lang.reflect.Method;

public class CacheDecorator implements Decorator<CacheDecoratorToken> {

    private final Cache<String, Object> cache;

    public CacheDecorator(Cache<String, Object> cache) {
        this.cache = cache;
    }

    @Override
    public CacheDecoratorToken preInvoke(Object proxy, Method method, Object[] args) {
        CacheHint cacheHint;
        CacheDecoratorToken token;
        if ((cacheHint = method.getAnnotation(CacheHint.class)) != null) {
            token = new CacheDecoratorToken(proxy, method, args, cacheHint.value(),
                    args[cacheHint.index()]);
        } else {
            token = new CacheDecoratorToken(proxy, method, args, CacheHint.Operation.IGNORE, null);
        }

        if (token.operation == CacheHint.Operation.GET) {
            token.setResult(cache.get((String) token.element));
        }

        return token;
    }


    @Override
    public CacheDecoratorToken postInvoke(CacheDecoratorToken token) throws Exception {
        if (token.operation == CacheHint.Operation.PUT || token.operation == CacheHint.Operation
                .DELETE) {
            //cast the result to a Response
            if (Response.class.equals(token.method.getReturnType())) {
                Response response = Response.class.cast(token.getResult());
                switch (token.operation) {
                    case PUT:
                        cache.put(response.getId(), token.element);
                        break;
                    case DELETE:
                        cache.delete(response.getId());
                        break;
                    default:
                        break;
                }
            }
        }
        return token;
    }
}
