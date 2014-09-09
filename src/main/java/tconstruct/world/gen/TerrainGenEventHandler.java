package tconstruct.world.gen;

import com.google.common.collect.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;

import static net.minecraft.world.biome.BiomeGenBase.*;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND;

public class TerrainGenEventHandler
{
    private final SurfaceOreGen ironSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 0, 12, true);
    private final SurfaceOreGen goldSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 1, 20, true);
    private final SurfaceOreGen copperSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 2, 12, true);
    private final SurfaceOreGen tinSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 3, 12, true);
    private final SurfaceOreGen aluminumSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 4, 12, true);
    private final SurfaceOreGen cobaltSurface = new SurfaceOreGen(TinkerWorld.oreGravel, 5, 30, true);

    private static ImmutableCollection<BiomeGenBase> EXTRA_ORE_BIOMES = ImmutableList.of(extremeHills, extremeHillsEdge);

    @SubscribeEvent
    public void onDecorateEvent (Decorate e)
    {
        // Trigger just before sand pass one--which comes just after vanilla ore
        // generation.
        if (e.type != SAND)
            return;

        BiomeGenBase biome = e.world.getWorldChunkManager().getBiomeGenAt(e.chunkX, e.chunkZ);
        int iterations = EXTRA_ORE_BIOMES.contains(biome) ? 2 : 1;
        for (int i = 0; i < iterations; i++)
        {
            generateSurfaceOres(e.rand, e.chunkX, e.chunkZ, e.world);
        }
    }

    private void generateSurfaceOres (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateIronSurface && random.nextInt(PHConstruct.ironsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            ironSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateGoldSurface && random.nextInt(PHConstruct.goldsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            goldSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateCopperSurface && random.nextInt(PHConstruct.coppersRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            copperSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateTinSurface && random.nextInt(PHConstruct.tinsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            tinSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateAluminumSurface && random.nextInt(PHConstruct.aluminumsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            aluminumSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateCobaltSurface && random.nextInt(PHConstruct.cobaltsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            cobaltSurface.generate(world, random, xPos, yPos, zPos);
        }
    }
}
