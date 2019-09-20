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
import kdp.unusual.Generator;
import kdp.unusual.ModConfig;

public abstract class GeneratorTile extends GenericTile implements ITickableTileEntity {

    protected ModConfig.GeneratorConfig config = ModConfig.configs.get(getGeneratorType());
    private final EnergyStorageExt energyStorage = new EnergyStorageExt(config.getEnergyStorage().get(),
            config.getEnergyStorage().get() / 15) {
        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        protected void onContentsChanged() {
            sync();
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
        energyStorage.setEnergyStored(NBTHelper.get(compound, "e", Integer.class));
        activateMode = NBTHelper.get(compound, "m", LimeEnums.ActivateMode.class);
    }

    @Override
    public CompoundNBT writeToSyncNBT(CompoundNBT compound) {
        return NBTBuilder.of().set("e", energyStorage.getEnergyStored()).set("m", activateMode).build();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readFromSyncNBT(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        writeToSyncNBT(compound);
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (!world.isRemote()) {
            int energyPerTick = getEnergyPerTick();
            if (activateMode == LimeEnums.ActivateMode.ALWAYS_ON//
                    || (activateMode == LimeEnums.ActivateMode.ON_WITH_REDSTONE && world.isBlockPowered(pos))//
                    || (activateMode == LimeEnums.ActivateMode.OFF_WITH_REDSTONE && !world.isBlockPowered(pos))//
                    || energyPerTick > 0) {
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
                sendMessage(NBTBuilder.of().set("ec", energyPerTick).build());
            }
        }
    }

    protected abstract int getEnergyPerTick();

    protected abstract Generator getGeneratorType();

    public int getEnergyPerTickClient() {
        return energyPerTickClient;
    }

    @Override
    public void handleMessage(PlayerEntity player, CompoundNBT nbt) {
        if (player.world.isRemote) {
            NBTHelper.getOptional(nbt, "ec", int.class).ifPresent(i -> energyPerTickClient = i);
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
