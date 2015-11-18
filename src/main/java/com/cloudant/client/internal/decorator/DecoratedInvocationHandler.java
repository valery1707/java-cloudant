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

package com.cloudant.client.internal.decorator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DecoratedInvocationHandler<T extends DecoratorToken> implements InvocationHandler {

    private final Object instance;
    private final Decorator<T> decorator;

    DecoratedInvocationHandler(Object instance, Decorator<T> decorator) {
        this.instance = instance;
        this.decorator = decorator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        T token = decorator.preInvoke(proxy, method, args);

        if (token.getResult() != null) {
            return token.getResult();
        }

        try {
            token.setResult(method.invoke(instance, args));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        token = decorator.postInvoke(token);

        return token.getResult();
    }
}
