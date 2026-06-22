/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.helper;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.UncheckedIOException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.ConstrainableInputStream;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public final class DataUtil {
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"']*)");
    static final String defaultCharset = "UTF-8";
    private static final int firstReadBufferSize = 5120;
    static final int bufferSize = 32768;
    private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final int boundaryLength = 32;

    private DataUtil() {
    }

    public static Document load(File in, String charsetName, String baseUri) throws IOException {
        return DataUtil.parseInputStream(new FileInputStream(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return DataUtil.parseInputStream(in, charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return DataUtil.parseInputStream(in, charsetName, baseUri, parser);
    }

    static void crossStreams(InputStream in, OutputStream out) throws IOException {
        int len;
        byte[] buffer = new byte[32768];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    static Document parseInputStream(InputStream input, String charsetName, String baseUri, Parser parser) throws IOException {
        if (input == null) {
            return new Document(baseUri);
        }
        input = ConstrainableInputStream.wrap(input, 32768, 0);
        Document doc = null;
        input.mark(32768);
        ByteBuffer firstBytes = DataUtil.readToByteBuffer(input, 5119);
        boolean fullyRead = input.read() == -1;
        input.reset();
        BomCharset bomCharset = DataUtil.detectCharsetFromBom(firstBytes);
        if (bomCharset != null) {
            charsetName = bomCharset.charset;
        }
        if (charsetName == null) {
            try {
                CharBuffer defaultDecoded = Charset.forName(defaultCharset).decode(firstBytes);
                doc = defaultDecoded.hasArray() ? parser.parseInput(new CharArrayReader(defaultDecoded.array()), baseUri) : parser.parseInput(defaultDecoded.toString(), baseUri);
            }
            catch (UncheckedIOException e) {
                throw e.ioException();
            }
            Elements metaElements = doc.select("meta[http-equiv=content-type], meta[charset]");
            String foundCharset = null;
            for (Element meta : metaElements) {
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = DataUtil.getCharsetFromContentType(meta.attr("content"));
                }
                if (foundCharset == null && meta.hasAttr("charset")) {
                    foundCharset = meta.attr("charset");
                }
                if (foundCharset == null) continue;
                break;
            }
            if (foundCharset == null && doc.childNodeSize() > 0) {
                Comment comment;
                Node first = doc.childNode(0);
                XmlDeclaration decl = null;
                if (first instanceof XmlDeclaration) {
                    decl = (XmlDeclaration)first;
                } else if (first instanceof Comment && (comment = (Comment)first).isXmlDeclaration()) {
                    decl = comment.asXmlDeclaration();
                }
                if (decl != null && decl.name().equalsIgnoreCase("xml")) {
                    foundCharset = decl.attr("encoding");
                }
            }
            if ((foundCharset = DataUtil.validateCharset(foundCharset)) != null && !foundCharset.equalsIgnoreCase(defaultCharset)) {
                charsetName = foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                doc = null;
            } else if (!fullyRead) {
                doc = null;
            }
        } else {
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
        }
        if (doc == null) {
            if (charsetName == null) {
                charsetName = defaultCharset;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, charsetName), 32768);
            if (bomCharset != null && bomCharset.offset) {
                long skipped = reader.skip(1L);
                Validate.isTrue(skipped == 1L);
            }
            try {
                doc = parser.parseInput(reader, baseUri);
            }
            catch (UncheckedIOException e) {
                throw e.ioException();
            }
            Charset charset = Charset.forName(charsetName);
            doc.outputSettings().charset(charset);
            if (!charset.canEncode()) {
                doc.charset(Charset.forName(defaultCharset));
            }
        }
        input.close();
        return doc;
    }

    public static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        Validate.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
        ConstrainableInputStream input = ConstrainableInputStream.wrap(inStream, 32768, maxSize);
        return input.readToByteBuffer(maxSize);
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            return DataUtil.validateCharset(charset);
        }
        return null;
    }

    private static String validateCharset(String cs) {
        if (cs == null || cs.length() == 0) {
            return null;
        }
        cs = cs.trim().replaceAll("[\"']", "");
        try {
            if (Charset.isSupported(cs)) {
                return cs;
            }
            if (Charset.isSupported(cs = cs.toUpperCase(Locale.ENGLISH))) {
                return cs;
            }
        }
        catch (IllegalCharsetNameException illegalCharsetNameException) {
            // empty catch block
        }
        return null;
    }

    static String mimeBoundary() {
        StringBuilder mime = StringUtil.borrowBuilder();
        Random rand = new Random();
        for (int i = 0; i < 32; ++i) {
            mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
        }
        return StringUtil.releaseBuilder(mime);
    }

    private static BomCharset detectCharsetFromBom(ByteBuffer byteData) {
        ByteBuffer buffer = byteData;
        ((Buffer)buffer).mark();
        byte[] bom = new byte[4];
        if (byteData.remaining() >= bom.length) {
            byteData.get(bom);
            ((Buffer)buffer).rewind();
        }
        if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1 || bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
            return new BomCharset("UTF-32", false);
        }
        if (bom[0] == -2 && bom[1] == -1 || bom[0] == -1 && bom[1] == -2) {
            return new BomCharset("UTF-16", false);
        }
        if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
            return new BomCharset(defaultCharset, true);
        }
        return null;
    }

    private static class BomCharset {
        private final String charset;
        private final boolean offset;

        public BomCharset(String charset, boolean offset) {
            this.charset = charset;
            this.offset = offset;
        }
    }
}

