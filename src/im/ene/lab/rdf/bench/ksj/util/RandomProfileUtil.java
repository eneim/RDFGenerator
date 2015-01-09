package im.ene.lab.rdf.bench.ksj.util;

import im.ene.lab.rdf.bench.ksj.models.NPeople;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class RandomProfileUtil {

	private static final String BASE_URL = "http://randomprofile.com/api/api.php";

	private final HttpPost httpPost;
	private final HttpClient httpClient;

	List<NameValuePair> params = new ArrayList<NameValuePair>();

	public RandomProfileUtil() {
		httpPost = new HttpPost(BASE_URL);
		httpClient = HttpClients.createDefault();

		HttpHost proxy = new HttpHost("proxy.noc.titech.ac.jp", 3128, "http");
		RequestConfig config = RequestConfig.custom().setSocketTimeout(30000)
				.setConnectTimeout(10000).setProxy(proxy).build();
		httpPost.setConfig(config);

	}

	public NPeople[] getDefault() throws ClientProtocolException, IOException {
		params.clear();

		params.add(new BasicNameValuePair("countries", "JPN"));
		params.add(new BasicNameValuePair("format", "json"));
		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		HttpResponse response = this.httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream responseStream = entity.getContent();
			String responseString = streamToString(responseStream);
			responseStream.close();

			// stupid fix
			if (responseString.contains("}{")) {
				responseString = responseString.replace("}{", "},{");
				responseString = responseString + "]";
				responseString = "[" + responseString;
			}

			return DataUtil.GSON.fromJson(responseString, NPeople[].class);
		} else
			return null;

	}

	private String streamToString(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		String result = sb.toString();
		br.close();
		return result;
	}

}
