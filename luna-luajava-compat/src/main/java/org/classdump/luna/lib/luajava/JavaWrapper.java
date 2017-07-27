/*
 * Copyright 2016 Miroslav Janíček
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.classdump.luna.lib.luajava;

import org.classdump.luna.ByteString;
import org.classdump.luna.Conversions;
import org.classdump.luna.Table;
import org.classdump.luna.Userdata;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.lib.NameMetamethodValueTypeNamer;
import org.classdump.luna.runtime.AbstractFunction1;
import org.classdump.luna.runtime.AbstractUntypedFunction2;
import org.classdump.luna.runtime.ExecutionContext;
import org.classdump.luna.runtime.LuaFunction;
import org.classdump.luna.runtime.ResolvedControlThrowable;

abstract class JavaWrapper<T> extends Userdata<Object> {

  abstract String typeName();

  /**
   * Returns the wrapped object.
   *
   * @return the wrapped object
   */
  public abstract T get();

  @Override
  public Object getUserValue() {
    return null;
  }

  @Override
  public Object setUserValue(Object value) {
    throw new UnsupportedOperationException("user value not supported");
  }

  @Override
  public Table setMetatable(Table mt) {
    throw new UnsupportedOperationException("cannot wrapper metatable");
  }

  static class ToString extends AbstractFunction1<JavaWrapper> {

    public static final ToString INSTANCE = new ToString();

    @Override
    public void invoke(ExecutionContext context, JavaWrapper wrapper)
        throws ResolvedControlThrowable {
      if (wrapper != null) {
        context.getReturnBuffer().setTo(wrapper.typeName() + " (" + wrapper.get().toString() + ")");
      } else {
        throw new IllegalArgumentException(
            "invalid argument to toString: expecting Java wrapper, got "
                + NameMetamethodValueTypeNamer.typeNameOf(wrapper, context));
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException(this.getClass());
    }

  }

  static abstract class AbstractGetMemberAccessor extends AbstractUntypedFunction2 {

    protected abstract LuaFunction methodAccessorForName(String methodName);

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {

      // arg1 is ignored

      final String methodName;
      {
        ByteString s = Conversions.stringValueOf(arg2);
        if (s != null) {
          methodName = s.toString();
        } else {
          throw new IllegalArgumentException("invalid member name: expecting string, got "
              + NameMetamethodValueTypeNamer.typeNameOf(arg2, context));
        }
      }

      context.getReturnBuffer().setTo(methodAccessorForName(methodName));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException(this.getClass());
    }

  }

}
