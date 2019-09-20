package kdp.unusual.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.StateContainer;
import net.minecraftforge.common.ToolType;

import kdp.limelib.block.GenericBlock;

import static net.minecraft.state.properties.BlockStateProperties.ENABLED;

public class GeneratorBlock extends GenericBlock {

    public GeneratorBlock(String name) {
        super(Properties.create(Material.IRON).hardnessAndResistance(2.5f).harvestTool(ToolType.PICKAXE), name,
                ItemGroup.REDSTONE);
        setDefaultState(getStateContainer().getBaseState().with(ENABLED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }

}
