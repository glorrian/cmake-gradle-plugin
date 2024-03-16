package dev.infochem.cmakegradleplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * An abstract class from which all plugin tasks are inherited
 *
 * @version 1.0
 */
public abstract class CMakeTask extends DefaultTask {
    /**
     * The method that is used as a Task Action
     */
    @SuppressWarnings("unused")
    abstract public void execute();

    /**
     * Required output property for each task
     * @return {@link org.gradle.api.file.DirectoryProperty} The directory where CMake is being built
     */
    @OutputDirectory
    abstract public DirectoryProperty getBuildDirectory();
    /**
     * Required input property for each task
     * @return Property with the path to the CMake executable file
     */
    @InputFile
    abstract public Property<String> getCMakeExecutable();

    /**
     * The implementation of this method must contain an assembly of command line arguments for the task
     * @implNote The items in the list cannot contain spaces
     * @return The list of arguments to command line
     */
    abstract protected List<String> buildCommandLine();

    /**
     * Method to get DSL extension from project build
     * @return Extension contains data for task properties
     */
    @Internal
    protected final CMakeExtension getExtension() {
        CMakeExtension extension = (CMakeExtension) getProject().getExtensions().findByName("cmake");
        if (extension == null) {
            throw new GradleException("Cannot find extension from DSL with data");
        }
        return extension;
    }

    /**
     * <p>A method for getting getters of a certain class from a task</p>
     * For example this code returns all {@link Property} getters:
     *  <pre>
     *  {@code Method[] methods = getTypedGetters(Property.class)}
     *  </pre>
     * @param type The type that getters should return
     * @return Array of getter methods
     */
    private Method[] getTypedGetters(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("get") && type.isAssignableFrom(method.getReturnType())) {
                methods.add(method);
            }
        }
        return methods.toArray(new Method[]{});
    }

    /**
     * A method for set value to the field of task object from DSL extension
     *
     * @param cMakeExtension DSL extension with data in the fields
     * @param type Type of fields that will be handled by this method
     * @param setterMethod Method for set value of the parsed fields
     * @param <T> Parametrized type of {@link Class} from type param
     */
    protected final <T> void setTypedFields(CMakeExtension cMakeExtension, Class<T> type, Method setterMethod) {
        for (Method method : getTypedGetters(type)) {
            try {
                T field = type.cast(method.invoke(this));
                Object getterValue = cMakeExtension.getClass().getMethod(method.getName()).invoke(cMakeExtension);
                if (type.isInstance(getterValue)) {
                    T fieldValue = type.cast(getterValue);
                    setterMethod.invoke(field, fieldValue);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new GradleException(e.getMessage(), e);
            }
        }
    }

    /**
     * A method that parses all properties from the task
     * and automatically sets them values from the DSL extension
     * @param extension DSL extension from the build configuration
     */
    protected final void setProperties(CMakeExtension extension) {
        try {
            Method setterMethod = Property.class.getMethod("set", Provider.class);
            setTypedFields(extension, Property.class, setterMethod);
        } catch (NoSuchMethodException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }

    /**
     * Logs all the Provider of this task. The logging function is passed in arguments.
     * @param function The logging function
     */
    protected final void logProviders(Consumer<? super String> function) {
        for (Method method : getTypedGetters(Provider.class)) {
            String filedName = method.getName().replace("get", "");
            filedName = filedName.substring(0, 1).toLowerCase() + filedName.substring(1);
            try {
                function.accept("The value of the %s property: %s".formatted(filedName, ((Provider<?>) method.invoke(this)).getOrNull()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GradleException(e.getMessage(), e);
            }
        }
    }

}
