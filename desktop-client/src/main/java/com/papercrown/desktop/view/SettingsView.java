package com.papercrown.desktop.view;

import com.papercrown.desktop.service.BackendClient;
import com.papercrown.desktop.util.AudioManager;
import com.papercrown.desktop.viewmodel.SettingsViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class SettingsView extends VBox {

    private final SettingsViewModel vm;
    private final Consumer<Boolean> onFullscreenToggle;

    public SettingsView(BackendClient client, AudioManager audioManager, Consumer<Boolean> onFullscreenToggle, Consumer<Boolean> onAnimationChanged) {
        this.vm = new SettingsViewModel(client, audioManager);
        this.onFullscreenToggle = onFullscreenToggle;

        getStyleClass().add("page-view");
        setPadding(new Insets(32));
        setSpacing(24);

        Label title = new Label("Settings");
        title.getStyleClass().add("page-title");

        VBox settingsList = new VBox(16);
        settingsList.setMaxWidth(500);

        settingsList.getChildren().addAll(
                createFullscreenSetting(),
                createVolumeSetting(),
                createSoundSetting(),
                createAnimationSetting(),
                createQuitSetting()
        );

        getChildren().addAll(title, settingsList);

        vm.fullscreen.addListener((obs, old, val) -> onFullscreenToggle.accept(val));
        vm.animationEnabled.addListener((obs, old, val) -> onAnimationChanged.accept(val));

        vm.load();
    }

    private VBox createFullscreenSetting() {
        HBox row = createSettingRow("Fullscreen", "fas-expand");

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(vm.fullscreen);
        row.getChildren().add(checkBox);

        VBox wrapper = new VBox(row);
        return wrapper;
    }

    private VBox createVolumeSetting() {
        HBox row = createSettingRow("Master Volume", "fas-volume-up");

        Slider slider = new Slider(0, 1, 0.5);
        slider.setPrefWidth(200);
        slider.valueProperty().bindBidirectional(vm.masterVolume);

        Label valueLabel = new Label(String.format("%.0f%%", vm.masterVolume.get() * 100));
        valueLabel.setStyle("-fx-text-fill: #8888a0; -fx-font-size: 13px; -fx-min-width: 40;");
        vm.masterVolume.addListener((obs, old, val) ->
                valueLabel.setText(String.format("%.0f%%", val.doubleValue() * 100)));

        row.getChildren().addAll(slider, valueLabel);

        VBox wrapper = new VBox(row);
        return wrapper;
    }

    private VBox createSoundSetting() {
        HBox row = createSettingRow("Sound Effects", "fas-music");

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(vm.soundEnabled);
        row.getChildren().add(checkBox);

        VBox wrapper = new VBox(row);
        return wrapper;
    }

    private VBox createAnimationSetting() {
        HBox row = createSettingRow("Animations", "fas-sync-alt");

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().bindBidirectional(vm.animationEnabled);
        row.getChildren().add(checkBox);

        VBox wrapper = new VBox(row);
        return wrapper;
    }

    private VBox createQuitSetting() {
        Button quitBtn = new Button("Quit Game");
        quitBtn.setStyle("-fx-background-color: #ff3c3c; -fx-text-fill: white; -fx-padding: 12 24; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-size: 14px;");
        quitBtn.setMaxWidth(Double.MAX_VALUE); // stretch to width
        quitBtn.setOnAction(e -> showQuitConfirmModal());

        FontIcon icon = new FontIcon(FontAwesomeSolid.POWER_OFF);
        icon.setIconSize(16);
        icon.setIconColor(javafx.scene.paint.Color.WHITE);
        quitBtn.setGraphic(icon);
        quitBtn.setGraphicTextGap(10);

        return new VBox(quitBtn);
    }

    private void showQuitConfirmModal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to quit Paper Crown?");
        
        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: #1a1a24; -fx-base: #1a1a24;");
        dp.lookup(".content.label").setStyle("-fx-text-fill: #e8e8f0;");
        
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                javafx.application.Platform.exit();
                System.exit(0);
            }
        });
    }

    private HBox createSettingRow(String label, String iconStr) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #1a1a24; -fx-background-radius: 8; -fx-border-color: #2a2a38; -fx-border-radius: 8;");

        FontIcon icon = new FontIcon(switch (iconStr) {
            case "fas-expand" -> FontAwesomeSolid.EXPAND;
            case "fas-volume-up" -> FontAwesomeSolid.VOLUME_UP;
            case "fas-music" -> FontAwesomeSolid.MUSIC;
            case "fas-sync-alt" -> FontAwesomeSolid.SYNC_ALT;
            case "fas-power-off" -> FontAwesomeSolid.POWER_OFF;
            default -> FontAwesomeSolid.COG;
        });
        icon.setIconSize(18);

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-text-fill: #d4d4dc; -fx-font-size: 14px;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        row.getChildren().addAll(icon, nameLabel);
        return row;
    }
}
