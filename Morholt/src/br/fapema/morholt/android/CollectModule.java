package br.fapema.morholt.android;

import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.android.wizardpager.ApplicationInterface;
import br.fapema.morholt.android.wizardpager.wizard.model.PageList;
import dagger.Module;
import dagger.Provides;

/**
 * On eclipse see that:
 * 
 * 
    Create a directory compile-libs next to your libs directory.
    Put dagger-compiler-.jar in this directory.
    Put dagger-.jar and javax.inject.jar (get it from here: http://code.google.com/p/atinject/downloads/list ) in your libs directory.
    Put dagger-.jar and javax.inject.jar on your buildpath.
    Goto Project->Properties->Java Compiler-> and check "Enable project specific settings". AND Goto Project->->Properties->Java Compiler->Factory path and check "Enable project specific settings"
    Under Factory path Click add JARs and add dagger-compiler-.jar, dagger-.jar and javax.inject.jar

Update!
Make sure you have ".apt_generated" as a source folder on your build path.

Clean and rebuild project and you should be good to go!

 * @author pedro
 *
 */
@Module(
		injects = {WizardActivity.class},
		complete = true
		)

public class CollectModule {


    public static final String DIAGNOSTIC_BRANCH = "diagnostic";
    
	private final ApplicationInterface applicationInterface;	
	
	private Creator creator;
	
	public CollectModule(ApplicationInterface applicationInterface) {
		this.applicationInterface = applicationInterface;
		
		 creator = new Creator(applicationInterface);
		
	}
	
	// TODO make it static
	@Provides PageList providesPageList() {
		return creator.pageList;
	}

	public Creator getCreator() {
		return creator;
	}
}