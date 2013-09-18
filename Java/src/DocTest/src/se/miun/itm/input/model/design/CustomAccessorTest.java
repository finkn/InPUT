package se.miun.itm.input.model.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import model.CustomAccessorTester;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.mapping.Mappings;
import se.miun.itm.input.model.param.ParamStore;

/**
 * This class contains multiple tests that explore how custom accessors
 * behave. The word "accessors" here refers to both "accessor" (get) as
 * well as "mutator" (set).
 * 
 * @author Christoffer Fink
 */
public class CustomAccessorTest {
	public final static String designSpaceFile = "customAccessorSpace.xml";
	private IDesignSpace space;
	private IDesign design;
	private String msg;
	
	@Before
	public void setup() throws InPUTException {
		space = new DesignSpace(designSpaceFile);
		design = space.nextDesign("design");
	}
	@After
	public void cleanup() {
		ParamStore.releaseAllParamStores();
		Mappings.releaseAllMappings();
	}


	/**
	 * This test works like a lemma. It confirms that the
	 * CustomAccessorTester class works as expected.
	 */
	@Test
	public void invokingAccessorsShouldIncreaseCounters() {
		CustomAccessorTester tester = new CustomAccessorTester(1);
		assertEquals(1, tester.data);

		// Should start off at 0 invocations.
		assertEquals(0, tester.getGetterInvocations());
		assertEquals(0, tester.getSetterInvocations());
		// Get data.
		assertEquals(1, tester.customGetMethod());
		// Number of invocations should have increased.
		assertEquals(1, tester.getGetterInvocations());
		// Set data.
		tester.customSetMethod(3);
		tester.customSetMethod(2);
		// Number of invocations should have increased.
		assertEquals(2, tester.getSetterInvocations());
		// Also confirm that the data was actually set.
		assertEquals(2, tester.customGetMethod());
	}

	/**
	 * This test demonstrates that fetching the structural parameter for
	 * which custom accessors were set causes the set method to be
	 * invoked (provided no constructor argument is used).
	 * 
	 * @see #fetchingOuterParameterMultipleTimesHasNoEffect()
	 * @see #customAccessorsDoNotWorkWhenUsingConstructorArg()
	 * @throws InPUTException never
	 */
	@Test
	public void fetchingOuterParameterInvokesSetter()
			throws InPUTException {
		CustomAccessorTester tester = design.getValue("Tester");
		msg = "Setter should have been invoked once.";
		assertEquals(msg, 1, tester.getSetterInvocations());
	}

	/**
	 * This test shows that the setter is invoked only the first time
	 * the outer parameter is fetched. Presumably, the parameter is
	 * initialized the first time.
	 * 
	 * @see #fetchingOuterParameterInvokesSetter()
	 * @throws InPUTException never
	 */
	@Test
	public void fetchingOuterParameterMultipleTimesHasNoEffect()
			throws InPUTException {
		CustomAccessorTester tester = design.getValue("Tester");
		tester = design.getValue("Tester");
		tester = design.getValue("Tester");
		msg = "Setter should have been invoked once.";
		assertEquals(msg, 1, tester.getSetterInvocations());
	}

	/**
	 * This test demonstrates that setting the value for the nested
	 * (inner) parameter causes the custom accessor to be invoked, at
	 * least in these particular circumstances.
	 * 
	 * @see #customSetterIsNotInvokedWhenOuterParameterHasNotBeenFetched()
	 * @throws InPUTException never
	 */
	@Test
	public void customSetterShouldBeInvokedWhenSettingValue()
			throws InPUTException {
		// Initializing the object uses the setter.
		CustomAccessorTester tester = design.getValue("Tester");
		design.setValue("Tester.Data", 1);
		msg = "Setter should have been invoked twice.";
		assertEquals(msg, 2, tester.getSetterInvocations());
	}

	/**
	 * This test demonstrates that setting the value for the nested
	 * parameter only causes the custom accessor to be invoked if the
	 * outer parameter has first been fetched.
	 * <p>
	 * However, the value is correctly set.
	 * 
	 * @see #customSetterShouldBeInvokedWhenSettingValue()
	 * @throws InPUTException never
	 */
	@Test
	public void customSetterIsNotInvokedWhenOuterParameterHasNotBeenFetched()
			throws InPUTException {
		// Do setValue multiple times. These will not be counted.
		design.setValue("Tester.Data", 1);
		design.setValue("Tester.Data", 4);
		design.setValue("Tester.Data", 2);

		CustomAccessorTester tester = design.getValue("Tester");
		msg = "Setter should have been invoked once.";
		assertEquals(msg, 1, tester.getSetterInvocations());
		msg = "The data should match the value set earlier.";
		assertEquals(msg, 2, tester.data);

		fail("Undocumented behavior.");
	}

	/**
	 * This test arguably demonstrates a bug. It shows that a custom
	 * accessor is not invoked when getting the value for an inner
	 * parameter.
	 * @throws InPUTException never
	 */
	@Test
	public void customGetterShouldBeInvokedWhenGettingValue()
			throws InPUTException {
		CustomAccessorTester tester = design.getValue("Tester");
		design.getValue("Tester.Data");
		msg = "Getter should have been invoked once.";
		assertEquals(msg, 1, tester.getGetterInvocations());
	}

	/**
	 * This test demonstrates that custom accessors are never invoked
	 * if the nested parameter was set using a constructor argument.
	 * @throws InPUTException
	 */
	@Test
	public void customAccessorsDoNotWorkWhenUsingConstructorArg()
			throws InPUTException {
		CustomAccessorTester tester = design.getValue("TesterWithArg");
		design.setValue("TesterWithArg.Data", 1);
		msg = "Setter is not expected to be invoked.";
		assertEquals(msg, 0, tester.getSetterInvocations());

		fail("Undocumented behavior.");
	}
}
