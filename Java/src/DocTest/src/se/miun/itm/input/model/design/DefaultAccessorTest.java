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
import model.DefaultSetTester;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;

/**
 * This class contains tests dealing with default accessor methods.
 * Is the default set method really invoked? Is it used later when
 * setting values for a parameter?
 * 
 * @author Christoffer Fink
 */
public class DefaultAccessorTest extends TestCleanup {
	public final static String designSpaceFile = "defaultAccessorSpace.xml";
	private IDesignSpace space;
	private IDesign design;

	@Before
	public void setup() throws InPUTException {
		space = new DesignSpace(designSpaceFile);
		design = space.nextDesign("design");
	}

	/**
	 * This test confirms that a default setter method is invoked when
	 * initializing a parameter for which no constructor parameter or
	 * custom setter was supplied.
	 * @throws InPUTException
	 */
	@Test
	public void defaultSetterIsUsedInAbsenceOfCustomSetterOrConstructorParam()
			throws InPUTException {
		DefaultSetTester tester = design.getValue("DefaultAccessorsExist");
		assertEquals(1, tester.getSetterInvocations());
	}

	/**
	 * This test confirms that the default setter method name is a function
	 * of the inner parameter name, not the field that is initialized (or
	 * the constructor argument that might be used to initialize it).
	 * <p>
	 * The test is almost identical to
	 * {@link #defaultSetterIsUsedInAbsenceOfCustomSetterOrConstructorParam()}
	 * with the difference that the parameter name does not match the field
	 * name and therefore not the setter method name.
	 * @throws InPUTException never
	 */
	@Test
	public void defaultSetterIsAFunctionOfTheParameterName()
			throws InPUTException {
		try {
			design.getValue("DefaultAccessorsMissing");
			fail("Expected getting the parameter to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test shows that the default setter method (that is used in the
	 * absence of a constructor parameter or custom setter) will be used
	 * when setting values after initialization as well.
	 * @throws InPUTException never
	 */
	@Test
	public void defaultSetterIsUsedEvenAfterInitialization()
			throws InPUTException {
		DefaultSetTester tester = design.getValue("DefaultAccessorsExist");
		design.setValue("DefaultAccessorsExist.Data", 1);
		assertEquals(1, tester.data);
		design.setValue("DefaultAccessorsExist.Data", 2);
		assertEquals(2, tester.data);
		// Init + set 1 + set 2.
		assertEquals(3, tester.getSetterInvocations());
	}
}
