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

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;

/**
 * These tests investigate what happens when a nested parameter shares
 * the same name as an outer parameter.
 *
 * @author Christoffer Fink
 */
public class ParameterShadowingTest extends TestCleanup {
	/**
	 * This test combines the
	 * {@link #outerParametersOverShadowsNestedParameters()} and the
	 * {@link NestedDependencyTest#nestedParametersCanReferenceOtherParameters()} tests.
	 * None of the two structural parameters are initialized to the
	 * expected value because there are two different "A" parameters.
	 * @throws InPUTException never
	 */
	@Test
	public void overShadowingAndNestedParameterReferencesAreStrange()
			throws InPUTException {
		final String designSpaceFile = "nestedDependentParamSpace02.xml";
		IDesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");
		int independent = design.getValue("IndependentInteger");
		int dependent = design.getValue("DependentInteger");
		// IndependentInteger.A is defined to be 1, yet it is 5.
		assertEquals(5, independent);
		// DependentInteger.B is defined to be 2 x IndependentInteger.A,
		// which would be 10, yet it is 0.
		assertEquals(0, dependent);

		fail("Undocumented behavior.");
	}

	/**
	 * This test demonstrates that a nested parameter (inside a SParam)
	 * is over shadowed by an outer parameter if they have the same name.
	 * <p>
	 * Due to peculiarities demonstrated in
	 * {@link ExtendedDesignSpaceTest#nextDoesNotProduceTheCorrectValueForExpressions()},
	 * a design must be created so that parameters are properly initialized.
	 * @throws InPUTException never
	 */
	@Test
	public void outerParametersOverShadowsNestedParameters()
			throws InPUTException {
		final String designSpaceFile = "duplicateIdSpace03.xml";
		IDesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");

		int n = design.getValue("Integer");
		assertEquals(1, n);
	}
}
