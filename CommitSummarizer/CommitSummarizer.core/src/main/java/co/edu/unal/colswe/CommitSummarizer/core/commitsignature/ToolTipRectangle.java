package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ToolTipRectangle {
  public static void main(String[] args) {
    Display display = new Display();
    final Color[] colors = { display.getSystemColor(SWT.COLOR_RED),
        display.getSystemColor(SWT.COLOR_GREEN), display.getSystemColor(SWT.COLOR_BLUE), };
    final Rectangle[] rects = { new Rectangle(10, 10, 30, 30), new Rectangle(20, 45, 25, 35),
        new Rectangle(80, 80, 10, 10), };
    final Shell shell = new Shell(display);
    Listener mouseListener = new Listener() {
      public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.MouseEnter:
        case SWT.MouseMove:
          for (int i = 0; i < rects.length; i++) {
            if (rects[i].contains(event.x, event.y)) {
              String text = "ToolTip " + i;
              if (!(text.equals(shell.getToolTipText()))) {
                shell.setToolTipText("ToolTip " + i);
              }
              return;
            }
          }
          shell.setToolTipText(null);
          break;
        }
      }
    };
    shell.addListener(SWT.MouseMove, mouseListener);
    shell.addListener(SWT.MouseEnter, mouseListener);
    shell.addListener(SWT.Paint, new Listener() {
      public void handleEvent(Event event) {
        GC gc = event.gc;
        for (int i = 0; i < rects.length; i++) {
          gc.setBackground(colors[i]);
          gc.fillRectangle(rects[i]);
          gc.drawRectangle(rects[i]);
        }
      }
    });
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}