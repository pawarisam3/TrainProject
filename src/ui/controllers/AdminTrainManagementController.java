package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import models.Train;
import services.BookingService;
import services.TrainService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminTrainManagementController {

    @FXML private TextField searchIdField;
    @FXML private TextField searchFromField;
    @FXML private TextField searchToField;

    @FXML private ComboBox<Integer> dayCombo;
    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;

    @FXML private TableView<Train> trainTable;
    @FXML private TableColumn<Train, String> idCol;
    @FXML private TableColumn<Train, String> fromCol;
    @FXML private TableColumn<Train, String> toCol;
    @FXML private TableColumn<Train, String> departureCol;
    @FXML private TableColumn<Train, String> arrivalCol;
    @FXML private TableColumn<Train, String> typeCol;
    @FXML private TableColumn<Train, Integer> seatsCol;

    private final TrainService trainService = new TrainService();
    private final BookingService bookingService = new BookingService();
    private final ObservableList<Train> trainList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("trainId"));
        fromCol.setCellValueFactory(new PropertyValueFactory<>("fromStation"));
        toCol.setCellValueFactory(new PropertyValueFactory<>("toStation"));
        departureCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));

        for (int i = 1; i <= 31; i++) dayCombo.getItems().add(i);
        for (int i = 1; i <= 12; i++) monthCombo.getItems().add(i);
        yearCombo.getItems().add(2025);

        loadTrains();
    }

    private void loadTrains() {
        trainList.setAll(trainService.getAllTrains());
        trainTable.setItems(trainList);
    }

    @FXML
    private void handleSearchTrains() {
        String id = searchIdField.getText().trim().toLowerCase();
        String from = searchFromField.getText().trim().toLowerCase();
        String to = searchToField.getText().trim().toLowerCase();

        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();

        final String filterDate;
        if (day != null && month != null && year != null) {
            filterDate = String.format("%04d-%02d-%02d", year, month, day);
        } else {
            filterDate = null;
        }

        List<Train> filtered = trainService.getAllTrains().stream()
            .filter(t -> (id.isEmpty() || t.getTrainId().toLowerCase().contains(id)) &&
                        (from.isEmpty() || t.getFromStation().toLowerCase().contains(from)) &&
                        (to.isEmpty() || t.getToStation().toLowerCase().contains(to)) &&
                        (filterDate == null || t.getServiceDates().contains(filterDate))
            )
            .collect(Collectors.toList());

        trainList.setAll(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchIdField.clear();
        searchFromField.clear();
        searchToField.clear();
        dayCombo.getSelectionModel().clearSelection();
        monthCombo.getSelectionModel().clearSelection();
        yearCombo.getSelectionModel().clearSelection();
        loadTrains();
    }

    @FXML
    private void handleAddTrain() {
        Dialog<Train> dialog = new Dialog<>();
        dialog.setTitle("Add Train");
        dialog.setHeaderText(null);

        TextField idField = new TextField();
        TextField fromField = new TextField();
        TextField toField = new TextField();
        TextField depField = new TextField();
        TextField arrField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("รถธรรมดา", "รถเร็ว", "รถด่วน"));
        TextField seatsField = new TextField();
        TextField datesField = new TextField();
        TextField basePriceField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        grid.addRow(0, new Label("Train ID:"), idField);
        grid.addRow(1, new Label("From:"), fromField);
        grid.addRow(2, new Label("To:"), toField);
        grid.addRow(3, new Label("Departure (HH:mm):"), depField);
        grid.addRow(4, new Label("Arrival (HH:mm):"), arrField);
        grid.addRow(5, new Label("Type:"), typeBox);
        grid.addRow(6, new Label("Seats:"), seatsField);
        grid.addRow(7, new Label("Dates (yyyy-MM-dd;...):"), datesField);
        grid.addRow(8, new Label("Base Price:"), basePriceField);

        dialog.getDialogPane().setContent(grid);
        ButtonType okButtonType = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);

        final Train[] newTrain = {null};

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                String id = idField.getText().trim();
                String from = fromField.getText().trim();
                String to = toField.getText().trim();
                String dep = depField.getText().trim();
                String arr = arrField.getText().trim();
                String type = typeBox.getValue();
                String seatsText = seatsField.getText().trim();
                String priceText = basePriceField.getText().trim();

                List<String> dates = Arrays.stream(datesField.getText().trim().split(";"))
                           .map(String::trim)
                           .filter(s -> !s.isEmpty())
                           .collect(Collectors.toList());

                if (id.isEmpty() || from.isEmpty() || to.isEmpty() || dep.isEmpty() || arr.isEmpty() || type == null ||
                    seatsText.isEmpty() || priceText.isEmpty() || dates.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "กรุณากรอกข้อมูลให้ครบถ้วน");
                    event.consume(); 
                    return;
                }

                if (!isValidTime(dep) || !isValidTime(arr)) {
                    showAlert(Alert.AlertType.ERROR, "เวลาต้องอยู่ในรูปแบบ HH:mm เช่น 15:45");
                    event.consume();
                    return;
                }

                for (String date : dates) {
                    if (!isValidDate(date)) {
                        showAlert(Alert.AlertType.ERROR, "วันเดินรถไม่ถูกต้อง: " + date + "\nควรใช้ yyyy-MM-dd");
                        event.consume();
                        return;
                    }
                }

                int seats = Integer.parseInt(seatsText);
                double basePrice = Double.parseDouble(priceText);

                boolean duplicateId = trainService.getAllTrains().stream()
                        .anyMatch(t -> t.getTrainId().equalsIgnoreCase(id));
                if (duplicateId) {
                    showAlert(Alert.AlertType.WARNING, "รหัสขบวนรถไฟนี้มีอยู่แล้ว");
                    event.consume();
                    return;
                }

                for (Train t : trainService.getAllTrains()) {
                    if (t.getFromStation().equalsIgnoreCase(from) &&
                        t.getToStation().equalsIgnoreCase(to)) {

                        for (String date : dates) {
                            if (t.getServiceDates().contains(date)) {
                                if (t.getDepartureTime().equals(dep) && t.getArrivalTime().equals(arr)) {
                                    showAlert(Alert.AlertType.ERROR,
                                            "ขบวนรถจาก " + from + " ไป " + to + "\n" +
                                            "ในวันที่ " + date + " เวลา " + dep + "-" + arr + " มีอยู่แล้ว");
                                    event.consume();
                                    return;
                                }
                            }
                        }
                    }
                }


                newTrain[0] = new Train(id, from, to, dep, arr, type, seats, dates, basePrice);

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "จำนวนที่นั่งและราคาต้องเป็นตัวเลข");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return newTrain[0];
            }
            return null;
        });

        Optional<Train> result = dialog.showAndWait();
        result.ifPresent(train -> {
            trainService.addTrain(train);
            loadTrains();
            showAlert(Alert.AlertType.INFORMATION, "เพิ่มรถไฟเรียบร้อยแล้ว");
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteTrain() {
        Train selected = trainTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "กรุณาเลือกข้อมูลรถไฟที่ต้องการลบ");
            alert.showAndWait();
            return;
        }

        boolean hasBooking = bookingService.getBookings().stream()
                .anyMatch(b -> b.getTrainId().equals(selected.getTrainId()) && !b.isCancelled());

        if (hasBooking) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "ไม่สามารถลบรถไฟที่มีการจองอยู่ได้");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "คุณแน่ใจที่จะลบรถไฟ " + selected.getTrainId() + " หรือไม่?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                trainService.deleteTrain(selected.getTrainId());
                loadTrains();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "ลบรถไฟเรียบร้อยแล้ว");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handleAddNewsImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("เลือกภาพสำหรับเพิ่ม");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("ไฟล์ภาพ", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Path targetDir = Paths.get("src/ui/scenes/news-images");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                Path targetPath = targetDir.resolve(selectedFile.getName());

                if (Files.exists(targetPath)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "มีภาพที่ชื่อเดียวกันอยู่แล้ว");
                    alert.showAndWait();
                    return;
                }

                Files.copy(selectedFile.toPath(), targetPath);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "เพิ่มภาพเรียบร้อยแล้ว");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "เพิ่มภาพไม่สำเร็จ");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDeleteNewsImage() {
        File dir = new File("src/ui/scenes/news-images");
        File[] images = dir.listFiles((d, name) -> name.matches(".*\\.(png|jpg|jpeg|gif)$"));

        if (images == null || images.length == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "ไม่มีภาพให้ลบ");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(images[0].getName(),
                Arrays.stream(images).map(File::getName).toList());
        dialog.setTitle("ลบภาพข่าว");
        dialog.setHeaderText("เลือกภาพที่ต้องการลบ");

        dialog.showAndWait().ifPresent(filename -> {
            File toDelete = new File(dir, filename);
            if (toDelete.delete()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "ลบภาพเรียบร้อยแล้ว");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "ลบภาพไม่สำเร็จ");
                alert.showAndWait();
            }
        });
    }

    private boolean isValidTime(String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(timeStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
