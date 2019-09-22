package kdp.unusual.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;

import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.helper.nbt.NBTHelper;
import kdp.unusual.Generator;

public class RunningGeneratorTile extends GeneratorTile {

    private static double maxCharge = 30;
    private double charge;

    public RunningGeneratorTile() {
        super(Generator.RUNNING.tileEntityType);
    }

    @Override
    protected int getEnergyPerTick() {
        return (int) Math.floor(config.getMaxGeneration().get() * config.getFunction().get().function
                .applyAsDouble(charge / maxCharge));
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote && world.getGameTime() % 5 == 1) {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                double fullSpeed = world.getPlayers().stream()//
                        .filter(p -> p.getEntityWorld() == world //
                                && p.getPositionVec().squareDistanceTo(pos.getX() + .5, pos.getY() + .5,
                                pos.getZ() + .5) < 64 && (p.prevPosX != p.posX || p.prevPosZ != p.posZ))
                        .mapToDouble(p -> p.getPositionVec().subtract(p.prevPosX, p.prevPosY, p.prevPosZ).length())
                        .sum();
                sendMessage(NBTBuilder.of().set("c", fullSpeed).build());
            });
        }
    }

    @Override
    public void handleMessage(PlayerEntity player, CompoundNBT nbt) {
        super.handleMessage(player, nbt);
        if (!player.world.isRemote) {
            NBTHelper.getOptional(nbt, "c", double.class).ifPresent(
                    d -> charge = d == 0 ? Math.max(0, charge - maxCharge / 15) : Math.min(charge + d, maxCharge));
        }
    }

    @Override
    protected Generator getGeneratorType() {
        return Generator.RUNNING;
    }
}
