package kdp.unusual;

import net.minecraft.tileentity.TileEntityType;

import kdp.unusual.block.GeneratorBlock;
import kdp.unusual.tile.StaringGeneratorTile;

public enum Generator {
    STARING((GeneratorBlock) new GeneratorBlock("staring_generator").setTileType(StaringGeneratorTile::new));

    public final String name;
    public final GeneratorBlock block;
    public final TileEntityType tileEntityType;

    Generator(GeneratorBlock block) {
        this.block = block;
        this.name = block.getRegistryName().getPath();
        this.tileEntityType = block.getTileEntityType();
    }
}
