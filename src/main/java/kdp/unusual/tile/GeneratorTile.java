package kdp.unusual.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.helper.nbt.NBTHelper;
import kdp.limelib.tile.GenericTile;
import kdp.limelib.util.EnergyStorageExt;
import kdp.limelib.util.LimeEnums;
import kdp.unusual.Config;
import kdp.unusual.Generator;

public abstract class GeneratorTile extends GenericTile implements ITickableTileEntity {

    protected Config.GeneratorConfig config = Config.configs.get(getGeneratorType());
    private final EnergyStorageExt energyStorage = new EnergyStorageExt(config.getEnergyStorage().get(),
            config.getEnergyStorage().get() / 15) {
        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        protected void onContentsChanged() {
            markDirty();
        }
    };
    protected final LazyOptional<IEnergyStorage> energyHolder = LazyOptional.of(() -> energyStorage);
    private LimeEnums.ActivateMode activateMode = LimeEnums.ActivateMode.ALWAYS_ON;
    protected int energyPerTickClient = 0;

    public GeneratorTile(TileEntityType tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void readFromSyncNBT(CompoundNBT compound) {
        activateMode = NBTHelper.get(compound, "m", LimeEnums.ActivateMode.class);
    }

    @Override
    public CompoundNBT writeToSyncNBT(CompoundNBT compound) {
        return NBTBuilder.of(compound).set("m", activateMode).build();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        energyStorage.setEnergyStored(NBTHelper.get(compound, "e", Integer.class));
        readFromSyncNBT(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        writeToSyncNBT(compound);
        compound.putInt("e", energyStorage.getEnergyStored());
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (!world.isRemote()) {
            int energyPerTick = getEnergyPerTick();
            if ((activateMode == LimeEnums.ActivateMode.ALWAYS_ON//
                    || (activateMode == LimeEnums.ActivateMode.ON_WITH_REDSTONE && world.isBlockPowered(pos))//
                    || (activateMode == LimeEnums.ActivateMode.OFF_WITH_REDSTONE && !world.isBlockPowered(pos)))//
                    && energyPerTick > 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                energyStorage.modifyEnergyStored(energyPerTick);
                if (!getBlockState().get(BlockStateProperties.ENABLED)) {
                    setBlockState(getBlockState().cycle(BlockStateProperties.ENABLED), 2);
                }
            } else {
                if (getBlockState().get(BlockStateProperties.ENABLED)) {
                    setBlockState(getBlockState().cycle(BlockStateProperties.ENABLED), 2);
                }
            }
            if (world.getGameTime() % 10 == 5) {
                sendMessage(NBTBuilder.of().set("ec", energyPerTick).set("e", energyStorage.getEnergyStored()).build());
            }
        }
    }

    protected abstract int getEnergyPerTick();

    protected abstract Generator getGeneratorType();

    public int getEnergyPerTickClient() {
        return energyPerTickClient;
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void handleMessage(PlayerEntity player, CompoundNBT nbt) {
        if (player.world.isRemote) {
            NBTHelper.getOptional(nbt, "ec", int.class).ifPresent(i -> energyPerTickClient = i);
            NBTHelper.getOptional(nbt, "e", int.class).ifPresent(i -> energyStorage.setEnergyStored(i));
        } else {

        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHolder.cast();
        }
        return super.getCapability(cap, side);
    }
}
