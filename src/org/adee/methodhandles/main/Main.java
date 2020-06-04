package org.adee.methodhandles.main;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;

import org.adee.methodhandles.model.Country;

public class Main {

	private static String FIELD_NAME = "name";
	private static String FIELD_POPULATION = "population";
	private static String METHOD_SETNAME = "setName";
	private static String METHOD_GETNAME = "getName";

	private static Lookup lookup = MethodHandles.lookup();
	private static Lookup publicLookup = MethodHandles.publicLookup();
	private static Lookup privateLookup;

	static {
		try {
			privateLookup = MethodHandles.privateLookupIn(Country.class, lookup);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Throwable {
		Country country = lookupAndCreateCountry();
		System.out.println("Country Object Created - " + country);
		invokeMethods(country);
		invokePublicFieldActions(country);
		invokePrivateFieldActions(country);
		invokeConstructors();
		invokeStaticMethod();

	}

	private static void invokeMethods(Country country) {
		invokeGetCountry(country);
		invokeSetCountry(country);
	}

	private static void invokePublicFieldActions(Country country) {
		invokeReadActionPublicField(country);
		invokeWriteActionPublicField(country);
	}

	private static void invokePrivateFieldActions(Country country) {
		invokeReadActionPrivateField(country);
		invokeWriteActionPrivateField(country);
	}

	private static void invokeReadActionPublicField(Country country) {
		MethodHandle nameFieldHandle = null;
		try {
			nameFieldHandle = lookup.findGetter(Country.class, FIELD_NAME, String.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Invoked getName() " + nameFieldHandle.invoke(country));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void invokeWriteActionPublicField(Country country) {
		MethodHandle nameFieldHandle = null;
		try {
			// A method handle giving write access to a non-static field , name in this case
			nameFieldHandle = lookup.findSetter(Country.class, FIELD_NAME, String.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			// first argument is the instance containing the field and the second arg
			// is the value to be stored
			nameFieldHandle.invoke(country, "United Kingdom");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void invokeReadActionPrivateField(Country country) {
		try {

			MethodHandle getter = privateLookup.findGetter(Country.class, FIELD_POPULATION, int.class);
			String getValue = String.valueOf(getter.invoke(country));
			System.out.println("Invoke get private Field population " + getValue);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void invokeWriteActionPrivateField(Country country) {
		try {
			privateLookup.findSetter(Country.class, FIELD_POPULATION, int.class).invoke(country, 1070000000);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("Country after update " + country);
	}

	private static void invokeSetCountry(Country country) {
		// for a method that returns void and accepts a String argument.
		MethodType setter = MethodType.methodType(void.class, String.class);
		try {
			MethodHandle handle = publicLookup.findVirtual(Country.class, METHOD_SETNAME, setter);
			handle.invoke(country, "Greece");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("Country after update " + country);
	}

	private static void invokeGetCountry(Country country) {
		// for a method that returns a String and accepts no arguments.
		MethodType getter = MethodType.methodType(String.class);
		try {
			System.out.println("Invoke method getName "
					+ publicLookup.findVirtual(Country.class, METHOD_GETNAME, getter).invoke(country));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static Country lookupAndCreateCountry() {
		Class<?> countryClass = null;
		try {
			countryClass = lookup.findClass(Country.class.getName());
		} catch (ClassNotFoundException | IllegalAccessException e) {
			e.printStackTrace();
		}
		Country country = null;
		try {
			country = (Country) countryClass.getConstructor(String.class, int.class).newInstance("India", 1352600000);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return country;
	}

	private static void invokeConstructors() throws NoSuchMethodException, IllegalAccessException {
		// method type for a parametrized constructor
		MethodType constructor = MethodType.methodType(void.class, String.class, int.class);
		MethodHandle constructorHandle = lookup.findConstructor(Country.class, constructor);
		try {
			System.out.println(
					"Invoke Parametrized constructor " + constructorHandle.invokeWithArguments("China", 1392700000));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// method type for a no-args constructor
		MethodType noArgsConstructor = MethodType.methodType(void.class);
		MethodHandle emptyConstructorHandle = lookup.findConstructor(Country.class, noArgsConstructor);
		try {
			System.out.println("Invoke No-args constructor " + emptyConstructorHandle.invoke());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void invokeStaticMethod() {
		try {
			// a method that return a String[]
			MethodType staticMethodType = MethodType.methodType(String[].class);
			// get a method handle on getDetails method of the class Country
			MethodHandle staticMethodHandle = publicLookup.findStatic(Country.class, "getDetails", staticMethodType);
			staticMethodHandle.invoke();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
