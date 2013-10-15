package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTApp {

    private Shell shell;

    public SWTApp(Display display) {

        shell = new Shell(display);

        shell.addPaintListener(new ColorsPaintListener());

        shell.setText("Colors");
        shell.setSize(400, 200);
        shell.setLocation(300, 300);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private class ColorsPaintListener implements PaintListener {

        public void paintControl(PaintEvent e) {

            drawRectangles(e);

            e.gc.dispose();
        }
    }

    private void drawRectangles(PaintEvent e) {
        Color c1 = new Color(e.display, 50, 50, 200);
        e.gc.setBackground(c1);
        e.gc.fillRectangle(10, 15, 90, 60);

        Color c2 = new Color(e.display, 105, 90, 60);
        e.gc.setBackground(c2);
        e.gc.fillRectangle(130, 15, 90, 60);

        Color c3 = new Color(e.display, 33, 200, 100);
        e.gc.setBackground(c3);
        e.gc.fillRectangle(250, 15, 90, 60);

        c1.dispose();
        c2.dispose();
        c3.dispose();
    }

    public static void main(String[] args) {
        Display display = new Display();
        new SWTApp(display);
        display.dispose();
    }
}