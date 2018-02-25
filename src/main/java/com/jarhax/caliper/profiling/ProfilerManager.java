package com.jarhax.caliper.profiling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.jarhax.caliper.Caliper;

import net.darkhax.bookshelf.util.AnnotationUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

public class ProfilerManager {

    private final List<Profiler> features = new ArrayList<>();

    public void init (ASMDataTable asmDataTable) {

        for (final Entry<Profiler, RegisterProfiler> profiler : AnnotationUtils.getAnnotations(asmDataTable, RegisterProfiler.class, Profiler.class).entrySet()) {

            final RegisterProfiler annotation = profiler.getValue();

            if (annotation == null) {

                Caliper.LOG.warn("Annotation for {} was null!", profiler.getKey().getClass().getCanonicalName());
                continue;
            }

            this.registerFeature(profiler.getKey(), annotation.name(), annotation.description());
        }
    }

    private void registerFeature (Profiler profiler, String name, String description) {

        if (profiler.isEnabled()) {

            this.features.add(profiler);

            if (profiler.hasEvents()) {

                MinecraftForge.EVENT_BUS.register(profiler);
            }
        }
    }

    public boolean isLoaded () {

        return !this.getFeatures().isEmpty();
    }

    public List<Profiler> getFeatures () {

        return this.features;
    }
}