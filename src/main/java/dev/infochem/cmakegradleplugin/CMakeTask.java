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


public abstract class CMakeTask extends DefaultTask {
    @SuppressWarnings("unused")
    abstract public void execute();

    @OutputDirectory
    abstract public DirectoryProperty getBuildDirectory();

    @InputFile
    abstract public Property<String> getCMakeExecutable();

    abstract protected List<String> buildCommandLine();

    @Internal
    protected final CMakeExtension getExtension() {
        CMakeExtension extension = (CMakeExtension) getProject().getExtensions().findByName("cmake");
        if (extension == null) {
            throw new GradleException("Cannot find extension from DSL with data");
        }
        return extension;
    }

    private Method[] getTypedGetters(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("get") && type.isAssignableFrom(method.getReturnType())) {
                methods.add(method);
            }
        }
        return methods.toArray(new Method[]{});
    }

    protected final void setProperties(CMakeExtension extension) {
        for (Method method : getTypedGetters(Property.class)) {
            try {
                Property<?> property = (Property<?>) method.invoke(this);
                Object getterValue = extension.getClass().getMethod(method.getName()).invoke(extension);
                if (getterValue instanceof Property<?> valueProperty) {
                    Method setMethod = Property.class.getDeclaredMethod("set", Object.class);
                    setMethod.invoke(property, valueProperty.getOrNull());
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
                throw new GradleException(e.getMessage(), e);
            }
        }
    }

    protected final void logProviders() {
        for (Method method : getTypedGetters(Provider.class)) {
            String filedName = method.getName().replace("get", "");
            filedName = filedName.substring(0, 1).toLowerCase() + filedName.substring(1);
            try {
                getLogger().debug("The value of the {} property: {}", filedName, ((Provider<?>) method.invoke(this)).getOrNull());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GradleException(e.getMessage(), e);
            }
        }
    }

}
