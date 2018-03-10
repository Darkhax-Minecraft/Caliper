package net.darkhax.caliper.profiling.profilers.loadtimes;

/**
 * Functional interface for adding info to a load info object.
 */
interface InfoAdder {

    void addInfo (LoadInfo info, double amount);
}