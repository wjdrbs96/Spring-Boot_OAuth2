package me.gyun.test.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 아직 임시로 카카오 로그인 테스트만 해봄 (코드 리팩토링 필요)
 */

@Service
public class KaKaoService {

    // KAKAO REST API Key
    @Value("${KAKAO.RESTAPI}")
    private String kakaoKey;

    // KAKAO accessToken, refreshToken 호출
    @Value("${TOKEN_URL}")
    private String tokenReqURL;

    // KAKAO 사용자 정보 API 호출
    @Value("${API_URL}")
    private String apiReqURL;

    // Redirect URL
    @Value("${REDIRECT_URL}")
    private String redirectURL;

    public String getAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            HttpURLConnection conn = urlRequest(tokenReqURL);
            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + kakaoKey);
            sb.append("&redirect_uri=" + redirectURL);
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonElement element = linePlus(br);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public Map<String, Object> getUserInfo (String access_Token) {

        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        Map<String, Object> userInfo = new HashMap<>();

        try {
            HttpURLConnection conn = urlRequest(apiReqURL);

            //  요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            JsonElement element = linePlus(br);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

    private HttpURLConnection urlRequest(String reqURL) throws IOException {
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        return conn;
    }

    private JsonElement linePlus(BufferedReader br) throws IOException {
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        System.out.println("response body : " + result);

        JsonElement element = JsonParser.parseString(result);

        return element;
    }
}
