package dogapi;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    public String run(String breed) throws BreedNotFoundException {
        HttpUrl url = HttpUrl.parse("https://dog.ceo/api/breed/"+ breed +"/list").newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        try {
            String response = run(breed);
            JSONObject jsonObject = new JSONObject(response);

            String status = jsonObject.getString("status");
            if (!status.equals("success")) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray jsonBreeds = jsonObject.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < jsonBreeds.length(); i++) {
                subBreeds.add(jsonBreeds.getString(i));
            }
            return subBreeds;
        } catch (JSONException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}