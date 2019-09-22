package kdp.unusual;

import java.util.EnumMap;
import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.commons.lang3.tuple.Pair;

public class Config {

    private static ForgeConfigSpec config;

    public static Map<Generator, GeneratorConfig> configs = new EnumMap<>(Generator.class);

    public static void init() {
        Pair<Config, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(b -> {
            b.push("Generators");
            for (Generator generator : Generator.values()) {
                b.push(generator.block.getRegistryName().getPath());
                GeneratorConfig c = new GeneratorConfig();
                c.enabled = b.comment("active").define("enabled", true);
                c.energyStorage = b.comment("storage")
                        .defineInRange("storage", generator.defaultStorage, 100, 10_000_000);
                c.function = b.comment("scalefun").defineEnum("func", ScaleFunction.LINEAR);
                c.maxGeneration = b.comment("max").defineInRange("max", generator.defaultMaxGeneration, 1, 10_000);
                if (generator.configFunction != null)
                    generator.additionalConfigs.putAll(generator.configFunction.apply(b));
                b.pop();
                configs.put(generator, c);
            }
            b.pop();
            return null;
        });
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config = pair.getValue());
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
