package me.cncptpr.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record MethodReference(Object listener, Method method, ParameterBundle bundle) {

    public boolean hasEventAnnotation() {
        return method.isAnnotationPresent(Event.class);
    }

    public void invoke(Object[] parameters) {
        try {
            method.invoke(listener, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean hasParameters(Class<?>[] parameters) {
        return bundle.hasParameters(parameters);
    }

    public boolean hasParameters(ParameterBundle bundle) {
        return this.bundle.equals(bundle);
    }
}
