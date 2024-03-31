import java.awt.Desktop;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App
{
    private static String CLIENT_ID = "ec90806c0b35b2389951";
    private static String CLIENT_SECRET = "d2c98c11400d56de3b93021a23d3a592228fcfb0";
    private static String AUTH_ENDPOINT = String.format("https://github.com/login/oauth/authorize?response_type=code&client_id=%s", CLIENT_ID);
    private static String TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";
    private static String USER_ENDPOINT = "https://api.github.com/user";

    public static void main( String[] args ) throws Exception {
        System.out.println("Please visit the following URL to authorize the application:");
        System.out.println(AUTH_ENDPOINT);
        try {
            Desktop.getDesktop().browse(new URI(AUTH_ENDPOINT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the authentication code: ");
        String authCode = sc.nextLine();
        String accessToken = exchangeCodeForAccessToken(authCode);

        String userInfo = getUserInfo(accessToken);
        System.out.println(userInfo);

        sc.close();

    }

    private static String exchangeCodeForAccessToken(String code) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(TOKEN_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("client_id=%s&client_secret=%s&code=%s",
                                CLIENT_ID, CLIENT_SECRET, code)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        Map<String, String> responseParams = Pattern.compile("&")
                .splitAsStream(responseBody)
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> URLDecoder.decode(arr[0], java.nio.charset.StandardCharsets.UTF_8),
                        arr -> URLDecoder.decode(arr[1], java.nio.charset.StandardCharsets.UTF_8)
                ));
        return responseParams.get("access_token");
    }

    private static String getUserInfo(String accessToken) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(USER_ENDPOINT))
                .header(HttpHeaders.AUTHORIZATION, String.format("token %s", accessToken))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
