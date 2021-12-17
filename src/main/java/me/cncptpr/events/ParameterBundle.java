package me.cncptpr.events;

import java.util.LinkedList;
import java.util.List;

public class ParameterBundle {

    private static final List<ParameterBundle> bundles = new LinkedList<>();

    public static ParameterBundle get(Class<?>[] parameters) {
        ParameterBundle output = new ParameterBundle(parameters);

        for (ParameterBundle bundle : bundles)
            if (bundle.hasParameters(output))
                return bundle;

        bundles.add(output);
        return output;
    }


    private final Class<?>[] parameters;

    private ParameterBundle(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Class<?>[])
            return hasParameters((Class<?>[]) obj);
        if (obj instanceof ParameterBundle)
            return hasParameters((ParameterBundle) obj);
        return super.equals(obj);
    }

    public boolean hasParameters(Class<?>[] compare) {
        if(compare.length != parameters.length)
            return false;

        for (int i = 0; i < parameters.length; i++)
            if (!parameters[i].equals(compare[i]))
                return false;

        return true;
    }

    public boolean hasParameters(ParameterBundle compare) {
        return compare.hasParameters(parameters);
    }
}
