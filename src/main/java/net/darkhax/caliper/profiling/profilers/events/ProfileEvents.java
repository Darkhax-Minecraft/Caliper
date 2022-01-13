package net.darkhax.caliper.profiling.profilers.events;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.caliper.Caliper;
import net.darkhax.caliper.FileHelper;
import net.darkhax.caliper.profiling.Profiler;
import net.darkhax.caliper.profiling.RegisterProfiler;
import net.darkhax.caliper.profiling.profilers.InfoPair;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

@RegisterProfiler(name = "Event Analysis", description = "Digs through registered forge events to dump usage info.")
public class ProfileEvents extends Profiler {

    private final Multimap<String, EventInfo> allEvents = HashMultimap.create();

    @Override
    public void onLoadComplete () {

        this.scrapeEventBusses(MinecraftForge.EVENT_BUS, MinecraftForge.ORE_GEN_BUS, MinecraftForge.TERRAIN_GEN_BUS);

        final TableBuilder<InfoPair> eventCounts = InfoPair.createDataTable("Event Name", "Listener Count");

        for (final String eventName : this.allEvents.keySet()) {

            eventCounts.addEntry(new InfoPair(eventName, this.allEvents.get(eventName).size()));
        }

        Collections.sort(eventCounts.getEntries(), Collections.reverseOrder());

        try (final FileWriter writer = new FileWriter(new File(Caliper.LOG_DIR, "events.md"), false)) {

            FileHelper.writeInfoBlock(writer, 1, "Event Analysis", "This file contains an analysis on the various forge event busses. This first table lists events that have listeners, in order of most listeners. Further specifics on the listenrs.", true);
            writer.append(eventCounts.createString());

            for (final String eventName : this.allEvents.keySet()) {

                writer.append(FileHelper.NEW_PARAGRAPH);
                writer.append("## " + eventName + FileHelper.NEW_LINE);
                final TableBuilder<EventInfo> table = EventInfo.createTable();

                for (final EventInfo info : this.allEvents.get(eventName)) {

                    table.addEntry(info);
                }

                writer.append(table.createString());
            }
        }

        catch (final IOException e) {

            Caliper.LOG.catching(e);
        }
    }

    private void scrapeEventBusses (EventBus... busses) {

        for (final EventBus bus : busses) {

            final ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = ObfuscationReflectionHelper.getPrivateValue(EventBus.class, bus, "listeners");

            for (final Entry<Object, ArrayList<IEventListener>> s : listeners.entrySet()) {

                for (final IEventListener listener : s.getValue()) {

                    if (listener instanceof ASMEventHandler) {

                        try {

                            final EventInfo info = new EventInfo((ASMEventHandler) listener);

                            if (info.isValid()) {

                                this.allEvents.put(info.getEvent(), info);
                            }
                        }

                        catch (final Exception e) {

                            Caliper.LOG.error(listener.toString());
                            Caliper.LOG.catching(e);
                        }
                    }
                }
            }
        }
    }
}
