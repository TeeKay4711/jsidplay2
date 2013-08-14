package ui.joysticksettings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import sidplay.ini.intf.IJoystickSection;
import ui.common.C64Stage;
import ui.common.TypeTextField;
import ui.events.UIEvent;

public class JoystickSettings extends C64Stage {

	@FXML
	private CheckBox activateJoy1, activateJoy2;
	@FXML
	private ComboBox<Controller> device1, device2;
	@FXML
	private ComboBox<Component> up1, down1, left1, right1, fire1;
	@FXML
	private TypeTextField up1Value, down1Value, left1Value, right1Value,
			fire1Value;
	@FXML
	private ComboBox<Component> up2, down2, left2, right2, fire2;
	@FXML
	private TypeTextField up2Value, down2Value, left2Value, right2Value,
			fire2Value;
	@FXML
	private TableView<Component> testTable1, testTable2;

	private ObservableList<Controller> devices = FXCollections
			.<Controller> observableArrayList();

	private ObservableList<Component> components1 = FXCollections
			.<Component> observableArrayList();

	private ObservableList<Component> components2 = FXCollections
			.<Component> observableArrayList();

	private Timeline timer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		testTable1.setItems(components1);
		device1.setItems(devices);
		up1.setItems(components1);
		down1.setItems(components1);
		left1.setItems(components1);
		right1.setItems(components1);
		fire1.setItems(components1);

		testTable2.setItems(components2);
		up2.setItems(components2);
		down2.setItems(components2);
		left2.setItems(components2);
		right2.setItems(components2);
		fire2.setItems(components2);
		device2.setItems(devices);

		ControllerEnvironment controllerEnv = ControllerEnvironment
				.getDefaultEnvironment();
		Controller[] controllers = controllerEnv.getControllers();

		devices.addAll(controllers);

		IJoystickSection joystickSettings = getConfig().getJoystick();

		activateJoy1.setSelected(getPlayer().getC64().isJoystickConnected(0));
		select(controllers, joystickSettings.getDeviceName1(), device1);

		Controller controller1 = device1.getSelectionModel().getSelectedItem();
		select(controller1, joystickSettings.getComponentNameUp1(), up1);
		select(controller1, joystickSettings.getComponentNameDown1(), down1);
		select(controller1, joystickSettings.getComponentNameLeft1(), left1);
		select(controller1, joystickSettings.getComponentNameRight1(), right1);
		select(controller1, joystickSettings.getComponentNameBtn1(), fire1);

		up1Value.setValue(joystickSettings.getComponentValueUp1());
		down1Value.setValue(joystickSettings.getComponentValueDown1());
		left1Value.setValue(joystickSettings.getComponentValueLeft1());
		right1Value.setValue(joystickSettings.getComponentValueRight1());
		fire1Value.setValue(joystickSettings.getComponentValueBtn1());

		activateJoy2.setSelected(getPlayer().getC64().isJoystickConnected(1));
		select(controllers, joystickSettings.getDeviceName2(), device2);

		Controller controller2 = device2.getSelectionModel().getSelectedItem();
		select(controller2, joystickSettings.getComponentNameUp2(), up2);
		select(controller2, joystickSettings.getComponentNameDown2(), down2);
		select(controller2, joystickSettings.getComponentNameLeft2(), left2);
		select(controller2, joystickSettings.getComponentNameRight2(), right2);
		select(controller2, joystickSettings.getComponentNameBtn2(), fire2);

		up2Value.setValue(joystickSettings.getComponentValueUp2());
		down2Value.setValue(joystickSettings.getComponentValueDown2());
		left2Value.setValue(joystickSettings.getComponentValueLeft2());
		right2Value.setValue(joystickSettings.getComponentValueRight2());
		fire2Value.setValue(joystickSettings.getComponentValueBtn2());

		// periodically update joystick test tables
		final Duration oneFrameAmt = Duration.millis(1000);
		final KeyFrame oneFrame = new KeyFrame(oneFrameAmt,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent evt) {
						if (activateJoy1.isSelected()) {
							// XXX better way to update table?
							testTable1.getColumns().get(1).setVisible(false);
							testTable1.getColumns().get(1).setVisible(true);
						}
						if (activateJoy2.isSelected()) {
							// XXX better way to update table?
							testTable2.getColumns().get(1).setVisible(false);
							testTable2.getColumns().get(1).setVisible(true);
						}
					}
				});
		timer = TimelineBuilder.create().cycleCount(Animation.INDEFINITE)
				.keyFrames(oneFrame).build();
		timer.playFromStart();
	}

	@Override
	protected void doCloseWindow() {
		timer.stop();
	}

	private void select(Controller[] controllers, String controllerName,
			ComboBox<Controller> comboBox) {
		if (controllerName != null) {
			for (Controller controller : controllers) {
				if (controller.getName().equals(controllerName)) {
					comboBox.getSelectionModel().select(controller);
					break;
				}
			}
		}
	}

	private void select(Controller controller, String componentName,
			ComboBox<Component> combobox) {
		if (componentName != null && controller != null) {
			for (Component component : controller.getComponents()) {
				if (component.getName().equals(componentName)) {
					combobox.getSelectionModel().select(component);
					break;
				}
			}
		}
	}

	@FXML
	private void doActivateJoy1() {
		getPlayer().getC64().setJoystick(
				0,
				activateJoy1.isSelected() ? new JoystickReader(device1
						.getSelectionModel().getSelectedItem(), up1
						.getSelectionModel().getSelectedItem(), down1
						.getSelectionModel().getSelectedItem(), left1
						.getSelectionModel().getSelectedItem(), right1
						.getSelectionModel().getSelectedItem(), fire1
						.getSelectionModel().getSelectedItem(),
						(float) up1Value.getValue(), (float) down1Value
								.getValue(), (float) left1Value.getValue(),
						(float) right1Value.getValue(), (float) fire1Value
								.getValue()) : null);
	}

	@FXML
	private void chooseDevice1() {
		Controller controller1 = device1.getSelectionModel().getSelectedItem();
		components1.setAll(controller1.getComponents());
		getConfig().getJoystick().setDeviceName1(controller1.getName());
	}

	@FXML
	private void chooseUp1() {
		getConfig().getJoystick().setComponentNameUp1(
				up1.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseUp1Value() {
		getConfig().getJoystick().setComponentValueUp1(
				(float) up1Value.getValue());
	}

	@FXML
	private void chooseDown1() {
		getConfig().getJoystick().setComponentNameDown1(
				down1.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseDown1Value() {
		getConfig().getJoystick().setComponentValueDown1(
				(float) down1Value.getValue());
	}

	@FXML
	private void chooseLeft1() {
		getConfig().getJoystick().setComponentNameLeft1(
				left1.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseLeft1Value() {
		getConfig().getJoystick().setComponentValueLeft1(
				(float) left1Value.getValue());
	}

	@FXML
	private void chooseRight1() {
		getConfig().getJoystick().setComponentNameRight1(
				right1.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseRight1Value() {
		getConfig().getJoystick().setComponentValueRight1(
				(float) right1Value.getValue());
	}

	@FXML
	private void chooseFire1() {
		getConfig().getJoystick().setComponentNameBtn1(
				fire1.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseFire1Value() {
		getConfig().getJoystick().setComponentValueBtn1(
				(float) fire1Value.getValue());
	}

	@FXML
	private void doActivateJoy2() {
		getPlayer().getC64().setJoystick(
				1,
				activateJoy2.isSelected() ? new JoystickReader(device2
						.getSelectionModel().getSelectedItem(), up2
						.getSelectionModel().getSelectedItem(), down2
						.getSelectionModel().getSelectedItem(), left2
						.getSelectionModel().getSelectedItem(), right2
						.getSelectionModel().getSelectedItem(), fire2
						.getSelectionModel().getSelectedItem(),
						(float) up2Value.getValue(), (float) down2Value
								.getValue(), (float) left2Value.getValue(),
						(float) right2Value.getValue(), (float) fire2Value
								.getValue()) : null);
	}

	@FXML
	private void chooseDevice2() {
		Controller controller2 = device2.getSelectionModel().getSelectedItem();
		components2.setAll(controller2.getComponents());
		getConfig().getJoystick().setDeviceName2(controller2.getName());
	}

	@FXML
	private void chooseUp2() {
		getConfig().getJoystick().setComponentNameUp2(
				up2.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseUp2Value() {
		getConfig().getJoystick().setComponentValueUp2(
				(float) up2Value.getValue());
	}

	@FXML
	private void chooseDown2() {
		getConfig().getJoystick().setComponentNameDown2(
				down2.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseDown2Value() {
		getConfig().getJoystick().setComponentValueDown2(
				(float) down2Value.getValue());
	}

	@FXML
	private void chooseLeft2() {
		getConfig().getJoystick().setComponentNameLeft2(
				left2.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseLeft2Value() {
		getConfig().getJoystick().setComponentValueLeft2(
				(float) left2Value.getValue());
	}

	@FXML
	private void chooseRight2() {
		getConfig().getJoystick().setComponentNameRight2(
				right2.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseRight2Value() {
		getConfig().getJoystick().setComponentValueRight2(
				(float) right2Value.getValue());
	}

	@FXML
	private void chooseFire2() {
		getConfig().getJoystick().setComponentNameBtn2(
				fire2.getSelectionModel().getSelectedItem().getIdentifier()
						.getName());
	}

	@FXML
	private void chooseFire2Value() {
		getConfig().getJoystick().setComponentValueBtn2(
				(float) fire2Value.getValue());
	}

	@Override
	public void notify(final UIEvent evt) {
	}

}
