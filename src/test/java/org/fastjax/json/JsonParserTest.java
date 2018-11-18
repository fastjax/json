/* Copyright (c) 2018 FastJAX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.fastjax.json;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

public class JsonParserTest extends JsonReaderTest {
  private static JsonHandler newHandler(final String expected, final StringBuilder builder) {
    return new JsonHandler() {
      @Override
      public void startDocument() {
        assertEquals(0, builder.length());
      }

      @Override
      public void endDocument() {
        assertEquals(expected.trim(), builder.toString());
        builder.setLength(0);
      }

      @Override
      public boolean structural(final char ch) {
        assertTrue(JsonReader.isStructural(ch));
        builder.append(ch);
        return Math.random() < .6 ? true : false;
      }

      @Override
      public boolean characters(final char[] chars, final int start, final int end) {
        builder.append(chars, start, end - start);
        return Math.random() < .6 ? true : false;
      }

      @Override
      public boolean whitespace(final char[] chars, final int start, final int end) {
        builder.append(chars, start, end - start);
        return Math.random() < .6 ? true : false;
      }

      @Override
      public String toString() {
        return builder.toString();
      }
    };
  }

  @Override
  protected void passFile(final String jsonFileName) throws IOException, JsonParseException {
    final String json = readFile(jsonFileName);
    final JsonParser parser = new JsonParser(new JsonReader(new StringReader(json), false));
    final StringBuilder builder = new StringBuilder();
    while (!parser.parse(newHandler(json, builder)));
    assertEquals("builder length should have been set to 0 in JsonHandler#endDocument()", 0, builder.length());
  }
}