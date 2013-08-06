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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;

/**
 * The following tests are explicitly meant as documentation tests.
 * AKA executable documentation. It is not necessarily the case that
 * the tested behavior is the right behavior, in the sense that this
 * is what the program is supposed to do. These tests are intended to
 * document what the program in fact does, given some corner cases.
 * 
 * See the configuration files for more detailed comments.
 * 
 * @author Christoffer Fink
 */
public class DesignSpaceTest {
	/**
	 * This test fails to create a DesignSpace from the configuration
	 * due to a circular dependency. The name of the first parameter
	 * is parsed as a literal value rather than a reference to a param.
	 * This results in a NumberFormatException.
	 * @throws InPUTException never
	 */
	@Test(expected=NumberFormatException.class)
	public void shortCircularDependencyCausesException()
			throws InPUTException {
		final String designSpaceFile = "circularDependencySpace01.xml";
		new DesignSpace(designSpaceFile);
	}

	/**
	 * This test is similar to the shortCircularDependency test. The only
	 * difference is that the chain consists of three parameters rather
	 * than two. Creating the DesignSpace works fine in this case, but
	 * using the space to create a Design does not work.
	 * @throws InPUTException never
	 */
	@Test
	public void longerCircularDependencyCausesDifferentException()
			throws InPUTException {
		final String designSpaceFile = "circularDependencySpace02.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		try {
			space.nextDesign("design");
			fail("Trying to create the Design was supposed to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * While the short and long circularDependency tests only used exclMin
	 * as a relation, this circular dependency mixes all four kinds. The
	 * chain is therefore four parameters long. The result is the same as
	 * for the longerCircularDependency test.
	 * @throws InPUTException never
	 */
	@Test
	public void mixedCircularDependencyCausesException()
			throws InPUTException {
		final String designSpaceFile = "circularDependencySpace03.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		try {
			space.nextDesign("design");
			fail("Trying to create the Design was supposed to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test combines 02 and 03. In both tests, creating a DesignSpace
	 * works. The tests only fail when trying to create a Design. However,
	 * including both groups of parameters prevents even the DesignSpace
	 * from being created.
	 * @throws InPUTException never
	 */
	@Test(expected=StackOverflowError.class)
	public void twoUnrelatedGroupsOfParametersCauseError()
			throws InPUTException {
		final String designSpaceFile = "circularDependencySpace04.xml";
		new DesignSpace(designSpaceFile);
	}

	/**
	 * This test shows that even a fixed parameter will be null in an empty
	 * Design.
	 * @throws InPUTException never
	 */
	@Test
	public void fixedValueInEmptyDesignIsAlsoNull() throws InPUTException {
		final String designSpaceFile = "testSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);

		// When generating a normal, non-empty Design, F should be 43.
		IDesign nonEmpty = space.nextDesign("nonempty");
		final int f = nonEmpty.getValue("F");
		assertEquals("F is supposed to be fixed to 43", 43, f);

		// However, when generating an empty design, even F is null.
		IDesign empty = space.nextEmptyDesign("empty");
		assertNull("F must be null in an empty Design.", empty.getValue("F"));

		// Confirm that F is indeed fixed.
		try {
			empty.setValue("F", 2);
			fail("Setting F to anything but the fixed value should fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test confirms that it is impossible to create a DesignSpace
	 * where a parameter has explicit overlapping restrictions (the min/max
	 * limits leave no valid values).
	 * @throws InPUTException never
	 */
	@Test
	public void creatingImpossibleDesignSpaceFails() throws InPUTException {
		// All of these should fail when trying to create the DesignSpace.
		final String[] designSpaceFiles = {
			"impossibleSpace01.xml", "impossibleSpace02.xml",
			"impossibleSpace03.xml",
		};
		for(String file : designSpaceFiles) {
			try {
				new DesignSpace(file);
				fail(file + " should cause an exception.");
			} catch(InPUTException e) { }
		}
	}

	/**
	 * This test demonstrates a bug.
	 * The DesignSpace defines A such that the only valid value is 2.
	 * B is then defined to be at least 2, but strictly less than A, which
	 * is impossible. Not only can a DesignSpace be created from this
	 * configuration, a Design can also be created based on the DesignSpace.
	 * A is initialized as expected, but B is initialized to an illegal value.
	 * @throws InPUTException never
	 */
	@Test
	public void creatingDesignFromImpossibleDesignSpaceUnexpectedlyWorks()
			throws InPUTException {
		final String designSpaceFile = "impossibleSpace04.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		// Because the set of possible values for B is empty, one would
		// expect that a design cannot be created.
		IDesign design = space.nextDesign("impossibleDesign");

		// B is explicitly defined not to be able to take on the same
		// value as A, yet the two are equal.
		final int b = design.getValue("B");
		final int a = design.getValue("A");
		assertEquals("A and B can only be equal.", a, b);
		// Since B could only be set to an invalid value, trying to set
		// it to the same value again fails as expected.
		try {
			design.setValue("B", b);
			fail("Cannot re-set B to the same value.");
		} catch(IllegalArgumentException e) { }
	}

	/**
	 * This test confirms that inclMin=x and inclMax=x is a valid range.
	 * @throws InPUTException never
	 */
	@Test
	public void creatingPossibleDesignSpace() throws InPUTException {
		final String designSpaceFile = "possibleSpace.xml";
		new DesignSpace(designSpaceFile);
	}

	/**
	 * This test demonstrates that the referenced parameters are initialized
	 * independent of the ranges of the dependent parameters. In this design
	 * space, the D parameter can only be initialized to a valid value if A
	 * and B are both set to 0. C, on the other hand, can never be set to
	 * a valid value.
	 * This test is similar to the
	 * creatingDesignFromImpossibleDesignSpaceUnexpectedlyWorks test above.
	 * The difference is that the ranges are much bigger. In that test, A
	 * can only be set to a single value in any case. Here, A and B can take
	 * on many values, but should they?
	 * @throws InPUTException never
	 */
	@Test
	public void initializingParameterWithTinyRange() throws InPUTException {
		final String designSpaceFile = "overlapSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");
		final long a = design.getValue("A");
		final long b = design.getValue("B");
		assertFalse("A and B both being 0 is unlikely!", a == 0 && b == 0);
	}

	/**
	 * This test demonstrates that a parameter ID is allowed to contain
	 * a dot.
	 * @throws InPUTException never
	 */
	@Test
	public void singleParameterWithDotInTheIdIsLegal() throws InPUTException {
		final String designSpaceFile = "dottedNameSpace01.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");
		assertEquals(43, design.getValue("A.1.2.3"));
	}

	/**
	 * This test demonstrates that it is legal for an array element to
	 * have the same ID as another parameter. Getting the value for such
	 * a shared ID returns the array element.
	 * @throws InPUTException never
	 */
	@Test
	public void anArrayElementWithTheSameIdAsAnotherParameterTakesPrecedence()
			throws InPUTException {
		final String designSpaceFile = "dottedNameSpace02.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");
		assertEquals(10, design.getValue("A.1"));
	}

	/**
	 * This test demonstrates that duplicate IDs are legal.
	 * In other words, a DesignSpace can contain multiple parameters with
	 * the same ID. It seems to be the case that the one that is declared
	 * last is the one that remains.
	 * @throws InPUTException never
	 */
	@Test
	public void duplicateIDsAreLegal() throws InPUTException {
		final String designSpaceFile = "duplicateIdSpace01.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("design");
		assertEquals(10, design.getValue("A"));
		final int params = design.getSupportedParamIds().size();
		assertEquals("Expected only 1 parameter in the design.", 1, params);
	}

	/**
	 * This test demonstrates that an empty string is a valid Design id.
	 * @throws InPUTException never
	 */
	@Test
	public void creatingDesignWithEmptyIdIsLegal() throws InPUTException {
		final String designSpaceFile = "possibleSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		IDesign design = space.nextDesign("");
		assertEquals("", design.getId());
	}

	/**
	 * This test demonstrates that simple arithmetic expressions (in this
	 * case "1 + 2") are not evaluated. Instead they are parsed as
	 * numbers, causing a NumberFormatException.
	 * @throws InPUTException never
	 */
	@Test
	public void simpleArithmeticExpressionsCannotBeEvaluated()
			throws InPUTException {
		final String designSpaceFile = "simpleArithmeticSpace.xml";
		try {
			new DesignSpace(designSpaceFile);
			fail("Creating the design space is expected to fail.");
		} catch(NumberFormatException e) { }
	}

	/**
	 * This test demonstrates that fixed parameters cannot be set to an
	 * expression. The expression isn't evaluated and thus causes a
	 * NumberFormatException.
	 * @throws InPUTException never
	 */
	@Test
	public void fixedParametersAreNotEvaluated() throws InPUTException {
		final String designSpaceFile = "fixedRelativeSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		try {
			space.nextDesign("design id");
			fail("Creating the design is expected to fail.");
		} catch(NumberFormatException e) { }
	}

	/**
	 * This test demonstrates that spaces are allowed in IDs, and that
	 * the DesignSpace can be created. However, an expression involving
	 * a parameter ID with white space cannot be evaluated.
	 * @throws InPUTException never
	 */
	@Test
	public void spaceInIdIsLegal() throws InPUTException {
		final String designSpaceFile = "spaceInIdSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		try {
			space.nextDesign("design");
			fail("Creating the design is expected to fail.");
		} catch(InPUTException e) { }
	}

	/**
	 * This test demonstrates that fixed parameters can be fixed to
	 * new values (being fixed doesn't prevent them from getting fixed).
	 * The parameter was initially unfixed in the design space configuration.
	 * @throws InPUTException never
	 */
	@Test
	public void setFixedMultipleTimesIsLegal() throws InPUTException {
		final String designSpaceFile = "possibleSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		space.setFixed("A", "2");
		space.setFixed("A", "2");
	}

	/**
	 * This test demonstrates that fixed parameters can be fixed to new
	 * values even when they were defined as fixed in the design space
	 * configuration.
	 * @throws InPUTException never
	 */
	@Test
	public void fixedParametersCanBeFixedToNewValues() throws InPUTException {
		final String designSpaceFile = "fixedRelativeSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		space.setFixed("A", "2");
	}

	/**
	 * This test demonstrates that setting a fixed value bypasses range
	 * checks. That is, a parameter can be set to an out-of-range value.
	 * The only legal value for A is 2, but we can set it to 100.
	 * @throws InPUTException never
	 */
	@Test
	public void setFixedBypassesRanges() throws InPUTException {
		final String designSpaceFile = "possibleSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		space.setFixed("A", "100");
		IDesign design = space.nextDesign("design");
		assertEquals(100, design.getValue("A"));
	}

	/**
	 * This test demonstrates that an integer parameter can be fixed to
	 * a floating point value. The value will simply be truncated.
	 * @throws InPUTException never
	 */
	@Test
	public void integerParameterFixedToFloatIsTruncated()
			throws InPUTException {
		final String designSpaceFile = "typeMismatchSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		assertEquals("Expected A to be truncated to 2.", 2, space.next("A"));
	}

	/**
	 * This test demonstrates that an integer parameter can be defined
	 * by a range of floating point values. They will both be truncated.
	 * @throws InPUTException never
	 */
	@Test
	public void floatRangeIsTruncatedForIntegerParameter()
			throws InPUTException {
		final String designSpaceFile = "floatRangeSpace.xml";
		DesignSpace space = new DesignSpace(designSpaceFile);
		assertEquals("The only legal value should be 2.", 2, space.next("A"));
	}
}
