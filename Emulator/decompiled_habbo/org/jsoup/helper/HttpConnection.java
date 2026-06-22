/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.helper;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.UncheckedIOException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.DataUtil;
import org.jsoup.helper.Validate;
import org.jsoup.internal.ConstrainableInputStream;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

public class HttpConnection
implements Connection {
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
    private static final String USER_AGENT = "User-Agent";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final int HTTP_TEMP_REDIR = 307;
    private static final String DefaultUploadType = "application/octet-stream";
    private Connection.Request req = new Request();
    private Connection.Response res = new Response();

    public static Connection connect(String url) {
        HttpConnection con = new HttpConnection();
        con.url(url);
        return con;
    }

    public static Connection connect(URL url) {
        HttpConnection con = new HttpConnection();
        con.url(url);
        return con;
    }

    private static String encodeUrl(String url) {
        try {
            URL u = new URL(url);
            return HttpConnection.encodeUrl(u).toExternalForm();
        }
        catch (Exception e) {
            return url;
        }
    }

    static URL encodeUrl(URL u) {
        try {
            String urlS = u.toExternalForm();
            urlS = urlS.replace(" ", "%20");
            URI uri = new URI(urlS);
            return new URL(uri.toASCIIString());
        }
        catch (MalformedURLException | URISyntaxException e) {
            return u;
        }
    }

    private static String encodeMimeName(String val) {
        if (val == null) {
            return null;
        }
        return val.replace("\"", "%22");
    }

    @Override
    public Connection url(URL url) {
        this.req.url(url);
        return this;
    }

    @Override
    public Connection url(String url) {
        Validate.notEmpty(url, "Must supply a valid URL");
        try {
            this.req.url(new URL(HttpConnection.encodeUrl(url)));
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return this;
    }

    @Override
    public Connection proxy(Proxy proxy) {
        this.req.proxy(proxy);
        return this;
    }

    @Override
    public Connection proxy(String host, int port) {
        this.req.proxy(host, port);
        return this;
    }

    @Override
    public Connection userAgent(String userAgent) {
        Validate.notNull(userAgent, "User agent must not be null");
        this.req.header(USER_AGENT, userAgent);
        return this;
    }

    @Override
    public Connection timeout(int millis) {
        this.req.timeout(millis);
        return this;
    }

    @Override
    public Connection maxBodySize(int bytes) {
        this.req.maxBodySize(bytes);
        return this;
    }

    @Override
    public Connection followRedirects(boolean followRedirects) {
        this.req.followRedirects(followRedirects);
        return this;
    }

    @Override
    public Connection referrer(String referrer) {
        Validate.notNull(referrer, "Referrer must not be null");
        this.req.header("Referer", referrer);
        return this;
    }

    @Override
    public Connection method(Connection.Method method) {
        this.req.method(method);
        return this;
    }

    @Override
    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.req.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    @Override
    public Connection ignoreContentType(boolean ignoreContentType) {
        this.req.ignoreContentType(ignoreContentType);
        return this;
    }

    @Override
    public Connection data(String key, String value) {
        this.req.data(KeyVal.create(key, value));
        return this;
    }

    @Override
    public Connection sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.req.sslSocketFactory(sslSocketFactory);
        return this;
    }

    @Override
    public Connection data(String key, String filename, InputStream inputStream) {
        this.req.data(KeyVal.create(key, filename, inputStream));
        return this;
    }

    @Override
    public Connection data(String key, String filename, InputStream inputStream, String contentType) {
        this.req.data(KeyVal.create(key, filename, inputStream).contentType(contentType));
        return this;
    }

    @Override
    public Connection data(Map<String, String> data) {
        Validate.notNull(data, "Data map must not be null");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            this.req.data(KeyVal.create(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    @Override
    public Connection data(String ... keyvals) {
        Validate.notNull(keyvals, "Data key value pairs must not be null");
        Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");
        for (int i = 0; i < keyvals.length; i += 2) {
            String key = keyvals[i];
            String value = keyvals[i + 1];
            Validate.notEmpty(key, "Data key must not be empty");
            Validate.notNull(value, "Data value must not be null");
            this.req.data(KeyVal.create(key, value));
        }
        return this;
    }

    @Override
    public Connection data(Collection<Connection.KeyVal> data) {
        Validate.notNull(data, "Data collection must not be null");
        for (Connection.KeyVal entry : data) {
            this.req.data(entry);
        }
        return this;
    }

    @Override
    public Connection.KeyVal data(String key) {
        Validate.notEmpty(key, "Data key must not be empty");
        for (Connection.KeyVal keyVal : this.request().data()) {
            if (!keyVal.key().equals(key)) continue;
            return keyVal;
        }
        return null;
    }

    @Override
    public Connection requestBody(String body) {
        this.req.requestBody(body);
        return this;
    }

    @Override
    public Connection header(String name, String value) {
        this.req.header(name, value);
        return this;
    }

    @Override
    public Connection headers(Map<String, String> headers) {
        Validate.notNull(headers, "Header map must not be null");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            this.req.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Connection cookie(String name, String value) {
        this.req.cookie(name, value);
        return this;
    }

    @Override
    public Connection cookies(Map<String, String> cookies) {
        Validate.notNull(cookies, "Cookie map must not be null");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.req.cookie(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Connection parser(Parser parser) {
        this.req.parser(parser);
        return this;
    }

    @Override
    public Document get() throws IOException {
        this.req.method(Connection.Method.GET);
        this.execute();
        return this.res.parse();
    }

    @Override
    public Document post() throws IOException {
        this.req.method(Connection.Method.POST);
        this.execute();
        return this.res.parse();
    }

    @Override
    public Connection.Response execute() throws IOException {
        this.res = Response.execute(this.req);
        return this.res;
    }

    @Override
    public Connection.Request request() {
        return this.req;
    }

    @Override
    public Connection request(Connection.Request request) {
        this.req = request;
        return this;
    }

    @Override
    public Connection.Response response() {
        return this.res;
    }

    @Override
    public Connection response(Connection.Response response) {
        this.res = response;
        return this;
    }

    @Override
    public Connection postDataCharset(String charset) {
        this.req.postDataCharset(charset);
        return this;
    }

    private static boolean needsMultipart(Connection.Request req) {
        for (Connection.KeyVal keyVal : req.data()) {
            if (!keyVal.hasInputStream()) continue;
            return true;
        }
        return false;
    }

    public static class Request
    extends Base<Connection.Request>
    implements Connection.Request {
        private Proxy proxy;
        private int timeoutMilliseconds = 30000;
        private int maxBodySizeBytes = 0x200000;
        private boolean followRedirects = true;
        private Collection<Connection.KeyVal> data = new ArrayList<Connection.KeyVal>();
        private String body = null;
        private boolean ignoreHttpErrors = false;
        private boolean ignoreContentType = false;
        private Parser parser;
        private boolean parserDefined = false;
        private String postDataCharset = "UTF-8";
        private SSLSocketFactory sslSocketFactory;

        Request() {
            this.method = Connection.Method.GET;
            this.addHeader("Accept-Encoding", "gzip");
            this.addHeader(HttpConnection.USER_AGENT, HttpConnection.DEFAULT_UA);
            this.parser = Parser.htmlParser();
        }

        @Override
        public Proxy proxy() {
            return this.proxy;
        }

        @Override
        public Request proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        @Override
        public Request proxy(String host, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
            return this;
        }

        @Override
        public int timeout() {
            return this.timeoutMilliseconds;
        }

        @Override
        public Request timeout(int millis) {
            Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            this.timeoutMilliseconds = millis;
            return this;
        }

        @Override
        public int maxBodySize() {
            return this.maxBodySizeBytes;
        }

        @Override
        public Connection.Request maxBodySize(int bytes) {
            Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
            this.maxBodySizeBytes = bytes;
            return this;
        }

        @Override
        public boolean followRedirects() {
            return this.followRedirects;
        }

        @Override
        public Connection.Request followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        @Override
        public boolean ignoreHttpErrors() {
            return this.ignoreHttpErrors;
        }

        @Override
        public SSLSocketFactory sslSocketFactory() {
            return this.sslSocketFactory;
        }

        @Override
        public void sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
        }

        @Override
        public Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
            this.ignoreHttpErrors = ignoreHttpErrors;
            return this;
        }

        @Override
        public boolean ignoreContentType() {
            return this.ignoreContentType;
        }

        @Override
        public Connection.Request ignoreContentType(boolean ignoreContentType) {
            this.ignoreContentType = ignoreContentType;
            return this;
        }

        @Override
        public Request data(Connection.KeyVal keyval) {
            Validate.notNull(keyval, "Key val must not be null");
            this.data.add(keyval);
            return this;
        }

        @Override
        public Collection<Connection.KeyVal> data() {
            return this.data;
        }

        @Override
        public Connection.Request requestBody(String body) {
            this.body = body;
            return this;
        }

        @Override
        public String requestBody() {
            return this.body;
        }

        @Override
        public Request parser(Parser parser) {
            this.parser = parser;
            this.parserDefined = true;
            return this;
        }

        @Override
        public Parser parser() {
            return this.parser;
        }

        @Override
        public Connection.Request postDataCharset(String charset) {
            Validate.notNull(charset, "Charset must not be null");
            if (!Charset.isSupported(charset)) {
                throw new IllegalCharsetNameException(charset);
            }
            this.postDataCharset = charset;
            return this;
        }

        @Override
        public String postDataCharset() {
            return this.postDataCharset;
        }
    }

    public static class Response
    extends Base<Connection.Response>
    implements Connection.Response {
        private static final int MAX_REDIRECTS = 20;
        private static final String LOCATION = "Location";
        private int statusCode;
        private String statusMessage;
        private ByteBuffer byteData;
        private InputStream bodyStream;
        private HttpURLConnection conn;
        private String charset;
        private String contentType;
        private boolean executed = false;
        private boolean inputStreamRead = false;
        private int numRedirects = 0;
        private Connection.Request req;
        private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");

        Response() {
        }

        private Response(Response previousResponse) throws IOException {
            if (previousResponse != null) {
                this.numRedirects = previousResponse.numRedirects + 1;
                if (this.numRedirects >= 20) {
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", previousResponse.url()));
                }
            }
        }

        static Response execute(Connection.Request req) throws IOException {
            return Response.execute(req, null);
        }

        static Response execute(Connection.Request req, Response previousResponse) throws IOException {
            boolean hasRequestBody;
            Validate.notNull(req, "Request must not be null");
            Validate.notNull(req.url(), "URL must be specified to connect");
            String protocol = req.url().getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new MalformedURLException("Only http & https protocols supported");
            }
            boolean methodHasBody = req.method().hasBody();
            boolean bl = hasRequestBody = req.requestBody() != null;
            if (!methodHasBody) {
                Validate.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + (Object)((Object)req.method()));
            }
            String mimeBoundary = null;
            if (req.data().size() > 0 && (!methodHasBody || hasRequestBody)) {
                Response.serialiseRequestUrl(req);
            } else if (methodHasBody) {
                mimeBoundary = Response.setOutputContentType(req);
            }
            long startTime = System.nanoTime();
            HttpURLConnection conn = Response.createConnection(req);
            Response res = null;
            try {
                conn.connect();
                if (conn.getDoOutput()) {
                    Response.writePost(req, conn.getOutputStream(), mimeBoundary);
                }
                int status = conn.getResponseCode();
                res = new Response(previousResponse);
                res.setupFromConnection(conn, previousResponse);
                res.req = req;
                if (res.hasHeader(LOCATION) && req.followRedirects()) {
                    String location;
                    if (status != 307) {
                        req.method(Connection.Method.GET);
                        req.data().clear();
                        req.requestBody(null);
                        req.removeHeader(HttpConnection.CONTENT_TYPE);
                    }
                    if ((location = res.header(LOCATION)).startsWith("http:/") && location.charAt(6) != '/') {
                        location = location.substring(6);
                    }
                    URL redir = StringUtil.resolve(req.url(), location);
                    req.url(HttpConnection.encodeUrl(redir));
                    for (Map.Entry cookie : res.cookies.entrySet()) {
                        req.cookie((String)cookie.getKey(), (String)cookie.getValue());
                    }
                    return Response.execute(req, res);
                }
                if (!(status >= 200 && status < 400 || req.ignoreHttpErrors())) {
                    throw new HttpStatusException("HTTP error fetching URL", status, req.url().toString());
                }
                String contentType = res.contentType();
                if (!(contentType == null || req.ignoreContentType() || contentType.startsWith("text/") || xmlContentTypeRxp.matcher(contentType).matches())) {
                    throw new UnsupportedMimeTypeException("Unhandled content type. Must be text/*, application/xml, or application/*+xml", contentType, req.url().toString());
                }
                if (contentType != null && xmlContentTypeRxp.matcher(contentType).matches() && req instanceof Request && !((Request)req).parserDefined) {
                    req.parser(Parser.xmlParser());
                }
                res.charset = DataUtil.getCharsetFromContentType(res.contentType);
                if (conn.getContentLength() != 0 && req.method() != Connection.Method.HEAD) {
                    res.bodyStream = null;
                    InputStream inputStream = res.bodyStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
                    if (res.hasHeaderWithValue(HttpConnection.CONTENT_ENCODING, "gzip")) {
                        res.bodyStream = new GZIPInputStream(res.bodyStream);
                    } else if (res.hasHeaderWithValue(HttpConnection.CONTENT_ENCODING, "deflate")) {
                        res.bodyStream = new InflaterInputStream(res.bodyStream, new Inflater(true));
                    }
                    res.bodyStream = ConstrainableInputStream.wrap(res.bodyStream, 32768, req.maxBodySize()).timeout(startTime, req.timeout());
                } else {
                    res.byteData = DataUtil.emptyByteBuffer();
                }
            }
            catch (IOException e) {
                if (res != null) {
                    super.safeClose();
                }
                throw e;
            }
            res.executed = true;
            return res;
        }

        @Override
        public int statusCode() {
            return this.statusCode;
        }

        @Override
        public String statusMessage() {
            return this.statusMessage;
        }

        @Override
        public String charset() {
            return this.charset;
        }

        @Override
        public Response charset(String charset) {
            this.charset = charset;
            return this;
        }

        @Override
        public String contentType() {
            return this.contentType;
        }

        @Override
        public Document parse() throws IOException {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
            if (this.byteData != null) {
                this.bodyStream = new ByteArrayInputStream(this.byteData.array());
                this.inputStreamRead = false;
            }
            Validate.isFalse(this.inputStreamRead, "Input stream already read and parsed, cannot re-read.");
            Document doc = DataUtil.parseInputStream(this.bodyStream, this.charset, this.url.toExternalForm(), this.req.parser());
            this.charset = doc.outputSettings().charset().name();
            this.inputStreamRead = true;
            this.safeClose();
            return doc;
        }

        private void prepareByteData() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            if (this.byteData == null) {
                Validate.isFalse(this.inputStreamRead, "Request has already been read (with .parse())");
                try {
                    this.byteData = DataUtil.readToByteBuffer(this.bodyStream, this.req.maxBodySize());
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                finally {
                    this.inputStreamRead = true;
                    this.safeClose();
                }
            }
        }

        @Override
        public String body() {
            this.prepareByteData();
            String body = this.charset == null ? Charset.forName("UTF-8").decode(this.byteData).toString() : Charset.forName(this.charset).decode(this.byteData).toString();
            ((Buffer)this.byteData).rewind();
            return body;
        }

        @Override
        public byte[] bodyAsBytes() {
            this.prepareByteData();
            return this.byteData.array();
        }

        @Override
        public Connection.Response bufferUp() {
            this.prepareByteData();
            return this;
        }

        @Override
        public BufferedInputStream bodyStream() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            Validate.isFalse(this.inputStreamRead, "Request has already been read");
            this.inputStreamRead = true;
            return ConstrainableInputStream.wrap(this.bodyStream, 32768, this.req.maxBodySize());
        }

        private static HttpURLConnection createConnection(Connection.Request req) throws IOException {
            HttpURLConnection conn = (HttpURLConnection)(req.proxy() == null ? req.url().openConnection() : req.url().openConnection(req.proxy()));
            conn.setRequestMethod(req.method().name());
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(req.timeout());
            conn.setReadTimeout(req.timeout() / 2);
            if (req.sslSocketFactory() != null && conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection)conn).setSSLSocketFactory(req.sslSocketFactory());
            }
            if (req.method().hasBody()) {
                conn.setDoOutput(true);
            }
            if (req.cookies().size() > 0) {
                conn.addRequestProperty("Cookie", Response.getRequestCookieString(req));
            }
            for (Map.Entry<String, List<String>> header : req.multiHeaders().entrySet()) {
                for (String value : header.getValue()) {
                    conn.addRequestProperty(header.getKey(), value);
                }
            }
            return conn;
        }

        private void safeClose() {
            if (this.bodyStream != null) {
                try {
                    this.bodyStream.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    this.bodyStream = null;
                }
            }
            if (this.conn != null) {
                this.conn.disconnect();
                this.conn = null;
            }
        }

        private void setupFromConnection(HttpURLConnection conn, Response previousResponse) throws IOException {
            this.conn = conn;
            this.method = Connection.Method.valueOf(conn.getRequestMethod());
            this.url = conn.getURL();
            this.statusCode = conn.getResponseCode();
            this.statusMessage = conn.getResponseMessage();
            this.contentType = conn.getContentType();
            LinkedHashMap<String, List<String>> resHeaders = Response.createHeaderMap(conn);
            this.processResponseHeaders(resHeaders);
            if (previousResponse != null) {
                for (Map.Entry prevCookie : previousResponse.cookies().entrySet()) {
                    if (this.hasCookie((String)prevCookie.getKey())) continue;
                    this.cookie((String)prevCookie.getKey(), (String)prevCookie.getValue());
                }
                previousResponse.safeClose();
            }
        }

        private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection conn) {
            LinkedHashMap<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
            int i = 0;
            while (true) {
                String key = conn.getHeaderFieldKey(i);
                String val = conn.getHeaderField(i);
                if (key == null && val == null) break;
                ++i;
                if (key == null || val == null) continue;
                if (headers.containsKey(key)) {
                    headers.get(key).add(val);
                    continue;
                }
                ArrayList<String> vals = new ArrayList<String>();
                vals.add(val);
                headers.put(key, vals);
            }
            return headers;
        }

        void processResponseHeaders(Map<String, List<String>> resHeaders) {
            for (Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
                String name = entry.getKey();
                if (name == null) continue;
                List<String> values = entry.getValue();
                if (name.equalsIgnoreCase("Set-Cookie")) {
                    for (String value : values) {
                        if (value == null) continue;
                        TokenQueue cd = new TokenQueue(value);
                        String cookieName = cd.chompTo("=").trim();
                        String cookieVal = cd.consumeTo(";").trim();
                        if (cookieName.length() <= 0) continue;
                        this.cookie(cookieName, cookieVal);
                    }
                }
                for (String value : values) {
                    this.addHeader(name, value);
                }
            }
        }

        private static String setOutputContentType(Connection.Request req) {
            String bound = null;
            if (req.hasHeader(HttpConnection.CONTENT_TYPE)) {
                if (req.header(HttpConnection.CONTENT_TYPE).contains(HttpConnection.MULTIPART_FORM_DATA) && !req.header(HttpConnection.CONTENT_TYPE).contains("boundary")) {
                    bound = DataUtil.mimeBoundary();
                    req.header(HttpConnection.CONTENT_TYPE, "multipart/form-data; boundary=" + bound);
                }
            } else if (HttpConnection.needsMultipart(req)) {
                bound = DataUtil.mimeBoundary();
                req.header(HttpConnection.CONTENT_TYPE, "multipart/form-data; boundary=" + bound);
            } else {
                req.header(HttpConnection.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + req.postDataCharset());
            }
            return bound;
        }

        private static void writePost(Connection.Request req, OutputStream outputStream, String bound) throws IOException {
            Collection<Connection.KeyVal> data = req.data();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.postDataCharset()));
            if (bound != null) {
                for (Connection.KeyVal keyVal : data) {
                    w.write("--");
                    w.write(bound);
                    w.write("\r\n");
                    w.write("Content-Disposition: form-data; name=\"");
                    w.write(HttpConnection.encodeMimeName(keyVal.key()));
                    w.write("\"");
                    if (keyVal.hasInputStream()) {
                        w.write("; filename=\"");
                        w.write(HttpConnection.encodeMimeName(keyVal.value()));
                        w.write("\"\r\nContent-Type: ");
                        w.write(keyVal.contentType() != null ? keyVal.contentType() : HttpConnection.DefaultUploadType);
                        w.write("\r\n\r\n");
                        w.flush();
                        DataUtil.crossStreams(keyVal.inputStream(), outputStream);
                        outputStream.flush();
                    } else {
                        w.write("\r\n\r\n");
                        w.write(keyVal.value());
                    }
                    w.write("\r\n");
                }
                w.write("--");
                w.write(bound);
                w.write("--");
            } else if (req.requestBody() != null) {
                w.write(req.requestBody());
            } else {
                boolean first = true;
                for (Connection.KeyVal keyVal : data) {
                    if (!first) {
                        w.append('&');
                    } else {
                        first = false;
                    }
                    w.write(URLEncoder.encode(keyVal.key(), req.postDataCharset()));
                    w.write(61);
                    w.write(URLEncoder.encode(keyVal.value(), req.postDataCharset()));
                }
            }
            w.close();
        }

        private static String getRequestCookieString(Connection.Request req) {
            StringBuilder sb = StringUtil.borrowBuilder();
            boolean first = true;
            for (Map.Entry<String, String> cookie : req.cookies().entrySet()) {
                if (!first) {
                    sb.append("; ");
                } else {
                    first = false;
                }
                sb.append(cookie.getKey()).append('=').append(cookie.getValue());
            }
            return StringUtil.releaseBuilder(sb);
        }

        private static void serialiseRequestUrl(Connection.Request req) throws IOException {
            URL in = req.url();
            StringBuilder url = StringUtil.borrowBuilder();
            boolean first = true;
            url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
            if (in.getQuery() != null) {
                url.append(in.getQuery());
                first = false;
            }
            for (Connection.KeyVal keyVal : req.data()) {
                Validate.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
                if (!first) {
                    url.append('&');
                } else {
                    first = false;
                }
                url.append(URLEncoder.encode(keyVal.key(), "UTF-8")).append('=').append(URLEncoder.encode(keyVal.value(), "UTF-8"));
            }
            req.url(new URL(StringUtil.releaseBuilder(url)));
            req.data().clear();
        }
    }

    public static class KeyVal
    implements Connection.KeyVal {
        private String key;
        private String value;
        private InputStream stream;
        private String contentType;

        public static KeyVal create(String key, String value) {
            return new KeyVal().key(key).value(value);
        }

        public static KeyVal create(String key, String filename, InputStream stream) {
            return new KeyVal().key(key).value(filename).inputStream(stream);
        }

        private KeyVal() {
        }

        @Override
        public KeyVal key(String key) {
            Validate.notEmpty(key, "Data key must not be empty");
            this.key = key;
            return this;
        }

        @Override
        public String key() {
            return this.key;
        }

        @Override
        public KeyVal value(String value) {
            Validate.notNull(value, "Data value must not be null");
            this.value = value;
            return this;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public KeyVal inputStream(InputStream inputStream) {
            Validate.notNull(this.value, "Data input stream must not be null");
            this.stream = inputStream;
            return this;
        }

        @Override
        public InputStream inputStream() {
            return this.stream;
        }

        @Override
        public boolean hasInputStream() {
            return this.stream != null;
        }

        @Override
        public Connection.KeyVal contentType(String contentType) {
            Validate.notEmpty(contentType);
            this.contentType = contentType;
            return this;
        }

        @Override
        public String contentType() {
            return this.contentType;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static abstract class Base<T extends Connection.Base>
    implements Connection.Base<T> {
        URL url;
        Connection.Method method;
        Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        Map<String, String> cookies = new LinkedHashMap<String, String>();

        private Base() {
        }

        @Override
        public URL url() {
            return this.url;
        }

        @Override
        public T url(URL url) {
            Validate.notNull(url, "URL must not be null");
            this.url = url;
            return (T)this;
        }

        @Override
        public Connection.Method method() {
            return this.method;
        }

        @Override
        public T method(Connection.Method method) {
            Validate.notNull((Object)method, "Method must not be null");
            this.method = method;
            return (T)this;
        }

        @Override
        public String header(String name) {
            Validate.notNull(name, "Header name must not be null");
            List<String> vals = this.getHeadersCaseInsensitive(name);
            if (vals.size() > 0) {
                return StringUtil.join(vals, ", ");
            }
            return null;
        }

        @Override
        public T addHeader(String name, String value) {
            Validate.notEmpty(name);
            value = value == null ? "" : value;
            List<String> values = this.headers(name);
            if (values.isEmpty()) {
                values = new ArrayList<String>();
                this.headers.put(name, values);
            }
            values.add(Base.fixHeaderEncoding(value));
            return (T)this;
        }

        @Override
        public List<String> headers(String name) {
            Validate.notEmpty(name);
            return this.getHeadersCaseInsensitive(name);
        }

        private static String fixHeaderEncoding(String val) {
            try {
                byte[] bytes = val.getBytes("ISO-8859-1");
                if (!Base.looksLikeUtf8(bytes)) {
                    return val;
                }
                return new String(bytes, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                return val;
            }
        }

        private static boolean looksLikeUtf8(byte[] input) {
            int i = 0;
            if (input.length >= 3 && (input[0] & 0xFF) == 239 && (input[1] & 0xFF) == 187 & (input[2] & 0xFF) == 191) {
                i = 3;
            }
            int j = input.length;
            while (i < j) {
                byte o = input[i];
                if ((o & 0x80) != 0) {
                    int end;
                    if ((o & 0xE0) == 192) {
                        end = i + 1;
                    } else if ((o & 0xF0) == 224) {
                        end = i + 2;
                    } else if ((o & 0xF8) == 240) {
                        end = i + 3;
                    } else {
                        return false;
                    }
                    if (end >= input.length) {
                        return false;
                    }
                    while (i < end) {
                        if (((o = input[++i]) & 0xC0) == 128) continue;
                        return false;
                    }
                }
                ++i;
            }
            return true;
        }

        @Override
        public T header(String name, String value) {
            Validate.notEmpty(name, "Header name must not be empty");
            this.removeHeader(name);
            this.addHeader(name, value);
            return (T)this;
        }

        @Override
        public boolean hasHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            return !this.getHeadersCaseInsensitive(name).isEmpty();
        }

        @Override
        public boolean hasHeaderWithValue(String name, String value) {
            Validate.notEmpty(name);
            Validate.notEmpty(value);
            List<String> values = this.headers(name);
            for (String candidate : values) {
                if (!value.equalsIgnoreCase(candidate)) continue;
                return true;
            }
            return false;
        }

        @Override
        public T removeHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            Map.Entry<String, List<String>> entry = this.scanHeaders(name);
            if (entry != null) {
                this.headers.remove(entry.getKey());
            }
            return (T)this;
        }

        @Override
        public Map<String, String> headers() {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(this.headers.size());
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                String header = entry.getKey();
                List<String> values = entry.getValue();
                if (values.size() <= 0) continue;
                map.put(header, values.get(0));
            }
            return map;
        }

        @Override
        public Map<String, List<String>> multiHeaders() {
            return this.headers;
        }

        private List<String> getHeadersCaseInsensitive(String name) {
            Validate.notNull(name);
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (!name.equalsIgnoreCase(entry.getKey())) continue;
                return entry.getValue();
            }
            return Collections.emptyList();
        }

        private Map.Entry<String, List<String>> scanHeaders(String name) {
            String lc = Normalizer.lowerCase(name);
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                if (!Normalizer.lowerCase(entry.getKey()).equals(lc)) continue;
                return entry;
            }
            return null;
        }

        @Override
        public String cookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return this.cookies.get(name);
        }

        @Override
        public T cookie(String name, String value) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            Validate.notNull(value, "Cookie value must not be null");
            this.cookies.put(name, value);
            return (T)this;
        }

        @Override
        public boolean hasCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return this.cookies.containsKey(name);
        }

        @Override
        public T removeCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            this.cookies.remove(name);
            return (T)this;
        }

        @Override
        public Map<String, String> cookies() {
            return this.cookies;
        }
    }
}

