package com.example.secondproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Driver extends Application {

    private Tree<District> districtTree = new Tree<>();
    private Stack<District> districtStack = new Stack<>();
    private Stack<District> previousDistrict = new Stack<>();
    private Stack<Location> nextLocationStack = new Stack<>();
    private Stack<Location> previousLocationStack = new Stack<>();
    private Stack<MDate> nextMartyrDateStack = new Stack<>();
    private Stack<MDate> previousMartyrDateStack = new Stack<>();
    private ObservableList<String> districtNames = FXCollections.observableArrayList();
    private ObservableList<String> locationNames;
    private ComboBox<String> districtComboBox;
    private ComboBox<String> locationComboBox;
    private File file;
    private FileChooser fileChooserWrite;
    private Stage stage;
    private Label resLabel;
    private Label labelDistrictInfo;
    private Label labelLocationInfo = new Label();
    private Label labelMartyrInfo = new Label();
    private Label labelMartyrUpdateInfo = new Label();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        Button loadButton = new Button("Load Records");

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                loadRecordsFromFile(selectedFile);
                districtScreen();
            }
        });

        resLabel = new Label();

        VBox vStart = new VBox();
        vStart.getChildren().addAll(loadButton, resLabel);
        vStart.setSpacing(10);
        vStart.setAlignment(Pos.CENTER);


        Scene scene = new Scene(vStart, 300, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Read File Screen");
        primaryStage.show();
        primaryStage.centerOnScreen();

    }

    public void districtScreen() {

        Button btnInsertD = new Button("Insert New District");
        Button btnUpdateD = new Button("Update District");
        Button btnDeleteD = new Button("Delete District");
        Button btnLoadDistrict = new Button("Load to Location");
        Button btnNextD = new Button("Next District");
        Button btnPreviousD = new Button("Previous District");

        TextField insField = new TextField();
        insField.setPromptText("Insert a new District");
        insField.setPrefWidth(200);
        TextField upField = new TextField();
        upField.setPromptText("District u want to update ");
        upField.setPrefWidth(200);
        TextField upNewField = new TextField();
        upNewField.setPromptText("New District Name");
        upNewField.setPrefWidth(200);

        labelDistrictInfo = new Label();
        Label delLabel = new Label("Just Take a district from combo box above then click");
        delLabel.setGraphicTextGap(10);


        btnDeleteD.setPrefWidth(200);
        btnInsertD.setPrefWidth(200);
        btnUpdateD.setPrefWidth(200);
        btnLoadDistrict.setPrefWidth(150);
        btnNextD.setPrefWidth(100);
        btnPreviousD.setPrefWidth(130);

        // Create a ComboBox to display district names
        districtComboBox = new ComboBox<>();
        districtComboBox.setItems(districtNames);
        districtComboBox.setPromptText("Select a District");
        districtComboBox.setPrefWidth(200);

        BorderPane border = new BorderPane();

        GridPane disPane = new GridPane();
        disPane.setAlignment(Pos.CENTER);
        disPane.setHgap(10);
        disPane.setVgap(10);
        disPane.add(btnInsertD, 0, 1);
        disPane.add(insField, 1, 1);
        disPane.add(btnUpdateD, 0, 2);
        disPane.add(upNewField, 1, 2);
        disPane.add(btnDeleteD, 0, 3);
        disPane.add(delLabel, 1, 3);
        disPane.add(districtComboBox, 1, 0);

        disPane.add(labelDistrictInfo, 0, 6);
        GridPane.setColumnSpan(labelDistrictInfo, 3);


        populateStackInOrder(districtTree.getRoot());
        displayDistrictStatistics(districtStack.peek());

        FXCollections.sort(districtNames);


        HBox navBox = new HBox();
        navBox.getChildren().addAll(btnPreviousD, btnNextD, btnLoadDistrict);
        navBox.setSpacing(10);
        navBox.setAlignment(Pos.CENTER);

        disPane.add(navBox, 1, 4);

        border.setCenter(disPane);

        btnInsertD.setOnAction(e -> {
            String newDistrictName = insField.getText();

            if (newDistrictName == null || newDistrictName.isEmpty()) {
                labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelDistrictInfo.setText("Please select a district from the list.");
                return;
            }

            // Check if the district already exists in the district tree using the find method
            TNode<District> districtNode = districtTree.find(new District(newDistrictName));

            if (districtNode != null) {
                labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelDistrictInfo.setText("District already exists: " + newDistrictName);
                districtComboBox.getSelectionModel().clearSelection();
                return;
            }

            // Create a new District object
            District newDistrict = new District(newDistrictName);

            // Insert the new district into the district tree
            districtTree.insert(newDistrict);
            districtStack.clear();
            previousDistrict.clear();
            populateStackInOrder(districtTree.getRoot());

            // Add the new district name to the combo box
            districtNames.add(newDistrictName);
            FXCollections.sort(districtNames);

            // Clear selection and input field after insertion
            districtComboBox.getSelectionModel().clearSelection();
            insField.clear();
            labelDistrictInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            labelDistrictInfo.setText("District inserted: " + newDistrictName);
        });

        btnUpdateD.setOnAction(e -> {
            String oldDistrictName = districtComboBox.getValue(); // Get selected district name
            String newDistrictName = upNewField.getText().trim();

            if (oldDistrictName == null || oldDistrictName.isEmpty() || newDistrictName.isEmpty()) {
                // Handle invalid input (if somehow the districtComboBox value is null or empty)
                return;
            }
            if (districtTree.find(new District(newDistrictName)) != null) {
                // New district name already exists
                labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelDistrictInfo.setText("District name already exists: " + newDistrictName);
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Update District");
            confirmAlert.setContentText("Are u sure to update district name " + oldDistrictName + " to " + newDistrictName + "?");
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                // Find the district to update by its current name
                TNode<District> districtNodeToUpdate = districtTree.find(new District(oldDistrictName));
                if (districtNodeToUpdate != null) {
                    // Update the district name
                    districtTree.delete(districtNodeToUpdate.getData()); // Remove existing node

                    // Create updated District object
                    District updatedDistrict = new District(newDistrictName);

                    // Insert updated District back into the tree
                    districtTree.insert(updatedDistrict);

                    districtNames.remove(oldDistrictName);
                    districtNames.add(newDistrictName);
                    districtComboBox.getItems().clear();
                    FXCollections.sort(districtNames);

                    districtStack.clear();
                    previousDistrict.clear();
                    populateStackInOrder(districtTree.getRoot());


                    System.out.println(districtNodeToUpdate);

                    districtComboBox.getSelectionModel().clearSelection();
                    labelDistrictInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    labelDistrictInfo.setText("District updated: " + oldDistrictName + " -> " + newDistrictName);

                    // Example: Compare district names (case-insensitive)
                    if (updatedDistrict.getDistrictName().equalsIgnoreCase(oldDistrictName)) {
                        labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        labelDistrictInfo.setText("District names match.");
                    }

                } else {
                    // District not found
                    labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelDistrictInfo.setText("District not found: " + oldDistrictName);
                }

            } else {
                // User canceled the update
                labelDistrictInfo.setStyle("-fx-text-fill: blue; -fx-font-weight: normal;");
                labelDistrictInfo.setText("Update canceled.");
            }

            // Clear input fields after processing
            upField.clear();
            upNewField.clear();
        });

        btnDeleteD.setOnAction(e -> {
            String deleteDistrictName = districtComboBox.getValue();

            if (deleteDistrictName == null || deleteDistrictName.isEmpty()) {
                labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelDistrictInfo.setText("Please select a district from the list.");
                return;
            }
            // Display a confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Update District");
            confirmAlert.setContentText("Are u sure to delete district  " + deleteDistrictName + "?");

            // Show the confirmation dialog and wait for user's response
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                // User confirmed the deletion

                // Find the district node to delete by its name
                TNode<District> districtNodeToDelete = districtTree.find(new District(deleteDistrictName));

                if (districtNodeToDelete != null) {
                    // Delete the district node from the tree
                    districtTree.delete(districtNodeToDelete.getData());

                    districtNames.remove(deleteDistrictName);
                    FXCollections.sort(districtNames);
                    districtComboBox.getSelectionModel().clearSelection();
                    // Update UI to show successful deletion message
                    labelDistrictInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    labelDistrictInfo.setText("District '" + deleteDistrictName + "' deleted successfully.");
                } else {
                    // District not found in the tree
                    labelDistrictInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelDistrictInfo.setText("District '" + deleteDistrictName + "' not found.");
                }
            } else {
                // User canceled the deletion
                labelDistrictInfo.setStyle("-fx-text-fill: blue; -fx-font-weight: normal;");
                labelDistrictInfo.setText("Deletion canceled.");
            }


        });

        btnNextD.setOnAction(event -> {
            navigateNext();
        });

        btnPreviousD.setOnAction(event -> {
            navigatePrevious();
        });

        btnLoadDistrict.setOnAction(e -> {
            District currentDistrict = districtStack.peek();

            // Clear the location stacks before loading new locations
            nextLocationStack.clear();
            previousLocationStack.clear();

            // Push all locations of the current district onto the nextLocationStack
            if (currentDistrict != null) {
                traverseLocationsLevelOrder(currentDistrict);
            }

            // Display the location screen
            locationScreen(currentDistrict);
        });


        Scene scene = new Scene(border, 650, 500);
        stage.setScene(scene);
        stage.setTitle("District Screen");
        stage.centerOnScreen();

    }


    public void locationScreen(District district) {
        Button btnInsertL = new Button("Insert New Location");
        Button btnUpdateL = new Button("Update Location");
        Button btnDeleteL = new Button("Delete Location");
        Button btnNextL = new Button("Next Location");
        Button btnPreviousL = new Button("Previous Location");
        Button btnLoadLocation = new Button("Load to Martyr Screen");
        Button btnBackL = new Button("Back");

        TextField insField = new TextField();
        insField.setPromptText("Insert a new Location");
        insField.setPrefWidth(200);


        TextField upNewField = new TextField();
        upNewField.setPromptText("New Location Name");
        upNewField.setPrefWidth(200);

        locationNames = FXCollections.observableArrayList(locationNamesComboBox(district));

        locationComboBox = new ComboBox<>();
        locationComboBox.setItems(locationNames);
        locationComboBox.setPromptText("Select a Location");
        locationComboBox.setPrefWidth(200);

        traverseLocationsLevelOrder(district);

        BorderPane lBorder = new BorderPane();

        GridPane locPane = new GridPane();
        locPane.setAlignment(Pos.CENTER);
        locPane.setHgap(10);
        locPane.setVgap(10);


        locPane.add(locationComboBox, 1, 0);

        locPane.add(btnInsertL, 0, 1);
        locPane.add(insField, 1, 1);

        locPane.add(btnUpdateL, 0, 2);
        locPane.add(upNewField, 1, 2);

        locPane.add(btnDeleteL, 0, 3);

        locPane.add(btnPreviousL, 0, 4);
        locPane.add(btnNextL, 1, 4);
        locPane.add(btnLoadLocation, 2, 4);

        locPane.add(labelLocationInfo, 1, 5);
        GridPane.setColumnSpan(labelLocationInfo, 2);
        lBorder.setCenter(locPane);

        HBox hBackBtn = new HBox(15);
        hBackBtn.getChildren().addAll(btnBackL);
        hBackBtn.setAlignment(Pos.BOTTOM_LEFT);
        lBorder.setBottom(hBackBtn);


        btnInsertL.setOnAction(event -> {

            String newLocationName = insField.getText().trim();

            if (newLocationName.isEmpty()) {
                labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelLocationInfo.setText("Please enter a location name.");
                return;
            }

            // Check if a similar location name (case-insensitive) already exists in the district
            Location existingLocation = findLocation(district, newLocationName);
            if (existingLocation != null) {
                labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelLocationInfo.setText("Location " + newLocationName + "' already exists in district '" + district.getDistrictName() + "'.");
                return;
            }

            // Create a new Location and add it to the district's location list
            Location newLocation = new Location(newLocationName);
            district.getLocation().insert(newLocation);
            nextLocationStack.clear();
            previousLocationStack.clear();

            locationNames.add(newLocationName);
            FXCollections.sort(locationNames);

            // Clear selection and input field after insertion
            locationComboBox.getSelectionModel().clearSelection();
            insField.clear();

            labelLocationInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            labelLocationInfo.setText("Location '" + newLocationName + "' inserted into district '" + district.getDistrictName() + "'.");

        });

        btnUpdateL.setOnAction(event -> {
            String oldLocationName = locationComboBox.getValue(); // Get the selected location to update
            String newLocationName = upNewField.getText().trim(); // Get the new location name from the input field

            if (oldLocationName == null || oldLocationName.isEmpty() || newLocationName.isEmpty()) {
                labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelLocationInfo.setText("Please select a location and enter a new location name.");
                return;
            }
            // Display a confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Update District");
            confirmAlert.setContentText("Are u sure to update district from " + oldLocationName + " to " + newLocationName + "?");

            // Show the confirmation dialog and wait for user's response
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                // Find the location within the district's location tree
                Location locationToUpdate = findLocation(district, oldLocationName);
                if (locationToUpdate == null) {
                    labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelLocationInfo.setText("Location '" + oldLocationName + "' not found in district '" + district.getDistrictName() + "'.");
                    return;
                }

                // Check if the new location name already exists in the district
                if (!oldLocationName.equalsIgnoreCase(newLocationName)) {
                    Location existingLocation = findLocation(district, newLocationName);
                    if (existingLocation != null) {
                        labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        labelLocationInfo.setText("Location '" + newLocationName + "' already exists in district '" + district.getDistrictName() + "'.");
                        return;
                    }
                }

                // Update the location name
                locationToUpdate.setLocation(newLocationName);
                nextLocationStack.clear();
                previousLocationStack.clear();

                // Update UI and display success message
                locationNames.remove(oldLocationName);
                locationNames.add(newLocationName);
                FXCollections.sort(locationComboBox.getItems());
                locationComboBox.getSelectionModel().clearSelection();

                upNewField.clear();
                labelLocationInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                labelLocationInfo.setText("Location updated: " + oldLocationName + " -> " + newLocationName);
            }
        });

        btnDeleteL.setOnAction(event -> {
            String locationName = locationComboBox.getValue();
            if (locationName.isBlank() || locationName.isEmpty()) {
                labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelLocationInfo.setText("Please enter both district name and location name.");
                return;
            }
            // Display a confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Update District");
            confirmAlert.setContentText("Are u sure to delete district  " + locationName + "?");

            // Show the confirmation dialog and wait for user's response
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Location locationToDelete = findLocation(district, locationName);
                if (locationToDelete == null) {
                    labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelLocationInfo.setText("Location not found in district: " + locationName);
                    return;
                }
                district.getLocation().delete(locationToDelete);
                nextLocationStack.clear();
                previousLocationStack.clear();
                locationNames.remove(locationName);
                locationComboBox.getSelectionModel().clearSelection();
                FXCollections.sort(locationComboBox.getItems());

                labelLocationInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                labelLocationInfo.setText("Location deleted: " + locationName);
            }
        });

        btnNextL.setOnAction(event -> {
            navigateNextL();
        });
        btnPreviousL.setOnAction(event -> {
            navigatePreviousL();
        });

        btnLoadLocation.setOnAction(event -> {
            if (!nextLocationStack.isEmpty()) {
                Location currentLocation = nextLocationStack.peek();
                if (currentLocation != null) {
                    populateStackInOrderM(currentLocation.getDate().getRoot());
                    displayLocationInfo(currentLocation);
                    martyrScreen(currentLocation);
                } else {
                    labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelLocationInfo.setText("Error: Current location is null.");
                }
            } else {
                labelLocationInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelLocationInfo.setText("No locations available to load.");
            }
        });

        btnBackL.setOnAction(e1 -> districtScreen());

        Scene scene = new Scene(lBorder, 1050, 450);
        stage.setScene(scene);
        stage.setTitle("Location Screen");
        stage.centerOnScreen();

    }


    public void martyrScreen(Location location) {

        Button btnInsertM = new Button("Insert New Martyr");
        Button btnUpdateM = new Button("Update Martyr");
        Button btnDeleteM = new Button("Delete Martyr");
        Button btnSearchMartyr = new Button("Search by part of name");
        Button btnNextDate = new Button("Next");
        Button btnPreviousDate = new Button("Previous");
        Button btnWrite = new Button("Write on file");




        Label labelName = new Label("Name:");
        Label labelAge = new Label("Age:");
        Label labelGender = new Label("Gender:");
        Label labelDate = new Label("Date:");


        Label labelFirstName = new Label("Name:");
        Label labelAgeUP = new Label("Age:");
        Label labelGenderUP = new Label("Gender:");
        Label labelDateUP = new Label("Date:");

        Label labelNameUpdate = new Label("Name:");
        Label labelAgeUpdate = new Label("Age:");
        Label labelGenderUpdate = new Label("Gender:");


        Label labelNameD = new Label("Name:");


        Label labelNameS = new Label("Name:");

        TextField textName = new TextField();
        textName.setPromptText("Name");
        TextField textAge = new TextField();
        textAge.setPromptText("Age");
        TextField textGender = new TextField();
        textGender.setPromptText("Gender");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date");


        TextField upNameField = new TextField();
        upNameField.setPromptText("Full Name");
        TextField upAgeField = new TextField();
        upAgeField.setPromptText("Age");
        TextField upGenderField = new TextField();
        upGenderField.setPromptText("Gender");
        DatePicker datePickerUP = new DatePicker();
        datePicker.setPromptText("Date");

        TextField textNameUpdate = new TextField(upNameField.getText());
        TextField textAgeUpdate = new TextField(String.valueOf(upAgeField.getText()));
        TextField textGenderUpdate = new TextField(upGenderField.getText());

        textNameUpdate.setPromptText("New Name");
        textAgeUpdate.setPromptText("New Age");
        textGenderUpdate.setPromptText("New Gender");

        TextField deleteFieldM = new TextField();
        deleteFieldM.setPromptText("Delete Martyr");


        TextField searchFieldM = new TextField();
        searchFieldM.setPromptText("Search by part of name");

        HBox buttonBox = new HBox(labelName, textName, labelAge, textAge, labelDate, datePicker, labelGender, textGender);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBoxUPNew = new HBox(labelNameUpdate, textNameUpdate, labelAgeUpdate, textAgeUpdate, labelGenderUpdate, textGenderUpdate);
        //buttonBoxUPNew.setAlignment(Pos.CENTER);
        buttonBoxUPNew.setSpacing(10);
        buttonBoxUPNew.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox2 = new HBox(labelFirstName, upNameField, labelAgeUP, upAgeField, labelGenderUP, upGenderField, labelDateUP, datePickerUP);
        //buttonBox2.setAlignment(Pos.CENTER);
        buttonBox2.setSpacing(10);
        buttonBox2.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox3 = new HBox(labelNameD, deleteFieldM);
        // buttonBox3.setAlignment(Pos.CENTER);
        buttonBox3.setSpacing(10);
        buttonBox3.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox4 = new HBox(labelNameS, searchFieldM);
        // buttonBox4.setAlignment(Pos.CENTER);
        buttonBox4.setSpacing(10);
        buttonBox4.setPadding(new Insets(10, 10, 10, 10));


        BorderPane martyrBorder = new BorderPane();

        GridPane martyrGrid = new GridPane();
        martyrGrid.setAlignment(Pos.CENTER);
        martyrGrid.setHgap(10);
        martyrGrid.setVgap(10);
        martyrGrid.add(btnInsertM, 0, 0);
        martyrGrid.add(buttonBox, 1, 0);

        martyrGrid.add(btnUpdateM, 0, 1);
        martyrGrid.add(buttonBox2, 1, 1);
        martyrGrid.add(buttonBoxUPNew, 1, 2);

        martyrGrid.add(btnDeleteM, 0, 3);
        martyrGrid.add(buttonBox3, 1, 3);

        martyrGrid.add(btnSearchMartyr, 0, 4);
        martyrGrid.add(buttonBox4, 1, 4);

        martyrGrid.add(btnNextDate, 1, 5);
        martyrGrid.add(btnPreviousDate,0,5);

        martyrGrid.add(labelMartyrInfo, 0, 6);
        GridPane.setColumnSpan(labelMartyrInfo, 3);
        GridPane.setHalignment(labelMartyrInfo, HPos.CENTER);
        GridPane.setValignment(labelMartyrInfo, VPos.CENTER);

        HBox hWrite = new HBox(15);
        hWrite.getChildren().addAll(btnWrite);
        hWrite.setAlignment(Pos.BOTTOM_CENTER);
        martyrBorder.setBottom(hWrite);

        martyrBorder.setCenter(martyrGrid);

        btnInsertM.setOnAction(event -> {
            String martyrName = textName.getText().trim();
            String ageStr = textAge.getText().trim();
            String gender = textGender.getText().trim();
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy")).toString();

            // Validate input (check for empty fields including DatePicker)
            if (martyrName.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || date.isEmpty()) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Please fill out all fields including the date.");
                return; // Exit the method if any field is empty
            }

            // Validate age (must be a valid integer)
            int martyrAge;
            try {
                martyrAge = Integer.parseInt(ageStr);
                if (martyrAge < 0 || martyrAge > 150) {
                    labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    labelMartyrInfo.setText("Invalid age. Age must be between 0 and 150.");
                    return; // Exit if age is out of range
                }
            } catch (NumberFormatException e) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Invalid age format. Please enter a valid number.");
                return; // Exit if age is not a valid number
            }
            // Validate gender
            if(gender.equalsIgnoreCase("M")|| gender.equalsIgnoreCase("F")) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Invalid gender. Gender must be either M or F.");
            }

            // Create a new martyr instance
            Martyr newMartyr = new Martyr(martyrName, martyrAge, gender);

            // Insert the new martyr into the selected location
            MDate martyrDate = new MDate(date);
            location.addMartyr(martyrDate, newMartyr);


            // Display success message
            labelMartyrInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            labelMartyrInfo.setText("New martyr record inserted successfully.");

            // Clear input fields
            textName.clear();
            textAge.clear();
            textGender.clear();
            datePicker.setValue(null); // Reset the DatePicker
        });

        btnUpdateM.setOnAction(event -> {
            String name = upNameField.getText().trim();
            String age = upAgeField.getText().trim();
            String gender = upGenderField.getText().trim();
            String date = datePickerUP.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy")).toString();
            String newName = textNameUpdate.getText().trim();
            String newAge = textAgeUpdate.getText().trim();
            String newGender = textGenderUpdate.getText().trim();


            // Validate input (check for empty fields)
            if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || date.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Missing Information");
                alert.setContentText("Please fill out all fields.");
                alert.showAndWait();
                return;
            }
            if(newGender.equalsIgnoreCase("M")|| newGender.equalsIgnoreCase("F")) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Invalid gender. Gender must be either M or F.");
            }

            // Validate age (must be a valid integer)
            int martyrAge;
            try {
                martyrAge = Integer.parseInt(age);
                if (martyrAge < 0 || martyrAge > 150) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Age");
                    alert.setContentText("Age must be between 0 and 150.");
                    alert.showAndWait();
                    return;
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Age Format");
                alert.setContentText("Please enter a valid age.");
                alert.showAndWait();
                return;
            }

            // Display confirmation dialog before clearing input fields
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Update Martyr Information");
            confirmation.setContentText("Are you sure you want to update this martyr's information?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    MDate martyrDate = findMartyrDate(location ,date);
                    Martyr martyrToUpdate = findMartyrByName(name, martyrDate);

                    // Update martyr
                    if (martyrDate != null && martyrToUpdate != null) {
                        updateMartyr(location ,martyrDate, martyrToUpdate, newName, Integer.parseInt(newAge), newGender);
                    }
                    // Clear input fields
                    upNameField.clear();
                    upAgeField.clear();
                    upGenderField.clear();
                    datePickerUP.setValue(null);
                    textNameUpdate.clear();
                    textAgeUpdate.clear();
                    textGenderUpdate.clear();
                }
            });
        });

        btnDeleteM.setOnAction(event -> {
            String martyrFullName = deleteFieldM.getText().trim(); // Full name of the martyr to delete

            if (martyrFullName.isEmpty()) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Please enter the full name of the martyr to delete.");
                return;
            }

            // Assuming 'location' is the Location object to operate on
            if (location == null) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Location object is null.");
                return;
            }

            // Find the martyr by full name within the location
            Martyr martyrToDelete = location.findMartyrByFullName(martyrFullName);
            if (martyrToDelete == null) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Martyr not found in location: " + martyrFullName);
                return;
            }

            // Attempt to delete the martyr from the location's list of martyrs
            boolean deletionSuccessful = location.removeMartyr(martyrToDelete);
            if (deletionSuccessful) {
                deleteFieldM.clear();
                labelMartyrInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Martyr '" + martyrFullName + "' deleted successfully.");
            } else {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Failed to delete martyr.");
            }
        });

        btnSearchMartyr.setOnAction(event -> {
            String partialName = searchFieldM.getText().trim();

            if (partialName.isEmpty()) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Please enter a part of the martyr's name to search.");
                return;
            }

            Queue<Martyr> martyrs = location.findMartyrByPartOfName(partialName);

            if (martyrs == null) {
                labelMartyrInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Martyr not found.");
            } else {

                String result = "";
                while (!martyrs.isEmpty()) {
                    Martyr martyr = martyrs.dequeue();
                    result += martyr.toString() + "\n";
                }

                labelMartyrInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                labelMartyrInfo.setText("Martyr found: " + result);
            }
        });

        btnNextDate.setOnAction(event -> {
            navigateNextM();
        });
        btnPreviousDate.setOnAction(event -> {
            navigatePreviousM();
        });
        btnWrite.setOnAction(event -> {
            writeFile();
        });


        Scene scene = new Scene(martyrBorder, 1500, 675);
        stage.setScene(scene);
        stage.setTitle("Martyr Screen");
        stage.centerOnScreen();

    }



    private void loadRecordsFromFile(File file) {

        districtTree.clear(); // Clear the district tree nodes

        try (Scanner scanner = new Scanner(file)) {
            scanner.nextLine(); // Skip the header line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    String name = parts[0].isEmpty() ? "Unknown" : parts[0];
                    String date = parts[1].isEmpty() ? "Unknown" : parts[1];
                    int age = 0;
                    try {
                        age = parts[2].isEmpty() ? -1 : Integer.parseInt(parts[2]);
                    } catch (NumberFormatException ex) {
                        resLabel.setText("Invalid age format for record: " + name + ". Setting age to -1.\n");
                        continue; // Skip invalid record
                    }
                    String location = parts[3].isEmpty() ? "Unknown" : parts[3];
                    String district = parts[4].isEmpty() ? "Unknown" : parts[4];
                    String gender = parts[5].equals("NA") ? "NA" : parts[5];


                    insertMartyrRecord(district, location, date, name, age, gender);
                    districtTree.traverseInOrder();
                }
            }
            resLabel.setText("Records loaded successfully.\n");
        } catch (IOException e) {
            resLabel.setText("Failed to load records.\n");
        }
    }

    public void insertMartyrRecord(String districtName, String locationName, String date, String martyrName, int age, String gender) {
        // Step 1: Insert or retrieve the District from the main district tree
        District district = findOrInsertDistrict(districtName);

        // Step 2: Insert or retrieve the Location from the location tree of the District
        Location location = findOrInsertLocation(district, locationName);

        // Step 3: Insert or retrieve the MDate from the date tree of the Location
        MDate mDate = findOrInsertMDate(location, date);

        // Step 4: Insert the Martyr into the linked list of Martyrs within the MDate
        Martyr martyr = addMartyrToMDate(mDate, martyrName, age, gender);


    }

    private District findOrInsertDistrict(String districtName) {
        // Create a District object to represent the district you want to find or insert
        District targetDistrict = new District(districtName);

        // Use the District object to find the corresponding node in the districtTree
        TNode<District> districtNode = districtTree.find(targetDistrict);

        if (districtNode != null) {
            // District node found, return its data
            return districtNode.getData();
        } else {
            // District not found, insert a new district node
            districtTree.insert(targetDistrict);
            return targetDistrict; // Return the newly inserted district
        }
    }


    private Location findOrInsertLocation(District district, String locationName) {
        // Get the Location tree from the District
        Tree<Location> locationTree = district.getLocation();

        // Create a target Location object with the specified name
        Location targetLocation = new Location(locationName);

        // Find the Location in the Location tree
        TNode<Location> locationNode = locationTree.find(targetLocation);

        if (locationNode != null) {
            // Location found, return its data
            return locationNode.getData();
        } else {
            // Location not found, insert a new Location node
            locationTree.insert(targetLocation);
            return targetLocation; // Return the newly inserted Location
        }
    }

    private MDate findOrInsertMDate(Location location, String date) {
        // Get the MDate tree from the Location
        Tree<MDate> dateTree = location.getDate();

        // Check if dateTree is null (Location's date tree has not been initialized)
        if (dateTree == null) {
            // Create a new Tree<MDate> and set it in the Location
            dateTree = new Tree<>();
            location.setDate(dateTree);
        }

        // Create a target MDate object with the specified date
        MDate targetMDate = new MDate(date);

        // Find the MDate in the MDate tree
        TNode<MDate> mDateNode = dateTree.find(targetMDate);

        if (mDateNode != null) {
            // MDate found, return its data
            return mDateNode.getData();
        } else {
            // MDate not found, insert a new MDate node
            dateTree.insert(targetMDate);
            return targetMDate; // Return the newly inserted MDate
        }
    }

    private Martyr addMartyrToMDate(MDate mDate, String martyrName, int age, String gender) {
        // Get the linked list of martyrs from the MDate
        SLinkedList<Martyr> martyrs = mDate.getMartyr();

        // Check if the martyr list is null (not initialized)
        if (martyrs == null) {
            // Initialize the martyr list
            martyrs = new SLinkedList<>();
            mDate.setMartyr(martyrs);
        }

        // Create a new Martyr object
        Martyr martyr = new Martyr(martyrName, age, gender);

        // Insert the martyr into the linked list of martyrs
        martyrs.insert(martyr);

        // Return the newly inserted Martyr object
        return martyr;
    }

    private void populateStackInOrder(TNode<District> node) {
        if (node == null) {
            return;
        }
        populateStackInOrder(node.getRight());
        districtStack.push(node.getData());
        districtNames.add(node.getData().getDistrictName());
        populateStackInOrder(node.getLeft());
    }


    private void populateStackInOrderM(TNode<MDate> nodeM) {
        if (nodeM == null) {
            return;
        }
        populateStackInOrderM(nodeM.getRight());
        nextMartyrDateStack.push(nodeM.getData());
        populateStackInOrderM(nodeM.getLeft());
    }

    private void navigateNext() {
        try {
            if (!districtStack.isEmpty()) {
                District nextDistrict = districtStack.pop();
                previousDistrict.push(nextDistrict);
                displayDistrictStatistics(districtStack.peek());
            }
        } catch (Exception e) {
            labelDistrictInfo.setText("No district available.");
        }
    }


   /* private void navigatePreviousL() {
        try {
            if (!previousLocationStack.isEmpty()) {
                Location previousLocation = previousLocationStack.pop();
                nextLocationStack.push(previousLocation);
                displayLocationInfo(nextLocationStack.peek());
            } else {
                labelLocationInfo.setText("No previous locations available.");
            }
        } catch (Exception e) {
            labelLocationInfo.setText("Error navigating to previous location.");
        }
    }*/


    public void navigateNextLocation() {
        if (!nextLocationStack.isEmpty()) {
            Location nextLocation = nextLocationStack.pop();
            previousLocationStack.push(nextLocation);
            displayLocationInfo(nextLocation);
        } else {
            labelLocationInfo.setText("No more locations available.");
        }
    }

    public void navigatePreviousLocation() {
        if (!previousLocationStack.isEmpty()) {
            Location previousLocation = previousLocationStack.pop();
            nextLocationStack.push(previousLocation);
            displayLocationInfo(previousLocation);
        } else {
            labelLocationInfo.setText("No previous locations available.");
        }
    }

    private void displayLocationInfo(Location location) {
        String locationName = location.getLocation();
        MDate earliestMartyrDate = location.getEarliestMartyrDate();
        MDate latestMartyrDate = location.getLatestMartyrDate();
        MDate maxMartyrsDate = location.getMaxMartyrsDate();

        String infoText = "Location: " + locationName + "\n";
        infoText += "Earliest Martyr Date: " + (earliestMartyrDate != null ? earliestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Latest Martyr Date: " + (latestMartyrDate != null ? latestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Date with Max Martyrs: " + (maxMartyrsDate != null ? maxMartyrsDate.getDate() : "N/A");

        labelLocationInfo.setText(infoText);
    }

    private void navigateNextL() {
        try {
            if (!nextLocationStack.isEmpty()) {
                Location nextLocation = nextLocationStack.pop();
                previousLocationStack.push(nextLocation);
                displayLocationInfo(nextLocation);
            } else {
                labelLocationInfo.setText("No more locations available.");
            }
        } catch (Exception e) {
            labelLocationInfo.setText("Error navigating to next location.");
        }
    }

    private void navigatePreviousL() {
        try {
            if (!previousLocationStack.isEmpty()) {
                Location previousLocation = previousLocationStack.pop();
                nextLocationStack.push(previousLocation);
                displayLocationInfo(previousLocation);
            } else {
                labelLocationInfo.setText("No previous locations available.");
            }
        } catch (Exception e) {
            labelLocationInfo.setText("Error navigating to previous location.");
        }
    }

  /*  private void displayLocationInfo(Location location) {
        // Display information for the current location
        String locationName = location.getLocation();
        MDate earliestMartyrDate = location.getEarliestMartyrDate();
        MDate latestMartyrDate = location.getLatestMartyrDate();
        MDate maxMartyrsDate = location.getMaxMartyrsDate();

        String infoText = "Location: " + locationName + "\n";
        infoText += "Earliest Martyr Date: " + (earliestMartyrDate != null ? earliestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Latest Martyr Date: " + (latestMartyrDate != null ? latestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Date with Max Martyrs: " + (maxMartyrsDate != null ? maxMartyrsDate.getDate() : "N/A");

        labelLocationInfo.setText(infoText);
    }*/


    private void navigateNextM() {
        try {
            if (!nextMartyrDateStack.isEmpty()) {
                MDate nextDate = nextMartyrDateStack.pop();
                previousMartyrDateStack.push(nextDate);
                System.out.println("0000000000000000000000000000000000000000000000000000000");
                displayMartyrDateStatistics(nextMartyrDateStack.peek());

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            labelMartyrInfo.setText("No date available.");
        }
    }

    private void navigatePreviousM() {
        try {
            if (!previousMartyrDateStack.isEmpty()) {
                MDate previousMartyrDate = previousMartyrDateStack.pop();
                nextMartyrDateStack.push(previousMartyrDate);
                displayMartyrDateStatistics(nextMartyrDateStack.peek());
            }
        } catch (Exception e) {
            labelDistrictInfo.setText("No district available.");
        }
    }

    private void navigatePrevious() {
        try {
            if (!previousDistrict.isEmpty()) {
                District previousDistrictObj = previousDistrict.pop();
                districtStack.push(previousDistrictObj);
                displayDistrictStatistics(districtStack.peek());
            }
        } catch (Exception e) {
            labelDistrictInfo.setText("No district available.");
        }
    }

    private void displayDistrictStatistics(District currentDistrict) {
        if (currentDistrict != null) {
            int totalMartyrs = currentDistrict.getTotalMartyrs();
            labelDistrictInfo.setText("Current District: " + currentDistrict.getDistrictName() +
                    "\nTotal Martyrs: " + totalMartyrs);
        }
    }

   /* private void displayLocationInfo(Location location) {
        // Display information for the current location
        String locationName = location.getLocation();
        MDate earliestMartyrDate = location.getEarliestMartyrDate();
        MDate latestMartyrDate = location.getLatestMartyrDate();
        MDate maxMartyrsDate = location.getMaxMartyrsDate();
        String infoText = "Location: " + locationName + "\n";
        infoText += "Earliest Martyr Date: " + (earliestMartyrDate != null ? earliestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Latest Martyr Date: " + (latestMartyrDate != null ? latestMartyrDate.getDate() : "N/A") + "\n";
        infoText += "Date with Max Martyrs: " + (maxMartyrsDate != null ? maxMartyrsDate.getDate() : "N/A");
        labelLocationInfo.setText(infoText);
    }
    */

    private ObservableList<String> locationNamesComboBox(District selectedDistrict) {
        ObservableList<String> locationNames = FXCollections.observableArrayList();

        if (selectedDistrict != null) {
            Tree<Location> locationTree = selectedDistrict.getLocation();
            TNode<Location> rootNode = locationTree.getRoot();
            traverseInOrder(rootNode, locationNames);
        }
        return locationNames;
    }

    private void traverseInOrder(TNode<Location> node, ObservableList<String> locationNames) {
        if (node == null) {
            return;
        }

        // Traverse left subtree
        traverseInOrder(node.getLeft(), locationNames);

        // Visit node itself
        locationNames.add(node.getData().getLocation());

        // Traverse right subtree
        traverseInOrder(node.getRight(), locationNames);
    }
    private String formatDate(LocalDate date) {
        // Define the desired date format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Format the LocalDate using the formatter
        return date.format(formatter);
    }
    private Martyr findMartyrByFullName(Location location, String fullName) {
        return location.findMartyrByFullName(fullName);
    }

    public void displayMartyrDateStatistics(MDate date) {
        if (date == null) {
            labelMartyrInfo.setText("No martyrs available for this date.");
            return;
        }

        // Retrieve location object for the date
        Location location = nextLocationStack.peek();
        if (location == null) {
            labelMartyrInfo.setText("No location found for the date.");
            return;
        }

        // Calculate statistics using location methods
        double averageAge = location.calculateAverageAge(date);
        Martyr youngestMartyr = location.findYoungestMartyr(date);
        Martyr oldestMartyr = location.findOldestMartyr(date);
        SLinkedList<Martyr> sortedMartyrsInfo = location.displayMartyrsSortedByName(date);

        // Prepare the display message
        String statisticsMessage = "Statistics for Date: " + date.getDate() + "\n"
                + "Average Martyr Age: " + String.format("%.2f", averageAge) + "\n"
                + "Youngest Martyr: " + (youngestMartyr != null ? youngestMartyr.toString() : "N/A") + "\n"
                + "Oldest Martyr: " + (oldestMartyr != null ? oldestMartyr.toString() : "N/A") + "\n"
                + "Martyrs Sorted by Name: \n" ;
        SNode<Martyr> head = sortedMartyrsInfo.getHead();
        while(head!= null) {
            statisticsMessage += head.getData().toString() + "\n";
            head = head.getNext();
        }
        // Set the display text to the label
        labelMartyrInfo.setText(statisticsMessage);
    }

    public Location findLocation(District district, String locationName) {
        if (district == null || locationName == null || locationName.isEmpty()) {
            return null;
        }
        Tree<Location> locationTree = district.getLocation();
        TNode<Location> rootNode = locationTree.getRoot();

        return findLocationInTree(rootNode, locationName);
    }

    private Location findLocationInTree(TNode<Location> node, String locationName) {
        if (node == null) {
            return null;
        }

        Location currentLocation = node.getData();

        if (currentLocation.getLocation().equalsIgnoreCase(locationName)) {
            return currentLocation;
        }

        Location leftResult = findLocationInTree(node.getLeft(), locationName);
        if (leftResult != null) {
            return leftResult;
        }

        return findLocationInTree(node.getRight(), locationName);
    }

    public void traverseLocationsLevelOrder(District district) {
        if (district == null || district.getLocation() == null || district.getLocation().getRoot() == null) {
            return;
        }

        nextLocationStack.clear();
        previousLocationStack.clear();
        TNode<Location> root = district.getLocation().getRoot();
        Queue<TNode<Location>> queue = new Queue<>();
        Stack<Location> tempStack = new Stack<>();
        queue.enqueue(root); // Enqueue the root node

        while (!queue.isEmpty()) {
            TNode<Location> tempNode = queue.dequeue();
            tempStack.push(tempNode.getData());
            System.out.println(tempNode.getData());

            if (tempNode.getLeft() != null) {
                queue.enqueue(tempNode.getLeft());
            }
            if (tempNode.getRight() != null) {
                queue.enqueue(tempNode.getRight());
            }

        }
        while (!tempStack.isEmpty()) {
            nextLocationStack.push(tempStack.pop());
        }

    }

    public void updateMartyr(Location location,MDate martyrDate, Martyr martyrToUpdate, String newName, int newAge, String newGender) {
        if (martyrToUpdate == null) {
            return; // No martyr to update
        }

        // Update the martyr's data
        martyrToUpdate.setName(newName);
        martyrToUpdate.setAge(newAge);
        martyrToUpdate.setGender(newGender);

        // Move the node to the correct position
        moveNodeToCorrectPosition(martyrDate, martyrToUpdate);
    }
    private void moveNodeToCorrectPosition(MDate martyrDate, Martyr martyrToUpdate) {
        SNode<Martyr> head = martyrDate.getMartyr().getHead();
        SNode<Martyr> prev = null;
        SNode<Martyr> current = head;

        // Find the node to update
        while (current != null && current.getData() != martyrToUpdate) {
            prev = current;
            current = current.getNext();
        }

        if (current == null) {
            // Node not found
            return;
        }

        // Remove the node from its current position
        if (prev != null) {
            prev.setNext(current.getNext());
        } else {
            // Update head if the node to remove is the head
            martyrDate.getMartyr().setHead(current.getNext());
        }

        // Reposition the node in the correct place
        SNode<Martyr> newPrev = null;
        SNode<Martyr> newCurrent = martyrDate.getMartyr().getHead();

        while (newCurrent != null && martyrToUpdate.compareTo(newCurrent.getData()) > 0) {
            newPrev = newCurrent;
            newCurrent = newCurrent.getNext();
        }

        // Insert the updated node in the correct position
        if (newPrev != null) {
            current.setNext(newPrev.getNext());
            newPrev.setNext(current);
        } else {
            current.setNext(martyrDate.getMartyr().getHead());
            martyrDate.getMartyr().setHead(current);
        }
    }
    private MDate findMartyrDate(Location location ,String date) {
        if (location == null || location.getDate() == null || location.getDate().getRoot() == null) {
            return null; // No dates available
        }

        return findMartyrDateHelper(location.getDate().getRoot(), date);
    }

    private MDate findMartyrDateHelper(TNode<MDate> currentNode, String date) {
        if (currentNode == null) {
            return null;
        }

        MDate currentMDate = currentNode.data;
        if (currentMDate != null && currentMDate.getDate().equals(date)) {
            return currentMDate; // Found the matching date
        }

        MDate leftSearch = findMartyrDateHelper(currentNode.left, date);
        if (leftSearch != null) {
            return leftSearch;
        }

        return findMartyrDateHelper(currentNode.right, date);
    }
    private Martyr findMartyrByName(String name, MDate martyrDate) {
        if (martyrDate == null || martyrDate.getMartyr() == null || martyrDate.getMartyr().isEmpty()) {
            return null; // No martyrs available for this date
        }

        SNode<Martyr> current = martyrDate.getMartyr().getHead();
        while (current != null) {
            Martyr currentMartyr = current.getData();
            if (currentMartyr != null && currentMartyr.getName().equalsIgnoreCase(name)) {
                return currentMartyr; // Found the martyr with the matching name
            }
            current = current.getNext();
        }

        return null; // Martyr not found
    }
    private void writeFile() {
        fileChooserWrite = new FileChooser();
        String result = "";
        fileChooserWrite.setTitle("Choose your file");
        fileChooserWrite.getExtensionFilters().add(new FileChooser.ExtensionFilter("File.csv", "*.csv"));
        file = fileChooserWrite.showOpenDialog(stage);
        if (file != null) {
            if (file.exists()) {
                try {
                    PrintWriter fileWriter = new PrintWriter(file);
                    fileWriter.println("Name,event,Age,location,District,Gender");
                    if (districtTree.getRoot() == null) return;
                    Queue queueDis = new Queue();
                    queueDis.enqueue(districtTree.getRoot());
                    while (!queueDis.isEmpty()) {
                        TNode tempDis = (TNode) queueDis.dequeue();
                        if (tempDis.getLeft() != null) queueDis.enqueue(tempDis.getLeft());
                        if (tempDis.getRight() != null) queueDis.enqueue(tempDis.getRight());

                        Queue queueLoc = new Queue();
                        queueLoc.enqueue(((District) tempDis.getData()).getLocation().getRoot());
                        while (!queueLoc.isEmpty()) {
                            TNode tempLoc = (TNode) queueLoc.dequeue();
                            if (tempLoc.getLeft() != null) queueLoc.enqueue(tempLoc.getLeft());
                            if (tempLoc.getRight() != null) queueLoc.enqueue(tempLoc.getRight());

                            Queue queueDate = new Queue();
                            queueDate.enqueue(((Location) tempLoc.getData()).getDate().getRoot());
                            while (!queueDate.isEmpty()) {
                                TNode tempDate = (TNode) queueDate.dequeue();
                                if (tempDate.getLeft() != null) queueDate.enqueue(tempDate.getLeft());
                                if (tempDate.getRight() != null) queueDate.enqueue(tempDate.getRight());
                                SNode snode = ((MDate) tempDate.getData()).getMartyr().getHead();
                                while (snode != null) {
                                    Martyr m = (Martyr) snode.getData();
                                    fileWriter.println(m.getName() + "," + tempDate.getData() + "," + m.getAge() + "," + tempLoc.getData() + "," + tempDis.getData() + "," + m.getGender());
                                    snode = snode.getNext();
                                }
                            }
                        }
                    }
                    fileWriter.close();
                } catch (IOException ex) {
                    System.out.println("Something wrong occured");
                }
            }

            labelMartyrInfo.setText("Write Successfully");
        }


    }

}