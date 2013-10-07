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
package model;
/**
 * This class is very similar to the CustomAccessorTester class.
 * It defines a setter that should match the default setter that
 * will be used when no constructor parameters or custom setter is
 * supplied in the configuration.
 * <p>
 * Of course, this assumest that the inner parameter will be called
 * "Data".
 * 
 * @author Christoffer Fink
 */
public class DefaultSetTester {
	private int getterInvocations = 0;
	private int setterInvocations = 0;
	// The data is public to provide a back door so that it can be
	// examined without invoking the accessors.
	public int data = 0;

	public DefaultSetTester() { }

	public DefaultSetTester(int data) {
		this.data = data;
	}

	public int getData() {
		getterInvocations++;
		return data;
	}
	public void setData(int data) {
		setterInvocations++;
		this.data = data;
	}

	// Access book keeping data.
	public int getGetterInvocations() {
		return getterInvocations;
	}
	public int getSetterInvocations() {
		return setterInvocations;
	}
}