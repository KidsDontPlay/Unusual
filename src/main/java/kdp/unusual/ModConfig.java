package kdp.unusual;

import java.util.EnumMap;
import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    private static ForgeConfigSpec config;

    public static Map<Generator, GeneratorConfig> configs = new EnumMap<>(Generator.class);

    public static void init() {
        Pair<ModConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(b -> {
            return null;
        });
        ModLoadingContext.get()
                .registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, config = pair.getValue());
    }

    public static class GeneratorConfig {

        private ForgeConfigSpec.BooleanValue enabled;
        private ForgeConfigSpec.IntValue energyStorage;
        private ForgeConfigSpec.IntValue maxGeneration;
        private ForgeConfigSpec.EnumValue<ScaleFunction> function;

        public ForgeConfigSpec.BooleanValue getEnabled() {
            return enabled;
        }

        public ForgeConfigSpec.IntValue getEnergyStorage() {
            return energyStorage;
        }

        public ForgeConfigSpec.IntValue getMaxGeneration() {
            return maxGeneration;
        }

        public ForgeConfigSpec.EnumValue<ScaleFunction> getFunction() {
            return function;
        }
    }
}
