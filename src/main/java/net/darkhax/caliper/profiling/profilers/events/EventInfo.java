package net.darkhax.caliper.profiling.profilers.events;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.caliper.Caliper;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EventInfo {

    private String event = "unknown";
    private String owner = "unknown";
    private String source = "unknown";
    private String location = "unknown";
    private String method = "unknown";
    private String priority = "unknown";
    private String recievedCanceled = "unknown";
    private boolean isModEvent = true;

    public EventInfo (ASMEventHandler event) {

        // Splits the readable info into parts, so we can grab info no longer available.
        final String[] parts = event.toString().split(" ");

        try {

            if (!parts[1].equalsIgnoreCase("forge")) {

                // Sets info from readable data
                this.location = parts[1].substring(0, parts[1].lastIndexOf("@"));
                this.event = parts[2].substring(parts[2].lastIndexOf("/") + 1, parts[2].length() - 3);
                this.method = parts[2].substring(0, parts[2].indexOf("("));

                // Gets info from the annotation
                final SubscribeEvent subInfo = ReflectionHelper.getPrivateValue(ASMEventHandler.class, event, "subInfo");

                if (subInfo != null) {

                    this.priority = subInfo.priority().name().toLowerCase();
                    this.recievedCanceled = Boolean.toString(subInfo.receiveCanceled());
                }

                final ModContainer ownerInfo = ReflectionHelper.getPrivateValue(ASMEventHandler.class, event, "owner");

                if (this.owner != null) {

                    this.owner = ownerInfo.getName();
                    this.source = ownerInfo.getSource() != null ? ownerInfo.getSource().getName() : "unknown";
                }
            }

            else {

                this.isModEvent = false;
            }
        }

        catch (final StringIndexOutOfBoundsException e) {

            Caliper.LOG.error("Unable to parse event listener: " + event.toString());
        }
    }

    public static TableBuilder<EventInfo> createTable () {

        final TableBuilder<EventInfo> table = new TableBuilder<>();

        table.addColumn("Owner", info -> info.owner);
        table.addColumn("Method", info -> info.method);
        table.addColumn("Location", info -> info.location);
        table.addColumn("Priority", info -> info.priority);
        table.addColumn("Source", info -> info.source);
        table.addColumn("RecieveCandeled", info -> info.recievedCanceled);
        return table;
    }

    public String getEvent () {

        return this.event;
    }

    public boolean isValid () {

        return this.isModEvent;
    }
}