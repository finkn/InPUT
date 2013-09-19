package se.miun.itm.input.model.design;

import org.junit.After;

import java.io.File;

import se.miun.itm.input.model.mapping.Mappings;
import se.miun.itm.input.model.param.ParamStore;

/**
 * Serves as a common test base class that handles cleanup.
 * The class can either be extended, in which case the cleanup method
 * is automatically executed, or it can be manually invoked from any
 * class by calling the static {@link #cleanup()} method.
 *
 * @author Christoffer Fink
 */
public class TestCleanup {
	/**
	 * Suitable filename for temporary files. Any such file will be
	 * removed during cleanup.
	 */
	public static final String tmpFile = "tmp.xml";

	@After
	public void tearDown() {
		TestCleanup.cleanup();
	}

	public static void cleanup() {
		ParamStore.releaseAllParamStores();
		Mappings.releaseAllMappings();
		File tmp = new File(tmpFile);
		tmp.delete();
	}
}
