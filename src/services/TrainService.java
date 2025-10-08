package services;

import models.Train;
import utils.CSVUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TrainService {
    private static final String TRAIN_FILE = "trains.csv";
    private final List<Train> trains;

    public TrainService() { this.trains = loadTrains(); }

    private List<Train> loadTrains() {
        List<String> lines = CSVUtils.readLines(TRAIN_FILE);
        if (lines.isEmpty()) return new ArrayList<>();

        return lines.stream()
                .skip(1)
                .map(line -> {
                    try {
                        return Train.fromCSV(line);
                    } catch (Exception e) {
                        System.err.println("Error parsing train line: " + line);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Train> getAllTrains() { return new ArrayList<>(trains); }

    public List<Train> searchTrains(String from, String to, String date) {
        String fromTrim = from.trim().toLowerCase();
        String toTrim = to.trim().toLowerCase();

        return trains.stream()
                .filter(t ->
                        t.getFromStation().toLowerCase().equals(fromTrim) &&
                        t.getToStation().toLowerCase().equals(toTrim) &&
                        t.getServiceDates().contains(date)
                )
                .toList();
    }

    public void addTrain(Train train) {
        trains.add(train);
        saveTrains();
    }

    public boolean deleteTrain(String trainId) {
        boolean removed = trains.removeIf(t -> t.getTrainId().equals(trainId));
        if (removed) saveTrains();
        return removed;
    }

    private void saveTrains() {
        List<String> lines = trains.stream().map(Train::toCSV).toList();
        CSVUtils.writeLines(TRAIN_FILE, lines);
    }

    public Train getTrainById(String trainId) { return trains.stream().filter(t -> t.getTrainId().equals(trainId)).findFirst().orElse(null); }

    public List<String> getAllStations() {
        Set<String> stations = trains.stream()
                .flatMap(t -> Arrays.stream(new String[]{t.getFromStation(), t.getToStation()}))
                .collect(Collectors.toSet());

        List<String> stationList = new ArrayList<>(stations);
        Collections.sort(stationList);
        return stationList;
    }
}
 