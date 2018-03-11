package net.darkhax.caliper.profiling.profilers.errors;

public abstract class ErrorReporter implements Comparable<ErrorReporter> {

    public abstract void collectErrors ();

    public abstract String getErrors ();

    public abstract String getDescription ();

    public abstract String getName ();

    public abstract Level getErrorLevel ();

    public abstract boolean foundErrors ();

    @Override
    public int compareTo (ErrorReporter o) {

        return this.getErrorLevel().compareTo(o.getErrorLevel());
    }
}