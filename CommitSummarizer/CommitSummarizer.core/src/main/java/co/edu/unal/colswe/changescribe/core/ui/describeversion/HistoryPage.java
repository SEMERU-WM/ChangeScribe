package co.edu.unal.colswe.changescribe.core.ui.describeversion;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import co.edu.unal.colswe.changescribe.core.git.CommitWrapper;

public class HistoryPage extends WizardPage {
	private Composite container;
	private TableViewer viewer;
	private List<CommitWrapper> commits;

	public HistoryPage() {
		super("Select a git commit");
		setTitle("Select a git commit");
		setDescription("Select a commit to generate the commit message");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		createViewer(container);
		
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}
	
	private void createViewer(Composite parent) {
	    viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
	        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
	    createColumns(parent, viewer);
	    final Table table = viewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    viewer.setContentProvider(new ArrayContentProvider());
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				if(null != viewer.getTable() && viewer.getTable().getSelectionIndex() > 0) {
					setPageComplete(true);
				}
			}
		});
	    // get the content for the viewer, setInput will call getElements in the
	    // contentProvider
	    viewer.setInput(commits);
	    // make the selection available to other views
	    // define layout for the viewer
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    viewer.getControl().setLayoutData(gridData);
	  }
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
	    String[] titles = { "Commit Id", "Author", "Message" };
	    int[] bounds = { 100, 100, 100, 100 };

	    // first column is for the commit id
	    TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        CommitWrapper p = (CommitWrapper) element;
	        return p.getCommit().getId().getName();
	      }
	    });

	    // second column is for the author name
	    col = createTableViewerColumn(titles[1], bounds[1], 1);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  CommitWrapper p = (CommitWrapper) element;
	        return p.getCommit().getAuthorIdent().getName();
	      }
	    });

	    // now the short message
	    col = createTableViewerColumn(titles[2], bounds[2], 2);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  CommitWrapper p = (CommitWrapper) element;
	          return p.getCommit().getShortMessage();
	      }
	    });
	  }

	  private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
	        SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);
	    return viewerColumn;
	  }

	public List<CommitWrapper> getCommits() {
		return commits;
	}

	public void setCommits(List<CommitWrapper> commits) {
		this.commits = commits;
	}
}
