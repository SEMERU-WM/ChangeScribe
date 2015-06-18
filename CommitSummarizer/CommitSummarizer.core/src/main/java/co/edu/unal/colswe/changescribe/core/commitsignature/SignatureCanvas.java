package co.edu.unal.colswe.changescribe.core.commitsignature;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import co.edu.unal.colswe.changescribe.core.Constants;
import co.edu.unal.colswe.changescribe.core.Messages;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;

/**
 * This class demonstrates a Canvas
 */
public class SignatureCanvas {

	private static final int Y_VALUE_STRING = 58;
	private static final int ZERO = 0;
	private static final double ONE_HUNDRED = 100.0;
	private TreeMap<MethodStereotype, Integer> signatureMap = new TreeMap<MethodStereotype, Integer>();
	private int width = 565;
	private int height = 40;
	private Composite composite;
	private Canvas canvas;
	private Shell shell;
	private double total;
	private List<Rectangle> rectangles = new LinkedList<Rectangle>();
	private ToolTip toolTip;
	private Rectangle currentRectangle;

	public SignatureCanvas(TreeMap<MethodStereotype, Integer> signatureMap,Composite composite, Shell shell) {
		super();
		this.setSignatureMap(signatureMap);
		this.composite = composite;
		this.shell = shell;
	}

	public void redraw() {
		if (getSignatureMap() != null) {
			canvas.getParent().layout(true, true);
			canvas.redraw();
			canvas.update();
		}
	}
	
	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public void createContents() {
		canvas = new Canvas(composite, SWT.NO_REDRAW_RESIZE);
		canvas.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		canvas.setSize(width, height);

		canvas.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
				e.gc.fillRectangle(0, 5, composite.getClientArea().width + 10, 50);

				createPercentageRule(e);
				// accessor and muttator different nuances of green and blue 
				// tan factory
				// Collaborational rose and turquoise
				// degenerate gray
				int accum = (int) ((int)(getShell().getSize().x - width) / 2.0);
				if (signatureMap != null) {
					rectangles = new LinkedList<Rectangle>();
					setTotal();
					for (final Entry<MethodStereotype, Integer> signature : getSignatureMap().entrySet()) {
						final double percentaje = Math.round((signature.getValue() * SignatureCanvas.ONE_HUNDRED) / total);
						Color color = null;
						
						if (signature.getKey() == MethodStereotype.ABSTRACT) {
							color = e.display.getSystemColor(SWT.COLOR_DARK_GRAY);
						} else if (signature.getKey() == MethodStereotype.EMPTY) {
							color = e.display.getSystemColor(SWT.COLOR_DARK_GRAY);
						} else if (signature.getKey() == MethodStereotype.INCIDENTAL) {
							color = e.display.getSystemColor(SWT.COLOR_DARK_GRAY);
						} else if (signature.getKey() == MethodStereotype.SET) {
							color = new Color(getShell().getDisplay(), 154, 204, 255);
						} else if (signature.getKey() == MethodStereotype.COMMAND) {
							color = new Color(getShell().getDisplay(), 0, 255, 255);
						} else if (signature.getKey() == MethodStereotype.NON_VOID_COMMAND) {
							color = new Color(getShell().getDisplay(), 22, 220, 220);
						} else if (signature.getKey() == MethodStereotype.CONSTRUCTOR) {
							color = new Color(getShell().getDisplay(), 214, 190, 154);
						} else if (signature.getKey() == MethodStereotype.COPY_CONSTRUCTOR) {
							color = new Color(getShell().getDisplay(), 197, 164, 114);
						} else if (signature.getKey() == MethodStereotype.DESTRUCTOR) {
							color = new Color(getShell().getDisplay(), 195, 135, 45);
						} else if (signature.getKey() == MethodStereotype.FACTORY) {
							color = new Color(getShell().getDisplay(), 194, 146, 76);
						} else if (signature.getKey() == MethodStereotype.GET) {
							color = new Color(getShell().getDisplay(), 204, 255, 204);
						} else if (signature.getKey() == MethodStereotype.PREDICATE) {
							color = new Color(getShell().getDisplay(), 154, 204, 0);
						} else if (signature.getKey() == MethodStereotype.VOID_ACCESSOR) {
							color = new Color(getShell().getDisplay(), 0, 255, 0);
						} else if (signature.getKey() == MethodStereotype.PROPERTY) {
							color = new Color(getShell().getDisplay(), 50, 154, 101);
						} else if (signature.getKey() == MethodStereotype.COLLABORATOR) {
							color = new Color(getShell().getDisplay(), 242, 120, 120);
						} else if (signature.getKey() == MethodStereotype.CONTROLLER) {
							color = new Color(getShell().getDisplay(), 252, 127, 127);
						} else if (signature.getKey() == MethodStereotype.LOCAL_CONTROLLER) {
							color = new Color(getShell().getDisplay(), 255, 166, 166);
						} 
						createSignature(e, accum, signature, percentaje, color);
						accum = accum + ((int)Math.round((percentaje * getWidth()) / SignatureCanvas.ONE_HUNDRED));
					}
				}
			}

		});
		
		canvas.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event e) {
				int counter = SignatureCanvas.ZERO;
				for(Rectangle rect : rectangles) {
					if(rect.contains(e.x, e.y)) {
						
						if(currentRectangle == null || currentRectangle != rect){
							if(toolTip != null && toolTip.isVisible()) {
								toolTip.setVisible(false);
							}
							toolTip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
							currentRectangle = rect;
							toolTip.setVisible(false);
							toolTip.setText(Messages.SignatureCanvas_StereotypeDescription);
							toolTip.setMessage(getToolTipInfo(counter) + Constants.NEW_LINE + getToolTipMessage(counter));
							toolTip.setVisible(true);
							toolTip.setAutoHide(true);
						} else {
							toolTip.setVisible(true);
						}
					}
					counter++;
				}
			}
		});
		
		canvas.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event e) {
				boolean visible = false;
				int i = 0;
				for(Rectangle rect : rectangles) {
					if(rect.contains(e.x, e.y)) {
						visible = true;
						break;
					}
					if(i == rectangles.size() - 1 && visible == false) {
						visible = false;
					}
					i++;
				}
				if(toolTip != null) {
					toolTip.setVisible(visible);
				}
			}
		});
	}
	
	public String getToolTipInfo(int value) {
		String text = Messages.SignatureCanvas_Stereotype + 
				((MethodStereotype) signatureMap.keySet().toArray()[value]).name().replace(Constants.UNDERSCORE, Constants.SPACE) +
				Constants.NEW_LINE; 
		text += Messages.SignatureCanvas_Amount + signatureMap.values().toArray()[value];
		
		return text;
	}
	
	public String getToolTipMessage(int value) {
		return ((MethodStereotype) signatureMap.keySet().toArray()[value]).getSubcategory().getDescription();
	}
	
	public void createPercentageRule(Event e) {
		int accumulate = (int) ((int)(getShell().getSize().x - width) / 2.0);
		int initial = accumulate;
		int i = 0;
		while (accumulate <= width + initial) {
			
			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_GRAY));
			e.gc.drawLine(accumulate, 5, accumulate, 55);
			
			if(accumulate == initial) {
				e.gc.drawString(Constants.EMPTY_STRING + i + Constants.PERCENTAJE, accumulate, SignatureCanvas.Y_VALUE_STRING, true); 
			} else {
				e.gc.drawString(Constants.EMPTY_STRING + i + Constants.PERCENTAJE, accumulate - 10, SignatureCanvas.Y_VALUE_STRING, true);
			}
			accumulate = accumulate + width / 10;
			i = i + 10;
		}
	}
	
	public void createSignature(Event e, int accum, final Entry<MethodStereotype, Integer> signature, final double percentaje, final Color color) {
		e.gc.setBackground(color);
		int widthValue = (int)Math.round((percentaje * getWidth())/100);
		if(accum + widthValue > getWidth() + 20) {
			int ruleLenght = ((getShell().getSize().x - width) / 2) + width ;
			widthValue = ruleLenght - accum - 5;
		} else {
			widthValue = (int) ((width * percentaje) / SignatureCanvas.ONE_HUNDRED);			
		}

		Rectangle rect = new Rectangle(accum, 20, widthValue ,getHeight());
		rectangles.add(rect);
		
		e.gc.fillRectangle(accum, 15, widthValue ,getHeight());
		e.gc.textExtent(signature.getKey().name());
		e.gc.drawText(signature.getValue() + 
				Constants.EMPTY_STRING, 
				(accum + (int) Math.round((percentaje * getWidth()) / SignatureCanvas.ONE_HUNDRED) / 2), 
				20); 
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public TreeMap<MethodStereotype, Integer> getSignatureMap() {
		return signatureMap;
	}

	public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap) {
		this.signatureMap = signatureMap;
	}

	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal() {
		this.total = 0;
		for (final Entry<MethodStereotype, Integer> signature : getSignatureMap().entrySet()) {
			this.total += signature.getValue();
		}
	}
}