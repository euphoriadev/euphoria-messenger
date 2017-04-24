package ru.euphoria.messenger.net;

import android.support.v4.util.ArrayMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import ru.euphoria.messenger.io.EasyStreams;

/**
 * A simple class to handle network requests. Support GET and POST.
 */
public class HttpRequest {
    public static final String GET = "GET";
    public static final String POST = "POST";

    private HttpURLConnection connection;
    private String url;
    private String method;
    private ArrayMap<String, String> params;

    /**
     * Creates a new HTTP HttpRequest with params
     *
     * @param url    the URL to remote
     * @param method the http method, e.g. GET, POST, HEAD
     * @param params the name-value params
     */
    public HttpRequest(String url, String method, ArrayMap<String, String> params) {
        this.url = url;
        this.method = method;
        this.params = params;
    }

    /**
     * Creates a new HTTP HttpRequest with GET method and specified url
     *
     * @param url    the URL to remote
     * @param params the name-value params
     */
    public static HttpRequest get(String url, ArrayMap<String, String> params) {
        return new HttpRequest(url, GET, params);
    }

    /**
     * Creates a new HTTP HttpRequest with GET method and specified url
     *
     * @param url the URL to remote
     */
    public static HttpRequest get(String url) {
        return get(url, null);
    }

    /**
     * Reads all characters from specified {@link HttpURLConnection}.
     *
     * @throws IOException if an I/O error occurs reading from the stream
     */
    public String asString() throws IOException {
        InputStream input = getStream();
        String content = EasyStreams.read(input);

        connection.disconnect();
        return content;
    }

    /**
     * Reads all bytes from specified {@link HttpURLConnection} into a byte array
     *
     * @throws IOException if an I/O error occurs reading from the stream
     */
    public byte[] asBytes() throws IOException {
        InputStream input = getStream();
        byte[] content = EasyStreams.readBytes(input);

        connection.disconnect();
        return content;
    }


    /**
     * Returns an input stream that reads from this open connection.
     * And wraps connection into {@link GZIPInputStream}
     * if "content-encoding" is "gzip"
     *
     * @throws IOException if an I/O error occurs reading from the connection stream
     */
    public InputStream getStream() throws IOException {
        if (connection == null) {
            connection = createConnection();
        }
        InputStream input = connection.getInputStream();

        String encoding = connection.getHeaderField("Content-Encoding");
        if (encoding != null && "gzip".equalsIgnoreCase(encoding)) {
            input = EasyStreams.gzip(input);
        }
        return input;
    }

    private String getParams() throws UnsupportedEncodingException {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            String key = params.keyAt(i);
            String value = params.valueAt(i);

            buffer.append(key).append("=");
            buffer.append(URLEncoder.encode(value, "UTF-8"));
            buffer.append("&");
        }
        return buffer.toString();
    }

    private String getUrl() throws UnsupportedEncodingException {
        if (params != null && "GET".equalsIgnoreCase(method)) {
            return url + "?" + getParams();
        }
        return url;
    }

    private HttpURLConnection createConnection() throws IOException {
        connection = (HttpURLConnection) new java.net.URL(getUrl()).openConnection();
        connection.setReadTimeout(60_000);
        connection.setConnectTimeout(60_000);
        connection.setUseCaches(true);
        connection.setDoInput(true);
        connection.setDoOutput(!GET.equalsIgnoreCase(method));
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept-Encoding", "gzip");

        return connection;
    }

    @Override
    public String toString() {
        try {
            return asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
