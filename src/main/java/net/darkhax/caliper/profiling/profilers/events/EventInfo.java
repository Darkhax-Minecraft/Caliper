package net.darkhax.caliper.profiling.profilers.events;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.caliper.Caliper;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventInfo {

    private String event = "unknown";
    private String owner = "unknown";
    private String source = "unknown";
    private String location = "unknown";
    private String method = "unknown";
    private String priority = "unknown";
    private String recievedCanceled = "unknown";
    private final boolean isModEvent = true;

    public EventInfo (ASMEventHandler event) {

        try {

            final String[] info = event.toString().split(" ");

            if ("ASM:".equalsIgnoreCase(info[0])) {

                final String locationInfo = info.length == 3 ? info[1] : info.length == 4 ? info[2] : "unknown";
                final String eventInfo = info.length == 3 ? info[2] : info.length == 4 ? info[3] : "unknown";

                this.location = locationInfo.contains("@") ? locationInfo.substring(0, locationInfo.lastIndexOf('@')) : locationInfo;
                this.event = eventInfo.substring(eventInfo.lastIndexOf('/') + 1, eventInfo.length() - 3);
                
                if (eventInfo.indexOf('(') > 0)
                    this.method = eventInfo.substring(0, eventInfo.indexOf('('));
                else{
                    Caliper.LOG.warn("Unable to parse event listener: {}.", event.toString());
                    return;
                }
                    
                // Gets info from the annotation
                final SubscribeEvent subInfo = ObfuscationReflectionHelper.getPrivateValue(ASMEventHandler.class, event, "subInfo");

                if (subInfo != null) {

                    this.priority = subInfo.priority().name().toLowerCase();
                    this.recievedCanceled = Boolean.toString(subInfo.receiveCanceled());
                }

                final ModContainer ownerInfo = ObfuscationReflectionHelper.getPrivateValue(ASMEventHandler.class, event, "owner");

                if (this.owner != null) {

                    this.owner = ownerInfo.getName();
                    this.source = ownerInfo.getSource() != null ? ownerInfo.getSource().getName() : "unknown";
                }
            }

            else {

                Caliper.LOG.warn("Unable to parse event listener: {}.", event.toString());
            }
        }

        catch (final Exception e) {

            Caliper.LOG.error("Unable to parse event listener: {}.", event.toString());
            Caliper.LOG.catching(e);
        }
    }

    public static TableBuilder<EventInfo> createTable () {

        final TableBuilder<EventInfo> table = new TableBuilder<>();

        table.addColumn("Owner", info -> info.owner);
        table.addColumn("Method", info -> info.method);
        table.addColumn("Location", info -> info.location);
        table.addColumn("Priority", info -> info.priority);
        table.addColumn("Source", info -> info.source);
        table.addColumn("RecieveCanceled", info -> info.recievedCanceled);
        return table;
    }

    public String getEvent () {

        return this.event;
    }

    public boolean isValid () {

        return this.isModEvent;
    }
}
