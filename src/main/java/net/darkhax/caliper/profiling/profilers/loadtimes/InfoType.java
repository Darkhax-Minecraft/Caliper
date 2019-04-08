package net.darkhax.caliper.profiling.profilers.loadtimes;

import net.darkhax.caliper.Caliper;

/**
 * Enum containing the basic load time trackerss.
 */
enum InfoType {

    PRE_INIT("PreInitialization", (info, amount) -> info.preInit += amount),
    INIT("Initialization", (info, amount) -> info.init += amount),
    IMC("InterModComms$IMC", (info, amount) -> info.imc += amount),
    POST_INIT("PostInitialization", (info, amount) -> info.postInit += amount);

    private final String needle;
    private final InfoAdder op;

    InfoType (String needle, InfoAdder op) {

        this.needle = "Bar Step: " + needle + " - ";
        this.op = op;
    }

    public boolean processMessage (String message) {

        if (message.startsWith(this.needle)) {

            try {
                
                // Splits data into mod name and load time.
                final String[] info = message.replace(this.needle, "").split(" took ");
                info[1] = info[1].substring(0, info[1].length() - 1);

                // Adds the time to the info object.
                this.op.addInfo(DebugLoadtimes.getLoadInfo(info[0]), Double.parseDouble(info[1]));

                return true;
            }
            
            catch (Exception e) {
                
                Caliper.LOG.warn("Failed to parse line, it will be ignored. {}");
                Caliper.LOG.catching(e);
            }
        }

        return false;
    }
}