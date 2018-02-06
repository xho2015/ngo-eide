package org.ngo.eide.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class NgoEngPerspective implements IPerspectiveFactory {

	private IPageLayout factory;

	public NgoEngPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
		removeToolBar();
	}

	private void addViews() {
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.

		IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
				IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		// bottom.addView("org.eclipse.team.ui.GenericHistoryView"); //NON-NLS-1
		// bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);

		IFolderLayout topLeft = factory.createFolder("topLeft", // NON-NLS-1
				IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);// .ID_RES_NAV);

		// topLeft.addView("org.eclipse.jdt.junit.ResultView"); //NON-NLS-1

		// factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView",0.50f);
		// //NON-NLS-1
		// factory.addFastView("org.eclipse.team.sync.views.SynchronizeView",
		// 0.50f); //NON-NLS-1
		
		//set project layout not movable & closable
		IViewLayout projectLayout = factory.getViewLayout(IPageLayout.ID_PROJECT_EXPLORER);
		projectLayout.setCloseable(false);
		projectLayout.setMoveable(false);
		
		IViewLayout problemView = factory.getViewLayout(IPageLayout.ID_PROBLEM_VIEW);
		problemView.setCloseable(false);
		problemView.setMoveable(false);

		IViewLayout consoleView = factory.getViewLayout(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleView.setCloseable(false);
		consoleView.setMoveable(false);
		
	}

	private void addActionSets() {
		factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.profileActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.ui.actionSet"); //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation");
		// //NON-NLS-1
		factory.addActionSet(JavaUI.ID_ACTION_SET);
		factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		// factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); //NON-NLS-1
	}

	private void addPerspectiveShortcuts() {
		// factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective");
		// //NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		// //NON-NLS-1
		factory.addPerspectiveShortcut("org.ngo.eide.perspectives.NgoEngPerspective"); // NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		// //NON-NLS-1

	}

	private void addNewWizardShortcuts() {
		// factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");//NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");// NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");// NON-NLS-1
	}

	private void addViewShortcuts() {
		// factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView");
		// //NON-NLS-1
		// factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView");
		// //NON-NLS-1
		factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView"); // NON-NLS-1
		factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView"); // NON-NLS-1
		// factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView");
		// //NON-NLS-1
		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
		factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}

	private void removeToolBar() {
		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();

		if(window instanceof WorkbenchWindow) {
		    MenuManager menuManager = ((WorkbenchWindow)window).getMenuManager();

		    //TODO you may need to remove items from the coolbar as well
		    ICoolBarManager coolBarManager = null;

		    if(((WorkbenchWindow) window).getCoolBarVisible()) {
		        coolBarManager = ((WorkbenchWindow)window).getCoolBarManager2();
		    }

		    Menu menu = menuManager.getMenu();

		    //you'll need to find the id for the item
		    String itemId = "Window";
		    IContributionItem item = menuManager.find(itemId);

		    // remember position, TODO this is protected
		    //int controlIdx = menu.indexOf(mySaveAction.getId());

		    if (item != null) {
		        // clean old one
		        menuManager.remove(item);

		        // refresh menu gui
		        menuManager.update();
		    }
		}
	}

}
