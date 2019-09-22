package kdp.unusual.client;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import kdp.unusual.Unusual;
import kdp.unusual.tile.GeneratorTile;

@Mod.EventBusSubscriber(modid = Unusual.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void overlay(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getInstance();
        RayTraceResult result = mc.objectMouseOver;
        TileEntity tile = null;
        if (result instanceof BlockRayTraceResult && (tile = mc.world
                .getTileEntity(((BlockRayTraceResult) result).getPos())) != null && tile instanceof GeneratorTile) {
            int energy = ((GeneratorTile) tile).getEnergy();
            int energyPerTick = ((GeneratorTile) tile).getEnergyPerTickClient();
            mc.fontRenderer.drawStringWithShadow("Energy: " + energy, 2, 2, Color.yellow.getRGB());
            mc.fontRenderer.drawStringWithShadow("Energy/tick: " + energyPerTick, 2, 12, Color.yellow.getRGB());
        }
    }
}
