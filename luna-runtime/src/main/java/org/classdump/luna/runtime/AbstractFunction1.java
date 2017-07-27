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

package org.classdump.luna.runtime;

/**
 * Abstract function of a single argument.
 */
public abstract class AbstractFunction1<T> extends LuaFunction<T, Object, Object, Object, Object> {

  @Override
  public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
    invoke(context, (T) null);
  }

  @Override
  public void invoke(ExecutionContext context, T arg1, Object arg2)
      throws ResolvedControlThrowable {
    invoke(context, arg1);
  }

  @Override
  public void invoke(ExecutionContext context, T arg1, Object arg2, Object arg3)
      throws ResolvedControlThrowable {
    invoke(context, arg1);
  }

  @Override
  public void invoke(ExecutionContext context, T arg1, Object arg2, Object arg3, Object arg4)
      throws ResolvedControlThrowable {
    invoke(context, arg1);
  }

  @Override
  public void invoke(ExecutionContext context, T arg1, Object arg2, Object arg3, Object arg4,
      Object arg5) throws ResolvedControlThrowable {
    invoke(context, arg1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
    T a = null;
    switch (args.length) {
      default:             // fall through
      case 1:
        a = (T) args[0]; // fall through
      case 0:
    }
    invoke(context, a);
  }

}
