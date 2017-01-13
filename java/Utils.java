package eta.aeson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class Utils {
    
    public static String unescapeJavaScript(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            if (writer == null) {
                throw new ArithmeticException("The Writer must not be null");
            }
            int sz = str.length();
            StringBuffer unicode = new StringBuffer(4);
            boolean hadSlash = false;
            boolean inUnicode = false;
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (inUnicode) {
                    // if in unicode, then we're reading unicode
                    // values in somehow
                    unicode.append(ch);
                    if (unicode.length() == 4) {
                        // unicode now contains the four hex digits
                        // which represents our unicode character
                        try {
                            int value = Integer.parseInt(unicode.toString(), 16);
                            writer.write((char) value);
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                        } catch (NumberFormatException nfe) {
                            // throw new ArithmeticException("Unable to parse unicode value: " + unicode, nfe);
                            // throw IOException;
                            
                        }
                    }
                    continue;
                }
                if (hadSlash) {
                    // handle an escaped value
                    hadSlash = false;
                    switch (ch) {
                    case '\\':
                        writer.write('\\');
                        break;
                    case '\'':
                        writer.write('\'');
                        break;
                    case '\"':
                        writer.write('"');
                        break;
                    case 'r':
                        writer.write('\r');
                        break;
                    case 'f':
                        writer.write('\f');
                        break;
                    case 't':
                        writer.write('\t');
                        break;
                    case 'n':
                        writer.write('\n');
                        break;
                    case 'b':
                        writer.write('\b');
                        break;
                    case 'u':
                        {
                            // uh-oh, we're in unicode country....
                            inUnicode = true;
                            break;
                        }
                    default :
                        writer.write(ch);
                        break;
                    }
                    continue;
                } else if (ch == '\\') {
                    hadSlash = true;
                    continue;
                }
                writer.write(ch);
            }
            if (hadSlash) {
                // then we're in the weird case of a \ at the end of the
                // string, let's writerput it anyway.
                writer.write('\\');
            }            
            return writer.toString();
        } catch (Exception ioe) {
            // this should never ever happen while writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

}
