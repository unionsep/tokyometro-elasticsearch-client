package client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;

public class TokyoMetro {

    public static final Integer ELASTIC_DEFAULT_PORT = 9300;
    public static final String METRO_API_URL = "https://api.tokyometroapp.jp/api/v2/datapoints?rdf:type=odpt:Train";

    public static void main(String ... args) {

        if (0 >= args.length) {
            System.out.println("empty arguments");
            return;
        }

        String consumerKey = null;
        String elasticAddress = null;
        Integer elasticPort = null;
        for (int i = 0; i < args.length; i = i + 2) {
            String key = args[i];
            String val = args[i + 1];
            switch (key) {
                case "-ck":
                    consumerKey = StringUtils.isNotEmpty(val) ? val : null;
                    break;
                case "-ea":
                    elasticAddress = StringUtils.isNotEmpty(val) ? val : null;
                    break;
                case "-ep":
                    elasticPort = StringUtils.isNumeric(val) ? Integer.parseInt(val) : ELASTIC_DEFAULT_PORT;
                    break;
                default:
                    break;
            }
        }

        if (null == elasticPort) {
            elasticPort = ELASTIC_DEFAULT_PORT;
        }
        if (StringUtils.isBlank(consumerKey) || StringUtils.isBlank(elasticAddress)) {
            System.out.println("invalid arguments");
            return;
        }

        try {
            TransportAddress add = new InetSocketTransportAddress(InetAddress.getByName(elasticAddress), elasticPort);
            TransportClient cli = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(add);
            for (Object json : getMetroAPI(consumerKey)) {
                IndexResponse response = cli.prepareIndex("tokyo-metro", "info").setSource(json.toString()).get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getMetroAPI(String consumerKey) throws IOException, ParseException {
        String metroUrl = METRO_API_URL + "&acl:consumerKey=" + consumerKey;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(metroUrl);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String json = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));

        JSONArray arr = (JSONArray) (new JSONParser()).parse(json);
        response.close();

        return arr;
    }

}
