package dataimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.infrastructure.CSVGymDataReader;

import java.util.LinkedList;
import java.util.Set;

public class GymsDiff {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymsDiff.class);

    // todo: refactor duplicate code in importer
    public static void main(String[] args) {
        CSVGymDataReader newGymDataReader = new CSVGymDataReader(GymsDiff.class.getResourceAsStream("/umea_gym_with_id.csv"));
        CSVGymDataReader oldGymDataReader = new CSVGymDataReader(GymsDiff.class.getResourceAsStream("/gyms_umeå.csv"));
        final Set<Gym> oldGyms = oldGymDataReader.readAll();
        final Set<Gym> newGyms = newGymDataReader.readAll();
        int sameCount = 0;
        LinkedList<String> report = new LinkedList<>();
        LinkedList<String> focusReport = new LinkedList<>();
        for (Gym newGym : newGyms) {
            if (!oldGyms.contains(newGym)) {
                boolean weird = false;
                for (Gym oldGym : oldGyms) {
                    if (oldGym.getX().equals(newGym.getX()) && oldGym.getY().equals(newGym.getY())) {
                        focusReport.add("New name for old gym? Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    } else if (oldGym.getId().equals(newGym.getId())) {
                        focusReport.add("Reused ID for old gym? Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    } else if (oldGym.getName().trim().equalsIgnoreCase(newGym.getName().trim())) {
                        focusReport.add("Potential duplicate. Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    }
                }
                if (!weird) {
                    focusReport.add("New gym: " + newGym);
                }
            } else {
                sameCount++;
            }
        }

        for (Gym oldGym : oldGyms) {
            boolean weird = false;
            if (!newGyms.contains(oldGym)) {
                for (Gym newGym : newGyms) {
                    if (oldGym.getX().equals(newGym.getX()) && oldGym.getY().equals(newGym.getY())) {
                        LOGGER.debug("OLD: New name for old gym? Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    } else if (oldGym.getId().equals(newGym.getId())) {
                        LOGGER.debug("OLD: Reused ID for old gym? Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    } else if (oldGym.getName().equalsIgnoreCase(newGym.getName())) {
                        LOGGER.debug("OLD: Potential duplicate. Old: " + oldGym + " - New: " + newGym);
                        weird = true;
                    }
                }
                if (!weird) {
                    report.add("Removed gym: " + oldGym);
                }
            } else {
                report.add("Exists in old and new: " + oldGym);
            }
        }

        for (String r : report) {
            System.out.println(r);
        }
        for (String r : focusReport) {
            System.out.println(r);
        }
        System.out.println("\nREPORT:\n\nOld gyms: " + oldGyms.size() + ", new gyms: " + newGyms.size() +
                ", diff: " + (newGyms.size() - oldGyms.size()) + ", same in both: " + sameCount);
    }
}
