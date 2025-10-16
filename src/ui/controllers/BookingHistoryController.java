package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import models.Booking;
import models.Train;
import models.User;
import services.BookingService;
import services.TrainService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingHistoryController {

    @FXML private ListView<String> bookingListView;
    @FXML private Button viewDetailsButton;

    private User user;
    private BookingService bookingService = new BookingService();
    private TrainService trainService = new TrainService();

    private ObservableList<String> bookingDisplayList = FXCollections.observableArrayList();
    private List<Booking> userBookings = FXCollections.observableArrayList();

    private MainFrameController mainController;

    public void setUser(User user) {
        this.user = user;
        loadUserBookings();
    }

    public void setMainController(MainFrameController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        bookingListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        bookingListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("เลยวันเดินทางแล้ว")) {
                        setStyle("-fx-text-fill: red;");
                    } else if(item.contains("เดินทางวันนี้")) {
                        setStyle("-fx-text-fill: yellow;");
                    } else {
                        setStyle("-fx-text-fill: lightblue;");
                    } 
                }
            }
        });
    }

    private void loadUserBookings() {
        bookingDisplayList.clear();

        List<Booking> allBookings = bookingService.getBookings();

        userBookings.clear(); 
        
        for (Booking b : allBookings) {
            if (b.getUsername().equalsIgnoreCase(user.getUsername())) {
                userBookings.add(b);

                Train train = trainService.getTrainById(b.getTrainId());

                String fromStation = "ไม่พบสถานีต้นทาง";
                String toStation = "ไม่พบสถานีปลายทาง";
                if (train != null) {
                    fromStation = train.getFromStation();
                    toStation = train.getToStation();
                }

                String travelDateStr = "ไม่พบวันที่เดินทาง";
                if (train != null && !train.getServiceDates().isEmpty()) {
                    travelDateStr = train.getServiceDates().get(0);
                }

                String statusText = "";
                if (b.isCancelled()) {
                    statusText = " (ยกเลิกแล้ว)";
                }

                String remainingText = "";
                try {
                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate travelDate = LocalDate.parse(travelDateStr, formatter);

                    long days = ChronoUnit.DAYS.between(today, travelDate);
                    if (days < 0) {
                        remainingText = " - เลยวันเดินทางแล้ว";
                    } else if (days == 0) {
                        remainingText = " - เดินทางวันนี้!";
                    } else {
                        remainingText = " - เหลืออีก " + days + " วัน";
                    }
                } catch (Exception e) {
                    remainingText = " - วันที่ไม่ถูกต้อง";
                }

                String displayText = b.getBookingId() + " - " + fromStation + " -> " + toStation
                        + " - วันที่: " + travelDateStr + statusText + remainingText;
                bookingDisplayList.add(displayText);
            }
        }
        bookingListView.setItems(bookingDisplayList);
    }


    @FXML
    private void onViewDetails() {
        int selectedIndex = bookingListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ผิดพลาด");
            alert.setHeaderText(null);
            alert.setContentText("โปรดเลือกตั๋วที่ต้องการดูรายละเอียด");
            alert.showAndWait();
            return;
        }
        Booking selectedBooking = userBookings.get(selectedIndex);
        mainController.loadBookingDetails("/ui/scenes/BookingDetails.fxml", selectedBooking);
    }
}
