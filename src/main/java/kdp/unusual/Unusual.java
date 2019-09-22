package kdp.unusual;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kdp.unusual.tile.StaringGeneratorTile;

@Mod(Unusual.MOD_ID)
public class Unusual {

    public static final String MOD_ID = "unusual";
    public static final Logger LOG = LogManager.getLogger(Unusual.class);

    public Unusual() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        Config.init();
        MinecraftForge.EVENT_BUS.addListener(this::worldTick);
        MinecraftForge.EVENT_BUS.addListener(this::render);
        //MinecraftForge.EVENT_BUS.addListener(this::mouse);
        Arrays.stream(Generator.values()).filter(generator -> Config.configs.get(generator).getEnabled().get())
                .forEach(generator -> generator.block.register());
    }

    private boolean floatAway = false;

    private void worldTick(TickEvent.ClientTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.END && Minecraft.getInstance().world != null) {
            RayTraceResult result = Minecraft.getInstance().objectMouseOver;
            boolean wasFalse = !floatAway || Minecraft.getInstance().world.getGameTime() % 10 == 0;
            floatAway = false;
            if (result instanceof BlockRayTraceResult && !Minecraft.getInstance().isGamePaused()) {
                Optional.ofNullable(
                        Minecraft.getInstance().world.getTileEntity(((BlockRayTraceResult) result).getPos()))//
                        .filter(StaringGeneratorTile.class::isInstance)//
                        .map(StaringGeneratorTile.class::cast)//
                        .ifPresent(t -> floatAway = true);
            }
            if (wasFalse && floatAway) {
                Random ran = new Random();
                yawDelta = (float) Math.sin(ran.nextInt()) / 6F;
                pitchDelta = (float) Math.sin(ran.nextInt()) / 6F;
            }
        }
    }

    float yawDelta, pitchDelta;

    private void render(TickEvent.RenderTickEvent event) {
        if (floatAway) {
            PlayerEntity player = Minecraft.getInstance().player;
            int fps = Minecraft.getDebugFPS();
            if (fps > 0) {
                player.rotationYaw += yawDelta * (60f / fps);
                player.rotationPitch += pitchDelta * (60f / fps);
            }
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

}
