package kdp.unusual;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import kdp.unusual.block.GeneratorBlock;
import kdp.unusual.tile.RunningGeneratorTile;
import kdp.unusual.tile.SeedGeneratorTile;
import kdp.unusual.tile.StaringGeneratorTile;

public enum Generator {
    STARING((GeneratorBlock) new GeneratorBlock("staring_generator").setTileType(StaringGeneratorTile::new), 60_000, 10,
            null),//
    RUNNING((GeneratorBlock) new GeneratorBlock("running_generator").setTileType(RunningGeneratorTile::new), 80_000, 20,
            null),//
    SEED((GeneratorBlock) new GeneratorBlock("seed_generator").setTileType(SeedGeneratorTile::new), 60_000, 100, null);

    public final String name;
    public final GeneratorBlock block;
    public final TileEntityType tileEntityType;
    public final int defaultStorage, defaultMaxGeneration;
    public final Function<ForgeConfigSpec.Builder, Map<String, ForgeConfigSpec.ConfigValue>> configFunction;
    public final Map<String, ForgeConfigSpec.ConfigValue> additionalConfigs = new HashMap<>();

    Generator(GeneratorBlock block, int defaultStorage, int defaultMaxGeneration,
            @Nullable Function<ForgeConfigSpec.Builder, Map<String, ForgeConfigSpec.ConfigValue>> configFunction) {
        this.block = block;
        this.name = block.getRegistryName().getPath();
        this.tileEntityType = Objects.requireNonNull(block.getTileEntityType());
        this.defaultStorage = defaultStorage;
        this.defaultMaxGeneration = defaultMaxGeneration;
        this.configFunction = configFunction;
    }
}
