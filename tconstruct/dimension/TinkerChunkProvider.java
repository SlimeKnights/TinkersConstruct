package tconstruct.dimension;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_BRIDGE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_CAVE;

import java.util.List;
import java.util.Random;

import mods.natura.dimension.NetheriteChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCavesHell;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class TinkerChunkProvider implements IChunkProvider
{
    private Random hellRNG;

    /** A NoiseGeneratorOctaves used in generating nether terrain */
    private NoiseGeneratorOctaves netherNoiseGen1;
    private NoiseGeneratorOctaves netherNoiseGen2;
    private NoiseGeneratorOctaves netherNoiseGen3;

    /** Determines whether slowsand or gravel can be generated at a location */
    private NoiseGeneratorOctaves slowsandGravelNoiseGen;

    /**
     * Determines whether something other than nettherack can be generated at a location
     */
    private NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
    public NoiseGeneratorOctaves netherNoiseGen4;
    public NoiseGeneratorOctaves netherNoiseGen5;

    /** Is the world that the nether is getting generated. */
    private World worldObj;
    private double[] noiseField;
    private double[] secondNoiseField;
    public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();

    /**
     * Holds the noise used to determine whether slowsand can be generated at a location
     */
    private double[] slowsandNoise = new double[256];
    private double[] gravelNoise = new double[256];

    /**
     * Holds the noise used to determine whether something other than netherrack can be generated at a location
     */
    private double[] netherrackExclusivityNoise = new double[256];
    private MapGenBase netherCaveGenerator = new MapGenCavesHell();
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;

    double[] secondNoiseData1;
    double[] secondNoiseData2;
    double[] secondNoiseData3;
    double[] secondNoiseData4;
    double[] secondNoiseData5;

    {
        genNetherBridge = (MapGenNetherBridge) TerrainGen.getModdedMapGen(genNetherBridge, NETHER_BRIDGE);
        netherCaveGenerator = TerrainGen.getModdedMapGen(netherCaveGenerator, NETHER_CAVE);
    }

    public TinkerChunkProvider(World par1World, long par2)
    {
        this.worldObj = par1World;
        this.hellRNG = new Random(par2);
        this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherNoiseGen4 = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.netherNoiseGen5 = new NoiseGeneratorOctaves(this.hellRNG, 16);

        NoiseGeneratorOctaves[] noiseGens = { netherNoiseGen1, netherNoiseGen2, netherNoiseGen3, slowsandGravelNoiseGen, netherrackExculsivityNoiseGen, netherNoiseGen4, netherNoiseGen5 };
        noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.hellRNG, noiseGens);
        this.netherNoiseGen1 = noiseGens[0];
        this.netherNoiseGen2 = noiseGens[1];
        this.netherNoiseGen3 = noiseGens[2];
        this.slowsandGravelNoiseGen = noiseGens[3];
        this.netherrackExculsivityNoiseGen = noiseGens[4];
        this.netherNoiseGen4 = noiseGens[5];
        this.netherNoiseGen5 = noiseGens[6];
    }

    /**
     * Generates the shape of the terrain in the nether.
     */
    public void generateNetherTerrain (int chunkX, int chunkZ, byte[] lowerIDs)
    {
        byte noiseInit = 4;
        byte b1 = 32;
        int k = noiseInit + 1;
        byte b2 = 17;
        int l = noiseInit + 1;
        this.noiseField = this.initializeNoiseField(this.noiseField, chunkX * noiseInit, 0, chunkZ * noiseInit, k, b2, l);

        for (int iterX = 0; iterX < noiseInit; ++iterX)
        {
            for (int iterZ = 0; iterZ < noiseInit; ++iterZ)
            {
                for (int iterY = 0; iterY < 16; ++iterY)
                {
                    double noiseOffset = 0.125D;
                    double n1 = this.noiseField[((iterX + 0) * l + iterZ + 0) * b2 + iterY + 0];
                    double n2 = this.noiseField[((iterX + 0) * l + iterZ + 1) * b2 + iterY + 0];
                    double n3 = this.noiseField[((iterX + 1) * l + iterZ + 0) * b2 + iterY + 0];
                    double n4 = this.noiseField[((iterX + 1) * l + iterZ + 1) * b2 + iterY + 0];
                    double n5 = (this.noiseField[((iterX + 0) * l + iterZ + 0) * b2 + iterY + 1] - n1) * noiseOffset;
                    double n6 = (this.noiseField[((iterX + 0) * l + iterZ + 1) * b2 + iterY + 1] - n2) * noiseOffset;
                    double n7 = (this.noiseField[((iterX + 1) * l + iterZ + 0) * b2 + iterY + 1] - n3) * noiseOffset;
                    double n8 = (this.noiseField[((iterX + 1) * l + iterZ + 1) * b2 + iterY + 1] - n4) * noiseOffset;

                    for (int offsetY = 0; offsetY < 8; ++offsetY)
                    {
                        double d9 = 0.25D;
                        double d10 = n1;
                        double d11 = n2;
                        double d12 = (n3 - n1) * d9;
                        double d13 = (n4 - n2) * d9;

                        for (int offsetX = 0; offsetX < 4; ++offsetX)
                        {
                            int layerPos = offsetX + iterX * 4 << 11 | 0 + iterZ * 4 << 7 | iterY * 8 + offsetY;
                            short amountPerLayer = 128;
                            double d14 = 0.25D;
                            double lValue = d10;
                            double lOffset = (d11 - d10) * d14;

                            for (int k2 = 0; k2 < 4; ++k2)
                            {
                                int blockID = 0;

                                /*if (iterY * 8 + offsetY < b1)
                                {
                                    blockID = Block.lavaStill.blockID;
                                }*/

                                if (lValue < 0.0D)
                                {
                                    blockID = Block.dirt.blockID;
                                }

                                if (lValue < -56.0D)
                                {
                                    blockID = Block.grass.blockID;
                                }

                                lowerIDs[layerPos] = (byte) blockID;
                                layerPos += amountPerLayer;
                                lValue += lOffset;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        n1 += n5;
                        n2 += n6;
                        n3 += n7;
                        n4 += n8;
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome (int par1, int par2, byte[] lowerIDs)
    {
        //Lower nether
        byte seaLevel = 64;
        double d0 = 0.03125D;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, d0, d0, 1.0D);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, par1 * 16, 109, par2 * 16, 16, 1, 16, d0, 1.0D, d0);
        this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

        for (int iterX = 0; iterX < 16; ++iterX)
        {
            for (int iterZ = 0; iterZ < 16; ++iterZ)
            {
                boolean flag = this.slowsandNoise[iterX + iterZ * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.gravelNoise[iterX + iterZ * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                int i1 = (int) (this.netherrackExclusivityNoise[iterX + iterZ * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
                int j1 = -1;
                byte b1 = (byte) Block.dirt.blockID;
                byte b2 = (byte) Block.grass.blockID;

                for (int k1 = 127; k1 >= 0; --k1)
                {
                    int l1 = (iterZ * 16 + iterX) * 128 + k1;

                    if (k1 > 0 + this.hellRNG.nextInt(5))
                    {
                        short b3 = lowerIDs[l1];

                        if (b3 == 0)
                        {
                            j1 = -1;
                        }
                        else if (b3 == Block.dirt.blockID)
                        {
                            if (j1 == -1)
                            {
                                if (i1 <= 0)
                                {
                                    b1 = 0;
                                    b2 = (byte) Block.dirt.blockID;
                                }
                                else if (k1 >= seaLevel - 4 && k1 <= seaLevel + 1)
                                {
                                    b1 = (byte) Block.dirt.blockID;
                                    b2 = (byte) Block.grass.blockID;

                                    if (flag1)
                                    {
                                        b1 = (byte) Block.gravel.blockID;
                                    }

                                    if (flag1)
                                    {
                                        b2 = (byte) Block.dirt.blockID;
                                    }

                                    if (flag)
                                    {
                                        b1 = (byte) Block.slowSand.blockID;
                                    }

                                    if (flag)
                                    {
                                        b2 = (byte) Block.stone.blockID;
                                    }
                                }

                                /*if (k1 < seaLevel && b1 == 0)
                                {
                                    b1 = (byte) Block.lavaStill.blockID;
                                }*/

                                j1 = i1;

                                if (k1 >= seaLevel - 1)
                                {
                                    lowerIDs[l1] = b1;
                                }
                                else
                                {
                                    lowerIDs[l1] = b2;
                                }
                            }
                            else if (j1 > 0)
                            {
                                --j1;
                                lowerIDs[l1] = b2;
                            }
                        }
                    }
                    /*else
                    {
                        lowerIDs[l1] = (byte)Block.bedrock.blockID;
                    }*/
                }
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk (int par1, int par2)
    {
        return this.provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk (int chunkX, int chunkZ)
    {
        this.hellRNG.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
        byte[] lowerArray = new byte[32768];
        //byte[] upperArray = new byte[32768];
        this.generateNetherTerrain(chunkX, chunkZ, lowerArray);
        this.replaceBlocksForBiome(chunkX, chunkZ, lowerArray);
        this.netherCaveGenerator.generate(this, this.worldObj, chunkX, chunkZ, lowerArray);
        this.genNetherBridge.generate(this, this.worldObj, chunkX, chunkZ, lowerArray);
        Chunk chunk = new NetheriteChunk(this.worldObj, lowerArray, chunkX, chunkZ);
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[]) null, chunkX * 16, chunkZ * 16, 16, 16);
        byte[] abyte1 = chunk.getBiomeArray();

        for (int k = 0; k < abyte1.length; ++k)
        {
            abyte1[k] = (byte) abiomegenbase[k].biomeID;
        }

        chunk.resetRelightChecks();
        return chunk;
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField (double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble, par2, par3, par4, par5, par6, par7);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY)
            return event.noisefield;
        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        double d0 = 684.412D;
        double d1 = 2053.236D;
        this.noiseData4 = this.netherNoiseGen4.generateNoiseOctaves(this.noiseData4, par2, par3, par4, par5, 1, par7, 1.0D, 0.0D, 1.0D);
        this.noiseData5 = this.netherNoiseGen5.generateNoiseOctaves(this.noiseData5, par2, par3, par4, par5, 1, par7, 100.0D, 0.0D, 100.0D);
        this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, par2, par3, par4, par5, par6, par7, d0 / 80.0D, d1 / 60.0D, d0 / 80.0D);
        this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, par2, par3, par4, par5, par6, par7, d0, d1, d0);
        this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, par2, par3, par4, par5, par6, par7, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;
        double[] adouble1 = new double[par6];
        int i2;

        for (i2 = 0; i2 < par6; ++i2)
        {
            adouble1[i2] = Math.cos((double) i2 * Math.PI * 6.0D / (double) par6) * 2.0D;
            double d2 = (double) i2;

            if (i2 > par6 / 2)
            {
                d2 = (double) (par6 - 1 - i2);
            }

            if (d2 < 4.0D)
            {
                d2 = 4.0D - d2;
                adouble1[i2] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (i2 = 0; i2 < par5; ++i2)
        {
            for (int j2 = 0; j2 < par7; ++j2)
            {
                double d3 = (this.noiseData4[l1] + 256.0D) / 512.0D;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                double d4 = 0.0D;
                double d5 = this.noiseData5[l1] / 8000.0D;

                if (d5 < 0.0D)
                {
                    d5 = -d5;
                }

                d5 = d5 * 3.0D - 3.0D;

                if (d5 < 0.0D)
                {
                    d5 /= 2.0D;

                    if (d5 < -1.0D)
                    {
                        d5 = -1.0D;
                    }

                    d5 /= 1.4D;
                    d5 /= 2.0D;
                    d3 = 0.0D;
                }
                else
                {
                    if (d5 > 1.0D)
                    {
                        d5 = 1.0D;
                    }

                    d5 /= 6.0D;
                }

                d3 += 0.5D;
                d5 = d5 * (double) par6 / 16.0D;
                ++l1;

                for (int k2 = 0; k2 < par6; ++k2)
                {
                    double d6 = 0.0D;
                    double d7 = adouble1[k2];
                    double d8 = this.noiseData2[k1] / 512.0D;
                    double d9 = this.noiseData3[k1] / 512.0D;
                    double d10 = (this.noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

                    if (d10 < 0.0D)
                    {
                        d6 = d8;
                    }
                    else if (d10 > 1.0D)
                    {
                        d6 = d9;
                    }
                    else
                    {
                        d6 = d8 + (d9 - d8) * d10;
                    }

                    d6 -= d7;
                    double d11;

                    if (k2 > par6 - 4)
                    {
                        d11 = (double) ((float) (k2 - (par6 - 4)) / 3.0F);
                        d6 = d6 * (1.0D - d11) + -10.0D * d11;
                    }

                    if ((double) k2 < d4)
                    {
                        d11 = (d4 - (double) k2) / 4.0D;

                        if (d11 < 0.0D)
                        {
                            d11 = 0.0D;
                        }

                        if (d11 > 1.0D)
                        {
                            d11 = 1.0D;
                        }

                        d6 = d6 * (1.0D - d11) + -10.0D * d11;
                    }

                    par1ArrayOfDouble[k1] = d6;
                    ++k1;
                }
            }
        }

        return par1ArrayOfDouble;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists (int par1, int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate (IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        BlockSand.fallInstantly = true;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks (boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks ()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave ()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString ()
    {
        return "HellRandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures (EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        if (par1EnumCreatureType == EnumCreatureType.monster && this.genNetherBridge.hasStructureAt(par2, par3, par4))
        {
            return this.genNetherBridge.getSpawnList();
        }
        else
        {
            BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(par2, par4);
            return biomegenbase == null ? null : biomegenbase.getSpawnableList(par1EnumCreatureType);
        }
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure (World par1World, String par2Str, int par3, int par4, int par5)
    {
        return null;
    }

    public int getLoadedChunkCount ()
    {
        return 0;
    }

    public void recreateStructures (int par1, int par2)
    {
        this.genNetherBridge.generate(this, this.worldObj, par1, par2, (byte[]) null);
    }

    @Override
    public void func_104112_b ()
    {
        // TODO Auto-generated method stub

    }
}
