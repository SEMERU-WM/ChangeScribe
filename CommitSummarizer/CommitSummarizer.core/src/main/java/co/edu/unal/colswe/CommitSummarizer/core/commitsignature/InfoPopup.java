package co.edu.unal.colswe.CommitSummarizer.core.commitsignature;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This class is used to create a popup dialog
 * box to display the contents as information
 * to the user. This dialog is same as infopopup
 * dialog provided by Eclipse.
 * @author Debadatta Mishra(PIKU)
 * 
 *
 */
public final class InfoPopup extends PopupDialog
{
	/**
	 * The text control that displays the text.
	 */
	private Text text;
	
	/**
	 * The String shown in the popup.
	 */
	private String contents = "";
	
	/**
	 * Object of type {@link Rectangle}
	 */
	private Rectangle rectangle = null;
	
	
	/**Default constructor
	 * @param parent of type {@link Shell}
	 * @param rectangle of type {@link Rectangle}
	 * @param headerString of type String indicating the header
	 * @param footerString of type String indicating the footer
	 */
	public InfoPopup( Shell parent , Rectangle rectangle , String headerString , String footerString)
	{
		super(parent, PopupDialog.HOVER_SHELLSTYLE, false, false, true, false, headerString,footerString);
		this.rectangle = rectangle;
	}
	
	/**
	 * This method is used to show the animation
	 * by decreasing the x and y coordinates and
	 * by setting the size dynamically.
	 * @param shell of type {@link Shell}
	 */
	private static void doAnimation( Shell shell )
	{
		Point shellArea = shell.getSize();
		int x = shellArea.x;
		int y = shellArea.y;
		while( x != -200 )
		{
			try
			{
				shell.setSize(x--, y--);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	protected void handleShellCloseEvent() 
	{
		//Comment out the following if do not want any kind of animated effect.
		doAnimation(getShell());
		super.handleShellCloseEvent();
	}
	
//	protected boolean hasTitleArea()
//	{
//		return false;
//	}
	
//	private class ExitAction extends Action
//	{
//		ExitAction()
//		{
//			super("Close", IAction.AS_PUSH_BUTTON);
//		}
//
//		public void run()
//		{
//			close();
//		}
//	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createTitleMenuArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createTitleMenuArea(Composite arg0) 
	{
		Control ctrl = super.createTitleMenuArea(arg0);
		Composite composite = (Composite)ctrl;
		Control[] ctrls = composite.getChildren();
		
		ToolBar toolBar = (ToolBar)ctrls[1];
		ToolItem[] toolItems = toolBar.getItems();
		toolItems[0].setImage(JFaceResources.getImage(Dialog.DLG_IMG_ERROR));
		
		return ctrl;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#fillDialogMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillDialogMenu(IMenuManager dialogMenu) 
	{
		dialogMenu.addMenuListener( new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager arg0) 
			{
//				close();
				handleShellCloseEvent();
			}
		}
		);
//		dialogMenu.add(new ExitAction());
	}

	/*
	 * Create a text control for showing the info about a proposal.
	 */
	protected Control createDialogArea(Composite parent)
	{
		text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP
				| SWT.NO_FOCUS);
		text.setText(contents);
		return text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#adjustBounds()
	 */
	protected void adjustBounds()
	{
		Point pt = getShell().getDisplay().getCursorLocation();
		getShell().setBounds(pt.x,pt.y,rectangle.width,rectangle.height);
	}
	
	/**Method to set the text contents of the InfoPop dialog
	 * @param textContents of type String indicating the message
	 */
	public void setText( String textContents )
	{
		this.contents = textContents;
	}
	
}