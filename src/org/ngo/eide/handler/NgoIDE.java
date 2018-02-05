package org.ngo.eide.handler;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ngo.eide.NgoStartup;
import org.ngo.ether.endpoint.EndpointCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NgoIDE implements EndpointCallback {

	private final static Logger LOGGER = LoggerFactory.getLogger(NgoIDE.class);

	
	public final static NgoIDE instance = new NgoIDE();
	protected static final String JAVA = "java"; //$NON-NLS-1$
	protected static final String JAVA_EXTENSION = ".java"; //$NON-NLS-1$
	protected static final String LAUNCHCONFIGURATIONS = "launchConfigurations"; //$NON-NLS-1$
	protected static final String LAUNCH_EXTENSION = ".launch"; //$NON-NLS-1$

	private IWorkbench workbench;

	public NgoIDE() {
		while (true)
		{
			//loop until the workbench is instantiated
			workbench = PlatformUI.getWorkbench();
			if (workbench != null)
				break;
			else
				try {
					LOGGER.debug("workbench is not available, loop again...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
		}
	}

	@Override
	public void connected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(String message) {
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();

		IWorkbenchWindow window1 = windows[0];

		// get all the perspectives
		IPerspectiveDescriptor[] prs = workbench.getPerspectiveRegistry().getPerspectives();

		// find a specific perspective
		IPerspectiveDescriptor prdebug = workbench.getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.debug.ui.DebugPerspective");
		IPerspectiveDescriptor prjava = workbench.getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.jdt.ui.JavaPerspective");

		String userprd = IDebugUIConstants.ID_DEBUG_PERSPECTIVE;
		if (message.equalsIgnoreCase("javap")) {
			userprd = "org.eclipse.jdt.ui.JavaPerspective";
		} else if (message.equalsIgnoreCase("debugp")) {
			userprd = IDebugUIConstants.ID_DEBUG_PERSPECTIVE;
		}
				
		final IPerspectiveDescriptor prdes =  workbench.getPerspectiveRegistry().findPerspectiveWithId(userprd);

		//do perspective switch here.
		/*Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	try {
					workbench.showPerspective(prdes.getId(), window1);
				} catch (WorkbenchException e) {}
		    }
		});*/ 
		
		
		//HXY: below is the PC version
		//http://www.programcreek.com/java-api-examples/index.php?source_dir=grails-ide-master/org.grails.ide.eclipse.groovy.debug.tests/jdt-debug-tests-src/org/eclipse/jdt/debug/tests/ProjectCreationDecorator.java
		DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() { 
            public void run() { 
                IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage(); 
			    activePage.setPerspective(prdes); 
			    // hide variables and breakpoints view to reduce simultaneous conflicting requests on debug targets 
                IViewReference ref = activePage.findViewReference(IDebugUIConstants.ID_VARIABLE_VIEW); 
                //activePage.hideView(ref); 
                ref = activePage.findViewReference(IDebugUIConstants.ID_BREAKPOINT_VIEW); 
                //activePage.hideView(ref); 
            } 
        }); 
		

		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		//root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		// projects
		IProject[] projects = wsroot.getProjects();
		
		

		if (window1 != null && message.startsWith("code:")) {

			DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() { 
	            public void run() { 
	            	IFile class1 = null;
					try {
						IResource[] rs = wsroot.members();
						IFolder src = projects[0].getFolder("src");
						IFolder package1 = src.getFolder("package1");
						class1 = package1.getFile("class1.java");
						if (!class1.exists()) {
						}
		
						// 3. insert some code into java file
						byte[] bytes = message.replace("code:", "").getBytes();
						ByteArrayInputStream source = new ByteArrayInputStream(bytes);
						// enable below when needed
						class1.setContents(source, true, true, null);
		
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	            } 
	        }); 
				

		}

		//HXY: implement debug/run config
		if (message.equalsIgnoreCase("config")) {
			try {
				// 10. launch configuration
				IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("test"); 
		        if (pro.exists()) { 
		            //pro.delete(true, true, null); 
		        } 
		        // create project and import source 
		        //IJavaProject fJavaProject = JavaProjectHelper.createJavaProject("DebugTests", "bin"); 
		        //IPackageFragmentRoot src = JavaProjectHelper.addSourceContainer(fJavaProject, "src"); 
		        
		        IFolder folder = pro.getFolder("launchConfigurations"); 
		        if (folder.exists()) { 
		            folder.delete(true, null); 
		        } 
		        folder.create(true, true, null); 
		 
		        // delete any existing launch configs 
		        ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(); 
		        for (int i = 0; i < configs.length; i++) { 
		            configs[i].delete(); 
		        } 
		 
		        createLaunchConfiguration(JavaCore.create(projects[0]),"SimpleTests");
				
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * Creates a shared launch configuration for the type with the given name.
	 */
	protected void createLaunchConfiguration(IJavaProject project, String mainTypeName) throws Exception {
		ILaunchConfigurationType type = getLaunchManager()
				.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy config = type.newInstance(project.getProject().getFolder(LAUNCHCONFIGURATIONS),
				mainTypeName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getElementName());
		// use 'java' instead of 'javaw' to launch tests (javaw is problematic
		// on JDK1.4.2)
		Map map = new HashMap(1);
		map.put(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, JAVA);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP, map);
		config.doSave();
	}

	/**
	 * Returns the launch manager
	 * 
	 * @return launch manager
	 */
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub

	}

}
