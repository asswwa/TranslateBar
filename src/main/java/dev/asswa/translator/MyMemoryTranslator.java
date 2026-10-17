package dev.asswa.translator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Uses Google's free, undocumented web-translation endpoint (no API key). */
public final class MyMemoryTranslator {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private MyMemoryTranslator() {}

    public static String translate(String text, String targetLanguage) {
        try {
            String query = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String target = URLEncoder.encode(targetLanguage, StandardCharsets.UTF_8);
            URI uri = URI.create("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl="
                    + target + "&dt=t&q=" + query);

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/131 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                throw new IOException("Translation service returned HTTP " + response.statusCode());
            }

            JsonArray root = JsonParser.parseString(response.body()).getAsJsonArray();
            JsonArray chunks = root.get(0).getAsJsonArray();
            StringBuilder translated = new StringBuilder();
            for (JsonElement chunkElement : chunks) {
                if (chunkElement != null && chunkElement.isJsonArray()) {
                    JsonArray chunk = chunkElement.getAsJsonArray();
                    if (!chunk.isEmpty() && !chunk.get(0).isJsonNull()) {
                        translated.append(chunk.get(0).getAsString());
                    }
                }
            }
            String result = translated.toString().trim();
            if (result.isEmpty()) {
                throw new IOException("Translation response was empty");
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Automatic language detection / translation failed", e);
        }
    }
}
