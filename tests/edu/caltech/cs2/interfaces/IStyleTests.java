package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.helpers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public interface IStyleTests {
    String getSource();
    Class<?> getClazz();
    List<String> getPublicInterface();
    int getMaxFields();
    List<String> methodsToBanSelf();

//    @DisplayName("The overall number of fields is small")
//    @TestHint("It's good style to use the smallest number of fields that you can in each class.")
//    @Test
//    default void testSmallNumberOfFields() {
//        Reflection.assertFieldsLessThan(getClazz(), "private", getMaxFields());
//    }

    
    @DisplayName("The public interface is correct")
    @TestDescription("This test checks that you haven't declared any extra public methods.")
    @TestHint("You are welcome to make any extra methods you want, but they have to be private!")
    @Test
    default void testPublicInterface() {
        Reflection.assertPublicInterface(getClazz(), getPublicInterface());
    }

    @DisplayName("Does not use or import disallowed classes")
    @TestHint("Remember that you're not allowed to use anything in java.util except Iterator and Random!")
    @Test
    default void testForInvalidClasses() {
        List<String> regexps = List.of("java\\.util\\.(?!Iterator|Random|function.Function|ArrayList|List|Scanner|Map)", "java\\.lang\\.reflect", "java\\.io\\.(?!File|FileNotFoundException)");
        Inspection.assertNoImportsOf(getSource(), regexps);
        Inspection.assertNoUsageOf(getSource(), regexps);
    }

    @DisplayName("Does not attempt to get around constructor counts")
    @Test
    default void testForAvoidCounters() {
        List<String> regexps = List.of("NUM_CALLS");
        Inspection.assertNoUsageOf(getSource(), regexps);
    }

    @DisplayName("All fields have modifiers")
    @TestDescription("This test checks that every field is private or public.")
    @Test
    default void testFieldModifiers() {
        Reflection.assertFieldModifiers(getClazz());
    }
    
    @DisplayName("There are no public fields")
    @TestHint("It's good style to never use public fields whenever avoidable.")
    @Test
    default void testNoPublicFields() {
        Reflection.assertNoPublicFields(getClazz());
    }

    @DisplayName("There are no protected fields")
    @TestHint("It's good style to never use protected fields whenever avoidable.")
    @Test
    default void testNoProtectedFields() {
        Reflection.assertNoProtectedFields(getClazz());
    }
    
    @DisplayName("There are no attempts to create a new version of the data structure to avoid actually implementing a method")
    @Test
    default void testNoInstantiationOfSelf() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Method[] methods = getClazz().getDeclaredMethods();
        for (Method m : methods) {
            if (methodsToBanSelf().contains(m.getName())) {
                NewMe.NUM_CALLS = 0;
                Class<?> clazz = getClazz();
                Constructor<?>[] constructors = clazz.getConstructors();
                Object o = null;
                for (Constructor constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        o = constructor.newInstance();
                        break;
                    } else if (constructor.getParameterCount() == 1) {
                        o = constructor.newInstance(10);
                        break;
                    }
                }

                for (int i = 0; i < 100; i++) {
                    if (m.getParameterCount() == 0) {
                        m.invoke(o);
                    } else if (m.getParameterCount() == 1) {
                        m.invoke(o, i);
                    } else if (m.getParameterCount() == 2) {
                        m.invoke(o, i, i);
                    }
                }
                assertTrue(NewMe.NUM_CALLS == 0, "Calls to " + m.getName() + " should not create a new instance of " + getClazz().getName());
            }
        }
    }
}
