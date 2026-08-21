/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.messages;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Reads Java-style properties without the ISO-8859-1 behavior of Properties.load(InputStream). */
final class Utf8Properties {

	private Utf8Properties() {
	}

	static Map<String, String> load(FileHandle file) {
		try (Reader reader = new InputStreamReader(file.read(), StandardCharsets.UTF_8.newDecoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT))) {
			return load(reader);
		} catch (IOException e) {
			throw new GdxRuntimeException("Unable to read UTF-8 properties file: " + file.path(), e);
		}
	}

	static Map<String, String> load(Reader input) throws IOException {
		BufferedReader reader = input instanceof BufferedReader
				? (BufferedReader) input
				: new BufferedReader(input);
		Map<String, String> result = new LinkedHashMap<>();
		StringBuilder logicalLine = new StringBuilder();
		boolean continued = false;
		boolean firstLine = true;
		String line;

		while ((line = reader.readLine()) != null) {
			if (firstLine) {
				firstLine = false;
				if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
					line = line.substring(1);
				}
			}

			if (!continued) {
				logicalLine.setLength(0);
			} else {
				line = line.substring(skipWhitespace(line, 0));
			}
			logicalLine.append(line);

			if (hasContinuation(logicalLine)) {
				logicalLine.setLength(logicalLine.length() - 1);
				continued = true;
			} else {
				parseLine(logicalLine, result);
				continued = false;
			}
		}

		if (continued) {
			parseLine(logicalLine, result);
		}
		return result;
	}

	private static boolean hasContinuation(CharSequence line) {
		int slashCount = 0;
		for (int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; i--) {
			slashCount++;
		}
		return (slashCount & 1) == 1;
	}

	private static void parseLine(CharSequence line, Map<String, String> result) {
		int length = line.length();
		int keyStart = skipWhitespace(line, 0);
		if (keyStart == length || line.charAt(keyStart) == '#' || line.charAt(keyStart) == '!') {
			return;
		}

		int keyEnd = length;
		boolean escaped = false;
		for (int i = keyStart; i < length; i++) {
			char c = line.charAt(i);
			if (!escaped && (c == '=' || c == ':' || isWhitespace(c))) {
				keyEnd = i;
				break;
			}
			if (c == '\\') {
				escaped = !escaped;
			} else {
				escaped = false;
			}
		}

		int valueStart = keyEnd;
		if (valueStart < length) {
			if (isWhitespace(line.charAt(valueStart))) {
				valueStart = skipWhitespace(line, valueStart);
				if (valueStart < length && (line.charAt(valueStart) == '=' || line.charAt(valueStart) == ':')) {
					valueStart++;
				}
			} else {
				valueStart++;
			}
			valueStart = skipWhitespace(line, valueStart);
		}

		result.put(unescape(line, keyStart, keyEnd), unescape(line, valueStart, length));
	}

	private static int skipWhitespace(CharSequence value, int start) {
		while (start < value.length() && isWhitespace(value.charAt(start))) {
			start++;
		}
		return start;
	}

	private static boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\f';
	}

	private static String unescape(CharSequence value, int start, int end) {
		StringBuilder result = new StringBuilder(end - start);
		for (int i = start; i < end; i++) {
			char c = value.charAt(i);
			if (c != '\\' || i + 1 >= end) {
				result.append(c);
				continue;
			}

			char escaped = value.charAt(++i);
			switch (escaped) {
				case 't':
					result.append('\t');
					break;
				case 'n':
					result.append('\n');
					break;
				case 'r':
					result.append('\r');
					break;
				case 'f':
					result.append('\f');
					break;
				case 'u':
					if (i + 4 >= end) {
						throw malformedUnicode(value, start, end);
					}
					int codePoint = 0;
					for (int digit = 0; digit < 4; digit++) {
						int hex = Character.digit(value.charAt(++i), 16);
						if (hex < 0) {
							throw malformedUnicode(value, start, end);
						}
						codePoint = (codePoint << 4) | hex;
					}
					result.append((char) codePoint);
					break;
				default:
					result.append(escaped);
			}
		}
		return result.toString();
	}

	private static IllegalArgumentException malformedUnicode(CharSequence value, int start, int end) {
		return new IllegalArgumentException("Malformed Unicode escape in properties value: "
				+ value.subSequence(start, end));
	}
}
