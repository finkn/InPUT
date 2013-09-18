package model;

/**
 * A simple class for testing custom accessors in the code mapping.
 * When an accessor is invoked, a counter is increased.
 * 
 * @author Christoffer Fink
 */
public class CustomAccessorTester {
	private int getterInvocations = 0;
	private int setterInvocations = 0;
	// The data is public to provide a back door so that it can be
	// examined without invoking the accessors.
	public int data = 0;

	public CustomAccessorTester() { }

	public CustomAccessorTester(int data) {
		this.data = data;
	}

	// Using slightly elaborate names to underscore that a truly
	// arbitrarily named (custom) accessor has really been set.
	public int customGetMethod() {
		getterInvocations++;
		return data;
	}
	public void customSetMethod(int data) {
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
