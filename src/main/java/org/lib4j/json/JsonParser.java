/* Copyright (c) 2018 lib4j
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

package org.lib4j.json;

import java.io.IOException;

import org.lib4j.util.Characters;

/**
 * Validating parser for JSON documents that asserts content conforms to the
 * <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a> specification.
 */
public class JsonParser {
  private final JsonReader reader;

  /**
   * Construct a new {@code JsonParser} for JSON content to be read from the
   * {@code reader} parameter instance.
   *
   * @param reader The {@code Reader} from which JSON is read.
   */
  public JsonParser(final JsonReader reader) {
    this.reader = reader;
  }

  /**
   * Parse the JSON document.
   *
   * @param handler The {@code JsonHandler} instance for handling content
   *          callbacks.
   * @return {@code true} if the document has been read entirely. {@code false}
   *         if parsing was aborted by a handler callback. If a handler aborts
   *         parsing, subsequent calls to {@link #parse(JsonHandler)} will resume
   *         from the position at which parsing was previously aborted.
   * @throws IOException If an I/O error occurs.
   * @throws JsonParseException If the content is not a well formed JSON term.
   * @throws NullPointerException If {@code handler} is null.
   */
  public boolean parse(final JsonHandler handler) throws IOException, JsonParseException {
    if (reader.getPosition() == 0)
      handler.startDocument();

    for (int start; (start = reader.readTokenStart()) != -1;) {
      final int end = reader.getPosition();
      final boolean abort;
      if (end - start == 1 && JsonReader.isStructural(reader.buf()[start]))
        abort = !handler.structural(reader.buf()[start]);
      else if (Characters.isWhiteSpace(reader.buf()[start]))
        abort = !handler.whitespace(reader.buf(), start, end);
      else
        abort = !handler.characters(reader.buf(), start, end);

      if (!abort)
        return abort;
    }

    handler.endDocument();
    return true;
  }
}