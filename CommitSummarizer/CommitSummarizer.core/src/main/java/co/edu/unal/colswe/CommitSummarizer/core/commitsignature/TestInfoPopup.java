package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**This is the test harness class to test the infopopup dialog.
 * @author Debadatta Mishra(PIKU)
 *
 */
public class TestInfoPopup
{
	public static void main(String[] args) 
	{
		final Display display = new Display ();
		final Shell shell = new Shell (display, SWT.DIALOG_TRIM); 
		
		Button btn = new Button( shell , SWT.PUSH );
		btn.setText("Press to see the InfoPopup");
		btn.setBounds(90, 10, 200, 30);
		
		btn.addSelectionListener( new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent se) 
			{
				//You can set the sixe of the Rectangle
				Rectangle rect = new Rectangle(110,220,200,110);
				InfoPopup pop = new InfoPopup( new Shell() , rect ,"Information for you","Select and press ESC to close");
				pop.setText("This is a special case of info popup dialog box which is similar to Eclipse InfoPopup.");
				pop.open();
			}
		}
		);
		shell.setSize(400, 200);
		shell.open ();
		while (!shell.isDisposed ()) 
		{
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}