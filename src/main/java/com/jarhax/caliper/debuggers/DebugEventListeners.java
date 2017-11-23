package com.jarhax.caliper.debuggers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jarhax.caliper.Caliper;
import com.jarhax.caliper.TableBuilder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class DebugEventListeners {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static final String NEW_LINE = System.lineSeparator();

    public static void printAllListeners () {

        try {

            final File file = new File("logs/caliper/event-analysis.md");
            final FileWriter writer = new FileWriter(file, false);

            writer.append("# Analysis - " + TIME_FORMAT.format(new Date()));
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(WordUtils.wrap("This file contains info about every registered event listener that was registered when FML had finished loading. This analysis was performed by the mod Caliper. This info can be very valuable when debugging weird and obscure issues, and can also find potential sources of lag. Please note that highest priority events are fired first, and lowest priority events are fired last. Aparently this confuses some people. This data is anonymous, and is not automatically submitted to any online service.", 80));
            writer.append(NEW_LINE + NEW_LINE);

            writeBusInfo(writer, MinecraftForge.EVENT_BUS, "EVENT_BUS");
            writeBusInfo(writer, MinecraftForge.ORE_GEN_BUS, "ORE_GEN_BUS");
            writeBusInfo(writer, MinecraftForge.TERRAIN_GEN_BUS, "TERRAIN_GEN_BUS");

            writer.close();
        }

        catch (final IOException e) {

        }
    }

    public static void writeBusInfo (FileWriter writer, EventBus bus, String busName) throws IOException {

        final Multimap<String, EventInfo> info = getListeners(bus);

        for (final String key : info.keySet()) {

            final TableBuilder<EventInfo> table = getCleanTable(info.get(key));

            writer.append(NEW_LINE + NEW_LINE);
            writer.append("## " + key + " - " + busName);
            writer.append(NEW_LINE + NEW_LINE);
            writer.append(table.createString());
        }
    }

    public static TableBuilder<EventInfo> getCleanTable (Collection<EventInfo> collection) {

        final TableBuilder<EventInfo> table = new TableBuilder<>();

        table.addColumn("Owner", (info) -> info.owner);
        table.addColumn("Method", (info) -> info.method);
        table.addColumn("Location", (info) -> info.location);
        table.addColumn("Priority", (info) -> info.priority);
        table.addColumn("Source", (info) -> info.source);
        table.addColumn("RecieveCandeled", (info) -> info.recievedCanceled);

        for (final EventInfo info : collection) {

            table.addEntry(info);
        }

        return table;
    }

    public static Multimap<String, EventInfo> getListeners (EventBus bus) {

        final Multimap<String, EventInfo> sortedListeners = HashMultimap.create();
        final ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = ReflectionHelper.getPrivateValue(EventBus.class, bus, "listeners");

        for (final Entry<Object, ArrayList<IEventListener>> s : listeners.entrySet()) {

            for (final IEventListener listener : s.getValue()) {

                if (listener instanceof ASMEventHandler) {

                    try {

                        final EventInfo info = new EventInfo((ASMEventHandler) listener);

                        if (info.isModEvent) {

                            sortedListeners.put(info.event, info);
                        }
                    }

                    catch (final Exception e) {

                        Caliper.LOG.error(listener.toString());
                        Caliper.LOG.catching(e);
                        ;
                    }
                }
            }
        }

        return sortedListeners;
    }

    private static class EventInfo {

        public String event = "unknown";
        public String owner = "unknown";
        public String source = "unknown";
        public String location = "unknown";
        public String method = "unknown";
        public String priority = "unknown";
        public String recievedCanceled = "unknown";

        public boolean isModEvent = true;

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
    }
}