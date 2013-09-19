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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import model.Complex;

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;

/**
 * These tests deal with structured parameters and how they may be
 * dependent on other parameters. This includes inner parameters
 * depending on other inner parameters, inner parameters depending on
 * outer parameters, and inner parameters depending on the inner
 * parameters of other structured parameters.
 *
 * @author Christoffer Fink
 */
public class NestedDependencyTest extends TestCleanup {
	/**
	 * This test demonstrates that nested parameters do not get their
	 * dependencies properly resolved. In particular, SParams that
	 * depend on each other cannot be correctly initialized even though
	 * their nested NParams form a terminated dependency chain and
	 * should be possible to successfully initialize.
	 * @throws InPUTException never
	 */
	@Test
	public void chainedDependenciesAreTreatedAsCircularDependencies()
			throws InPUTException {
		final String designSpaceFile = "nestedDependentParamSpace03.xml";
		IDesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");

		// First depends on Second, which depends on First.
		// (But not in a circular way!)
		Complex first = design.getValue("FirstComplex");
		Complex second = design.getValue("SecondComplex");
		// Should really be 2+8i and 4+1i.
		assertEquals(2.0, first.getReal(), 0.0);
		assertEquals(0.0, first.getImaginary(), 0.0);
		assertEquals(0.0, second.getReal(), 0.0);
		assertEquals(1.0, second.getImaginary(), 0.0);

		// Third depends in itself. (But not in a circular way!)
		Complex third = design.getValue("ThirdComplex");
		// Should really be 2+1i.
		assertEquals(0.0, third.getReal(), 0.0);
		assertEquals(1.0, third.getImaginary(), 0.0);

		// Fourth depends on both Third and Second, which is OK, because
		// no circular dependency is perceived in this case.
		Complex fourth = design.getValue("FourthComplex");
		// This is the only one that is correct.
		assertEquals(3.0, fourth.getReal(), 0.0);
		assertEquals(2.0, fourth.getImaginary(), 0.0);

		fail("Undocumented behavior.");
	}

	/**
	 * This test demonstrates that a nested parameter (inside a SParam)
	 * can depend on other parameters, even if they are also nested
	 * inside some SParam.
	 * <p>
	 * Due to peculiarities demonstrated in
	 * {@link ExtendedDesignSpaceTest#nextDoesNotProduceTheCorrectValueForExpressions()},
	 * a design must be created so that parameters are properly initialized.
	 * @throws InPUTException never
	 */
	@Test
	public void nestedParametersCanReferenceOtherParameters()
			throws InPUTException {
		final String designSpaceFile = "nestedDependentParamSpace01.xml";
		IDesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");

		assertEquals(1, design.getValue("IndependentInteger"));
		assertEquals(2, design.getValue("DependentInteger"));
		assertEquals(4, design.getValue("Integer"));
	}

	// This test doesn't really belong here.
	/**
	 * This test demonstrates that objects are cached based in ID.
	 * In this case, two code mappings share the same ID, which causes
	 * a parameter to be mapped to the wrong type.
	 * @throws InPUTException never
	 */
	@Test
	public void mappingsAreCachedById() throws InPUTException {
		final String designSpaceFile01 = "duplicateMappingIdSpace01.xml";
		final String designSpaceFile02 = "duplicateMappingIdSpace02.xml";
		IDesignSpace space01 = new DesignSpace(designSpaceFile01);
		IDesignSpace space02 = new DesignSpace(designSpaceFile02);

		// Note that, in 02, "Number" is mapped to a Double, not Integer.
		assertTrue(space01.next("Number") instanceof Integer);
		assertTrue(space02.next("Number") instanceof Integer);
	}
}
