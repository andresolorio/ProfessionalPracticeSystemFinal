package mx.uv.lis.professionalpracticesystem.logic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.INITIAL_COUNTER_INDEX;

/**
 *
 * @author cinth
 * @author andre
 */
public class JsonCityLoader {

    private static final Logger LOGGER = Logger.getLogger(JsonCityLoader.class.getName());

    private JsonCityLoader() {
    }

    public static List<String> loadCitiesFromCountry(String countryName) {
        List<String> cities = new ArrayList<>();

        try (InputStream inputStream = JsonCityLoader.class.getResourceAsStream("/json/CountriesAndCities.json")) {
            if (inputStream == null) {
                LOGGER.log(Level.WARNING, "Cities JSON asset file could not be found within resources context.");
                return cities;
            }

            String jsonText = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

            JSONArray countriesArray = new JSONArray(jsonText);

            for (int i = INITIAL_COUNTER_INDEX; i < countriesArray.length(); i++) {
                JSONObject countryObj = countriesArray.getJSONObject(i);
                if (countryObj.getString("name").equalsIgnoreCase(countryName)) {
                    JSONArray citiesArray = countryObj.getJSONArray("cities");

                    for (int j = INITIAL_COUNTER_INDEX; j < citiesArray.length(); j++) {
                        cities.add(citiesArray.getString(j));
                    }
                    break;
                }
            }

            Collections.sort(cities);

        } catch (JSONException | IOException exception) {
            LOGGER.log(Level.SEVERE, "Critical error occurred while parsing and processing geographical JSON stream data.", exception);
        }

        return cities;
    }
}
