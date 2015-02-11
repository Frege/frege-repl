package frege.repl;

import frege.runtime.Lambda;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class NativeWrapper {

    private final static Integer guiWorld = 1;

    public static Action swingAction(final Lambda arg1) {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                arg1.apply(actionEvent).apply(guiWorld).result().call();
            }
        };
    }

    public static WindowListener windowListener(final Lambda arg) {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                arg.apply(e).apply(guiWorld).result().call();
            }
        };
    }
}
