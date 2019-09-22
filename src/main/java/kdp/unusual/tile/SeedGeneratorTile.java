package kdp.unusual.tile;

import kdp.unusual.Generator;

public class SeedGeneratorTile extends GeneratorTile {
    public SeedGeneratorTile() {
        super(Generator.SEED.tileEntityType);
    }

    @Override
    protected int getEnergyPerTick() {
        int hash = Long.hashCode(world.getSeed());
        return Math.abs(hash == Integer.MIN_VALUE ? hash + 1 : hash) % config.getMaxGeneration().get();
    }

    @Override
    protected Generator getGeneratorType() {
        return Generator.SEED;
    }
}
