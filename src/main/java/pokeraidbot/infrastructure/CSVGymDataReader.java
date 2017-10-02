package pokeraidbot.infrastructure;

import pokeraidbot.domain.gym.Gym;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CSVGymDataReader {
    private String resourceName;

    public CSVGymDataReader(String resourceName) {
        this.resourceName = resourceName;
    }

    public Set<Gym> readAll() {
        String line;
        Set<Gym> gyms = new HashSet<>();
        try {
            final InputStream resourceAsStream = CSVGymDataReader.class.getResourceAsStream(resourceName);
            if (resourceAsStream == null) {
                throw new FileNotFoundException(resourceName);
            }
            final InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);

            while ((line = br.readLine()) != null) {
                String[] rowElements = line.split(",");
                if (rowElements[0].equalsIgnoreCase("ID")) {
                    // This is the header of the file, ignore
                } else {
                    String id = rowElements[0].trim();
                    String x = rowElements[1].replaceAll("\"", "").trim();
                    String y = rowElements[2].replaceAll("\"", "").trim();
                    String name = rowElements[3].trim();
                    String image = rowElements[4].trim();
                    Gym gym = new Gym(name, id, x, y, image);
                    gyms.add(gym);
                }
            }

        } catch (IOException e) {
            System.err.println("Error while trying to open gym file " + resourceName + ": " + e.getMessage());
        }

        System.out.println("Parsed " + gyms.size() + " gyms from \"" + resourceName + "\".");

        return gyms;
    }
}
