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

package org.classdump.luna.util;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A byte iterator over byte arrays.
 */
public class ArrayByteIterator implements ByteIterator {

  private final byte[] bytes;
  private int idx;

  /**
   * Constructs a new byte iterator of the array {@code bytes}.
   *
   * @param bytes the byte iterator to iterate over, must not be {@code null}
   * @throws NullPointerException if {@code bytes} is {@code null}
   */
  public ArrayByteIterator(byte[] bytes) {
    this.bytes = Objects.requireNonNull(bytes);
  }

  @Override
  public byte nextByte() {
    if (idx < bytes.length) {
      return bytes[idx++];
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    return idx < bytes.length;
  }

  @Override
  public Byte next() {
    return Byte.valueOf(nextByte());
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("read-only iterator");
  }

}
