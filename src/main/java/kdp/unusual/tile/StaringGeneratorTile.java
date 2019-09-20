package kdp.unusual.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.helper.nbt.NBTHelper;
import kdp.unusual.Generator;

public class StaringGeneratorTile extends GeneratorTile {

    private static int maxCharge = 100;
    private int charge;
    private boolean looking;

    public StaringGeneratorTile() {
        super(Generator.STARING.tileEntityType);
    }

    @Override
    protected int getEnergyPerTick() {
        return (int) Math.floor(config.getMaxGeneration().get() * config.getFunction().get().function
                .applyAsDouble(charge / (double) maxCharge));
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote) {
            if (looking) {
                charge = Math.min(charge + 1, maxCharge);
            } else {
                charge = 0;
            }
        }
        if (world.isRemote && world.getGameTime() % 5 == 2) {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
                RayTraceResult result = Minecraft.getInstance().objectMouseOver;
                sendMessage(NBTBuilder.of().set("l",
                        ((result instanceof BlockRayTraceResult && ((BlockRayTraceResult) result).getPos()
                                .equals(pos)))).build());

            });
        }
    }

    @Override
    public void handleMessage(PlayerEntity player, CompoundNBT nbt) {
        super.handleMessage(player, nbt);
        if (!player.world.isRemote) {
            NBTHelper.getOptional(nbt, "l", boolean.class).ifPresent(this::setLooking);
        }
    }

    @Override
    protected Generator getGeneratorType() {
        return Generator.STARING;
    }

    public void setLooking(boolean looking) {
        this.looking = looking;
    }
}
