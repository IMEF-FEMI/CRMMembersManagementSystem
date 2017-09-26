package ui.notification;

import javafx.scene.image.Image;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.nio.file.Path;

/**
 * Created by CRM on 8/5/2017.
 */
public class Notification {

    public  static void customNotify(String title, String message, NotificationType type, String path) {
        TrayNotification trayNotification = new TrayNotification();
        trayNotification.setTitle(title);
        trayNotification.setMessage(message);
        trayNotification.setNotificationType(type);
        trayNotification.setImage(new Image(path));
        trayNotification.setAnimationType(AnimationType.POPUP);
        trayNotification.showAndDismiss(Duration.seconds(3));
    }

    public static void notify(String title, String message, NotificationType type) {
        TrayNotification trayNotification = new TrayNotification();
        trayNotification.setTitle(title);
        trayNotification.setMessage(message);
        trayNotification.setNotificationType(type);
        trayNotification.setAnimationType(AnimationType.POPUP);
        trayNotification.showAndDismiss(Duration.seconds(3));
    }
}
