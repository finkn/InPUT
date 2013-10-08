/*-- $Copyright (C) 2012-13 Felix Dobslaw$

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package se.miun.itm.input.model.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;

/**
 * This class contains tests of how parameters are initialized and how
 * they are related to each other. Can a constructor parameter reference
 * a nested parameter? What is the relationship between a constructor
 * parameter and an inner parameter? What if no method for initializing
 * an inner parameter is explicitly supplied?
 * 
 * @author Christoffer Fink
 */
public class NestedInitializationTest extends TestCleanup {
	public final static String designSpaceFile = "nestedInitializationSpace.xml";
	private IDesignSpace space;
	private IDesign design;
	private String msg;

	@Before
	public void setup() throws InPUTException {
		space = new DesignSpace(designSpaceFile);
		design = space.nextDesign("design");
	}

	/**
	 * This test demonstrates that, while an outer parameter can over
	 * shadow an inner parameter with the same name, it cannot replace
	 * it. It is impossible to use an outer parameter as a constructor
	 * argument for an empty structural parameter.
	 * <p>
	 * Interestingly, the exception message says that a parameter with
	 * ID "Outer" is missing for the initialization of the parent
	 * parameter. It has already been demonstrated that an outer
	 * parameter can in fact be used for such initialization.
	 * @throws InPUTException never
	 */
	@Test
	public void outerParameterCannotBeUsedAsConstructorArgForEmptyParameter()
			throws InPUTException {
		try {
			design.getValue("Empty");
			fail("Expected getting the parameter to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test is almost identical to
	 * {@link #outerParameterCannotBeUsedAsConstructorArgForEmptyParameter()}.
	 * The only differences is that, rather than the structured parameter
	 * being empty, there is a nested parameter with the wrong name.
	 * The result is the same, as one would expect.
	 * @throws InPUTException never
	 */
	@Test
	public void outerParameterIdMustMatchInnerParameter() throws InPUTException {
		try {
			design.getValue("NonEmpty");
			fail("Expected getting the parameter to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test demonstrates that an inner parameter cannot be a
	 * constructor argument for another parameter.
	 * @throws InPUTException never
	 */
	@Test
	public void innerParametersCannotBeConstructorArgsForOtherParameters()
			throws InPUTException {
		msg = "Independent.Inner should have been successfully initialized.";
		assertEquals(msg, 1, design.getValue("Independent.Inner"));
		try {
			design.getValue("Dependent");
			fail("Expected initializing dependent parameter to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test demonstrates that there must exist some mechanism for
	 * initializing nested parameters. Either such a mechanism is
	 * explicitly supplied, or an attempt is made to infer one.
	 * In this case, the inference fails because the default setter
	 * does not exist. Therefore initialization fails.
	 *
	 * @see DefaultAccessorTest#defaultSetterIsUsedInAbsenceOfCustomSetterOrConstructorParam()
	 * @throws InPUTException never
	 */
	@Test
	public void innerParametersMustBeInitializedUsingConstructorOrSetter()
			throws InPUTException {
		try {
			design.getValue("Uninitialized");
			fail("Expected getting the parameter to fail.");
		} catch(InPUTException e) { }
	}
}
