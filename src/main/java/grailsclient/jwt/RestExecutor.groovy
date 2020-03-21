package grailsclient.jwt

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import okhttp3.*
import okhttp3.Request.Builder
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

public class RestExecutor {
	public static org.slf4j.Logger logger = LoggerFactory.getLogger(RestExecutor.class)
/*    private static final String API_VERSION = '1.0'
    private static final String HTTP_HEADER_ACCEPT_VERSION = 'Accept-Version'
    private static final String HTTP_HEADER_ACCEPT = 'Accept'
    private static final String HTTP_HEADER_ACCEPT_VALUE = 'application/json'
    private static final String HTTP_HEADER_AUTHORIZATION = 'Authorization'
    private static final String HTTP_HEADER_AUTHORIZATION_BEARER = 'Bearer'*/
	private static final MediaType JSON 	= MediaType.parse("application/json; charset=utf-8");
	static ConnectionPool pool = new ConnectionPool(20, 10000, TimeUnit.MILLISECONDS);
	private OkHttpClient client ;
	private final String serverUrl
	// private final JwtStorage jwtStorage
	String username
	String password

	/*public RestExecutor(String serverUrl, JwtStorage jwtStorage, String username = null, String password = null) {

        client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .build();

        this.serverUrl = serverUrl
        this.jwtStorage = jwtStorage
        this.username = username
        this.password = password
    }*/

	public RestExecutor(String serverUrl) {

		client = new OkHttpClient.Builder()
				.connectionPool(pool)
				.build();

		this.serverUrl = serverUrl
	}

	private int fetchResponse(Response response, Closure listener) {
		logger.info('Fetching response with code {}', response.code())
		if ( response.code() == 200 ) {
			try {
				processOKAPIResponse(response, listener)
			}catch (IOException e) {
				logger.info('Exception while fetching response',e);
			}
			return 2
		}

		logger.info('Involing error callback with body {}',  response.body().string())
		listener(null, APIError.NETWORKING_ERROR)
		return 2
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static void processOKAPIResponse(Response response, Closure listener) {
		try {

			def jsonString = response.body().string()
			listener(jsonString, APIError.NONE)
		} catch (IOException e) {
			listener(null, APIError.JSON_PARSING_ERROR)
		}
	}

	public void get(String path, Map params, Closure listener) {
		try {
			String urlPath = "${serverUrl}${path}"
			HttpUrl.Builder urlBuilder = HttpUrl.parse(urlPath).newBuilder();
			params.forEach { t, u -> urlBuilder.addQueryParameter(t,u.toString())}

			String url = urlBuilder.build().toString();
			Builder builder = new Request.Builder()
					.url(url)
					.get();

			Request request = builder.build()

			int attempt = 0;
			while (attempt < 2) {
				Response response = client.newCall(request).execute()
				attempt += fetchResponse(response, listener);
				if (attempt < 2) {
					logger.info("Will retry...");
					request = builder.build()
				}
			}

		} catch (IOException e) {
			logger.error("Exception while executing get", e)
			listener(null, APIError.NETWORKING_ERROR)
		}
	}


	/**
	 * Public method
	 * @param listener
	 */
	public void postJson(String path, String jsonData, Closure listener) {
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse("${serverUrl}${path}").newBuilder();

			String url = urlBuilder.build().toString();
			RequestBody body = RequestBody.create(JSON, jsonData);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build()

			Response response = client.newCall(request).execute()
			fetchResponse(response, listener)

		} catch (IOException e) {
			listener(null, APIError.NETWORKING_ERROR)
		}
	}

}
