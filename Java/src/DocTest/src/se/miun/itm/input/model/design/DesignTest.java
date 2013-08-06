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
import static org.junit.Assert.assertTrue;
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
public class DesignTest {
	/**
	 * This test arguably demonstrates a bug.
	 * A is defined as being strictly bigger than B, yet A and B have the
	 * same value in the Design that is being imported.
	 * @throws InPUTException never
	 */
	@Test
	public void importingDesignWithOutOfRangeValueSucceeds()
			throws InPUTException {
		final String designFile = "outOfRangeDesign01.xml";
		new Design(designFile);
	}

	/**
	 * This test arguably demonstrates a bug.
	 * Setting A without first touching B results in an InPUTException
	 * because the expression 'B' cannot be evaluated.
	 * Since all values are within their defined ranges, it seems that
	 * setting A should succeed.
	 * 
	 * @see #settingDependentValueSucceedsOnlyIfFirstTouchingDependee
	 * @throws InPUTException never
	 */
	@Test
	public void settingDependentValueFailsUnlessFirstTouchingDependee()
			throws InPUTException {
		final String designFile = "outOfRangeDesign02.xml";
		Design d = new Design(designFile);
		try {
			d.setValue("A", 6);
			fail("Setting A without first setting B is expected to fail.");
		} catch(InPUTException e) { }
	}
	
	/**
	 * This test is almost exactly the same as the fails
	 * test above. The only difference is that the B parameter is first
	 * accessed. (It does not matter whether it is set or only retrieved,
	 * touching it in any way solves the problem.)
	 * 
	 * @see #settingDependentValueFailsUnlessFirstTouchingDependee
	 * @throws InPUTException never
	 */
	@Test
	public void settingDependentValueSucceedsOnlyIfFirstTouchingDependee()
			throws InPUTException {
		final String designFile = "outOfRangeDesign02.xml";
		Design d = new Design(designFile);
		// Get the imported value and discard it.
		d.getValue("B");
		// Now A can be set without problems.
		d.setValue("A", 6);
	}

	/**
	 * This test shows that setting the value of a parameter that some other
	 * parameter depends on (dependee) does not affect the dependent parameter.
	 * Technically, setting the dependee to an inappropriate value (as is done
	 * in this test) places the dependent parameter out of range, thereby
	 * invalidating the Design.
	 * @throws InPUTException never
	 */
	@Test
	public void settingDependeeValueDoesNotAffectDependent()
			throws InPUTException {
		final String designFile = "outOfRangeDesign02.xml";
		Design d = new Design(designFile);
		d.setValue("B", 5);
		final int a = d.getValue("A");
		assertEquals("A should be 5 in the imported Design.", 5, a);
		// This is exactly what we would expect.
		// However, A is already out of range before trying to set it.
		try {
			d.setValue("A", a);
			fail("A is now out of range. Cannot re-set the original value.");
		} catch(IllegalArgumentException e) { }
	}

	/**
	 * This test arguably demonstrates a bug.
	 * By extending the scope of a design, the extending design can have its
	 * parameters set even if the design has been set to read-only.
	 * While the local parameters take precedent, which means that only
	 * parameters that only exist in the extending scope can be set, any
	 * parameter can still be set, simply by extending an empty Design so
	 * that all of the parameters are unique.
	 *
	 * Also note that parameters with the same IDs do not seem to cause
	 * problems, which would contradict the documentation for IDesign.
	 * @throws InPUTException never
	 */
	@Test
	public void extendingScopeCanCircumventReadOnly() throws InPUTException {
		final String designFile = "outOfRangeDesign02.xml";
		final String extendingDesignFile = "extendingDesign.xml";
		Design design = new Design(designFile);
		Design extendingDesign = new Design(extendingDesignFile);
		// Setting both Designs to read-only.
		design.setReadOnly();			// This isn't really relevant.
		extendingDesign.setReadOnly();
		
		final int origZ = extendingDesign.getValue("Z");
		
		// Trying to set a value should now fail.
		try {
			extendingDesign.setValue("Z", 1);
			fail("Cannot set any parameters on a read-only Design.");
		} catch(InPUTException e) { }
		
		// However, now the extendingDesign is used to extend design.
		design.extendScope(extendingDesign);
		// Not only can setValue be called on design (which is read-only),
		// but the extendingDesign (which is also read-only) will have its
		// parameter updated.
		design.setValue("Z", 1);
		
		final int z = extendingDesign.getValue("Z");
		assertTrue("The read-only design should be changed.", origZ != z);
	}

	/**
	 * This test demonstrates that the {@link Design.same(Object)} method
	 * returns true if the argument is a superset of {@code this}
	 * and all parameters in their intersection have the same value in
	 * both designs.
	 * @throws InPUTException never
	 */
	@Test
	public void subsetIsSameButSupersetIsNotSame() throws InPUTException {
		final String subsetFile = "subsetDesign.xml";
		final String supersetFile = "supersetDesign01.xml";
		final String supersetFile2 = "supersetDesign02.xml";
		final Design subset = new Design(subsetFile);
		final Design superset = new Design(supersetFile);
		final Design superset2 = new Design(supersetFile2);
		assertTrue(subset.same(superset));
		assertFalse(superset.same(subset));
		assertFalse(subset.same(superset2));
	}

	/**
	 * This test demonstrates that two designs with the same id and
	 * an identical set of parameters are still not considered equal.
	 * @throws InPUTException never
	 */
	@Test
	public void designEqualityIsDeterminedByDesignSpace()
			throws InPUTException {
		final String subsetFile = "duplicateIdSpace01.xml";
		final String supersetFile = "duplicateIdSpace02.xml";
		DesignSpace subsetSpace = new DesignSpace(subsetFile);
		DesignSpace supersetSpace = new DesignSpace(supersetFile);
		IDesign subsetDesign = subsetSpace.nextDesign("design");
		IDesign supersetDesign = supersetSpace.nextDesign("design");
		final String id1 = subsetDesign.getId();
		final String id2 = supersetDesign.getId();

		// The two designs are subsets of each other.
		// They define exactly the same parameter.
		// They also have the same id.
		assertTrue(subsetDesign.same(supersetDesign));
		assertTrue(supersetDesign.same(subsetDesign));
		assertEquals(id1, id2);
		// Yet, the two designs are not considered equal.
		assertFalse(subsetDesign.equals(supersetDesign));
	}
}
