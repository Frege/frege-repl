package frege.repl.nativedeps;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import static javafx.concurrent.Worker.State.FAILED;

public class Browser extends JFrame {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;

    private final JPanel panel = new JPanel(new BorderLayout());

    public Browser() {
        super();
        initComponents();
    }

    private void initComponents() {
        createScene();
        panel.add(jfxPanel, BorderLayout.CENTER);
        getContentPane().add(panel);

        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
    }

    private void createScene() {
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();

            engine.getLoadWorker()
                .exceptionProperty()
                .addListener((o, old, value) -> {
                    if (engine.getLoadWorker().getState() == FAILED) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            panel,
                            (value != null) ?
                                engine.getLocation() + "\n" + value.getMessage() :
                                engine.getLocation() + "\nUnexpected error.",
                            "Loading error...",
                            JOptionPane.ERROR_MESSAGE));
                    }
                });
            jfxPanel.setScene(new Scene(view));
        });
    }

    public void loadURL(final String url) {
        Platform.runLater(() -> {
            String tmp = toURL(url);

            if (tmp == null) {
                tmp = toURL("http://" + url);
            }

            engine.load(tmp);
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

    public static void show(String title, String url) {
        SwingUtilities.invokeLater(() -> {
            Browser browser = new Browser();
            browser.setVisible(true);
            browser.loadURL(url);
        });
    }
}
