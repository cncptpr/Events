package me.cncptpr.events;

import me.cncptpr.test.events.DefaultEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventManager {

    public static final EventManager events = new EventManager();

    private final HashMap<ParameterBundle, List<MethodReference>> types = new HashMap<>();

    /**
     * Registers the {@code listener}
     * All methods in the {@link Object} with the {@link Event} Annotation will be called, if the parameter types equals the event types getting passed into the {@code call}-method.
     * @param listener The {@link Object} getting registered
     */
    public void register(Object listener) {
        for (MethodReference method : getAllListenerMethods(listener)) {
            if (!types.containsKey(method.bundle()))
                types.put(method.bundle(), new LinkedList<>());
            types.get(method.bundle()).add(method);
        }

    }

    /**
     * Removes the {@code listener} from registry.
     * Method will no longer be called.
     * @param listener The {@link Object} getting removed
     */
    private void remove(Object listener) {
        for (MethodReference method : getAllListenerMethods(listener))
            if(types.containsKey(method.bundle()))
                types.get(method.bundle()).remove(method);
    }

    /**
     * Calls an event.
     * All {@code Methods} in the registered {@code Listeners} with the {@link Event}-Annotation and the fitting {@code Parameter Types} are being called
     * @param parameters The {@code Event} parameters
     */
    public void call(Object... parameters) {
        ParameterBundle bundle = extractParameterBundle(parameters);
        for (MethodReference methodReference : types.get(bundle)) {
            methodReference.invoke(parameters);
        }
    }

    private void tryInvoke(DefaultEvent event, Object listener, Method method) {
        try {
            method.invoke(listener, event.getClass().cast(event));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isListenerMethod(Method method, Class<?> currEventType) {
        return hasEventAnnotation(method) && hasRightParameter(method, currEventType);
    }

    private boolean hasRightParameter(Method method, Class<?> currEventType) {
        return method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(currEventType);
    }

    private boolean hasEventAnnotation(Method method) {
        return method.isAnnotationPresent(Event.class);
    }

    private ParameterBundle extractParameterBundle(Object[] parameters) {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++)
            parameterTypes[i] = parameters[i].getClass();
        return ParameterBundle.get(parameterTypes);

    }

    private MethodReference[] getAllListenerMethods(Object listener) {
        List<MethodReference> listenerMethods = new LinkedList<>();
        for (Method method : listener.getClass().getDeclaredMethods())
            if (hasEventAnnotation(method))
                listenerMethods.add(new MethodReference(listener, method, ParameterBundle.get(method.getParameterTypes())));
        return listenerMethods.toArray(new MethodReference[0]);
    }


}
