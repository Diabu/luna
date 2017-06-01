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

package net.sandius.rembulan;

import net.sandius.rembulan.util.ArrayByteIterator;
import net.sandius.rembulan.util.ByteIterator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * A byte string backed by a byte array.
 */
class ArrayByteString extends ByteString {

	static final ArrayByteString EMPTY_INSTANCE = new ArrayByteString(new byte[0]);

	private final byte[] bytes;
	private int hashCode;

	ArrayByteString(byte[] bytes) {
		this.bytes = Objects.requireNonNull(bytes);
	}

	@Override
	protected boolean equalsByteString(ByteString that) {
		if (this.length() != that.length()) return false;

		int len = this.length();
		for (int i = 0; i < len; i++) {
			if (this.byteAt(i) != that.byteAt(i)) return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hc = hashCode;
		if (hc == 0) {
			if (bytes.length > 0) {
				for (byte b : bytes) {
					hc = (hc * 31) + (b & 0xff);
				}
				hashCode = hc;
			}
		}

		return hc;
	}

	@Override
	int maybeHashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return decode();
	}

	@Override
	public String toRawString() {
		char[] chars = new char[bytes.length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}
		return String.valueOf(chars);
	}

	@Override
	public int length() {
		return bytes.length;
	}

	@Override
	int maybeLength() {
		return bytes.length;
	}

	@Override
	public boolean isEmpty() {
		return bytes.length == 0;
	}

	@Override
	public byte byteAt(int index) {
		return bytes[index];
	}

	@Override
	public ByteIterator byteIterator() {
		return new ArrayByteIterator(bytes);
	}

	@Override
	public InputStream asInputStream() {
		// no need to go via the iterator
		return new ByteArrayInputStream(bytes);
	}

	private static void checkSubstringBounds(int start, int end, int len) {
		if (start > end) throw new IndexOutOfBoundsException("start > end (" + start + " > " + end + ")");
		else if (start < 0) throw new IndexOutOfBoundsException("start < 0 (" + start + " < 0)");
		else if (end < 0) throw new IndexOutOfBoundsException("end < 0 (" + end + " < 0)");
		else if (end > len) throw new IndexOutOfBoundsException("end > length (" + start + " > " + len + ")");
	}

	@Override
	public ByteString substring(int start, int end) {
		checkSubstringBounds(start, end, bytes.length);
		return new ArrayByteString(Arrays.copyOfRange(bytes, start, end));
	}

	@Override
	public byte[] getBytes() {
		return Arrays.copyOf(bytes, bytes.length);
	}

	@Override
	public void putTo(ByteBuffer buffer) {
		buffer.put(bytes);
	}

	@Override
	public void writeTo(OutputStream stream) throws IOException {
		// must make a defensive copy to avoid leaking the contents
		stream.write(getBytes());
	}

	@Override
	public boolean startsWith(byte b) {
		return bytes.length > 0 && bytes[0] == b;
	}

}
