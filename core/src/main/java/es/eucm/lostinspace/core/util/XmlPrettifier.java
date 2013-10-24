
package es.eucm.lostinspace.core.util;

import java.util.Stack;

/** @author mfreire */
public class XmlPrettifier {

	private int maxCols = 20;
	private final String indentString = "  ";

	private boolean inTag = false;
	private int pos = 0;
	private int lastCol = 0;
	private String text;
	private Stack<String> tagStack = new Stack<String>();
	private StringBuilder pretty = new StringBuilder();

	public XmlPrettifier (int maxCols) {
		this.maxCols = maxCols;
		pretty = new StringBuilder();
	}

	public String getPrettyXml (String text) {
		this.text = text.trim();
		pos = 0;
		lastCol = 0;
		tagStack.clear();
		inTag = false;
		pretty.setLength(0);
		parse();
		return pretty.toString();
	}

	public char look () {
		return text.charAt(pos);
	}

	public void skipWhite () {
		while (isWhitespace(look()))
			pos++;
	}

	public void write (char c) {
		if (isWhitespace(c)) {
			if (lastCol > maxCols || c == '\n') {
				lastCol = 0;
				pretty.append("\n");
				for (int i = (inTag) ? -1 : 0; i < tagStack.size(); i++) {
					pretty.append(indentString);
					lastCol += indentString.length();
				}
			} else {
				pretty.append(c);
			}
		} else {
			pretty.append(c);
			lastCol++;
		}
	}

	public String writeText () {
		StringBuilder written = new StringBuilder();
		for (char c = look(); Character.isLetterOrDigit(c); c = look()) {
			written.append(c);
			write(c);
			pos++;
		}
		return written.toString();
	}

	public void write () {
		write(look());
		pos++;
	}

	public void removeLastIndent () {
		int prev = pretty.lastIndexOf(indentString);
		pretty.replace(prev, prev + indentString.length(), "");
	}

	public void parse () {
		while (pos < text.length()) {
			skipWhite();
			assert (look() == '<');
			write();
			skipWhite();

			if (look() == '/') {
				// end-tag
				write();
				skipWhite();
				String endTag = writeText();
				String lastTag = tagStack.pop();
				assert (lastTag.equals(endTag));
				skipWhite();
				assert look() == '>';
				write();
				removeLastIndent();
				write('\n');
				continue;
			}

			tagStack.push(writeText()); // push tag
			skipWhite();
			inTag = true;
			while (Character.isLetterOrDigit(look())) {
				write(' ');
				// read all attributes
				writeText(); // attribute name
				skipWhite();
				assert (look() == '=');
				write();
				skipWhite();
				assert (look() == '\"');
				write();
				writeText();
				assert (look() == '\"');
				write();
				skipWhite();
			}

			if (look() == '/') {
				// self-closed tag
				write();
				skipWhite();
				assert (look() == '>');
				write();
				tagStack.pop();
				inTag = false;
				write('\n');
			} else if (look() == '>') {
				// tag with contents
				write();
				skipWhite();
				inTag = false;
				write('\n');
			}
		}
	}

	boolean isWhitespace (char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
}
