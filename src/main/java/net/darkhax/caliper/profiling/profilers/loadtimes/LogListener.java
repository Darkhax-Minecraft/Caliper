package net.darkhax.caliper.profiling.profilers.loadtimes;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class LogListener extends AbstractFilter {

    @Override
    public Result filter (LogEvent event) {

        if (event.getMessage().getFormattedMessage().startsWith("Bar Step: ")) {

            final String barStep = event.getMessage().getFormattedMessage();

            for (final InfoType info : InfoType.values()) {

                if (info.processMessage(barStep)) {

                    break;
                }
            }
        }

        return Result.NEUTRAL;
    }
}
