package mods.tinker.tconstruct.dimension;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TinkerWorldProvider extends WorldProvider
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new TinkerChunkManager(BiomeGenBase.iceMountains, 1.0F, 0.0F);
        this.isHellWorld = true;
        this.hasNoSky = true;
        this.dimensionId = -7;
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float f = 1.0F;

        for (int i = 0; i <= 15; ++i)
        {
            float f1 = 1.0F;
            this.lightBrightnessTable[i] = 1.0f;
        }
    }

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    public IChunkProvider createChunkGenerator()
    {
        return new TinkerChunkProvider(this.worldObj, this.worldObj.getSeed());
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    public boolean isSurfaceWorld()
    {
        return false;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return false;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.5F;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    public boolean doesXZShowFog(int par1, int par2)
    {
        return false;
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    public String getDimensionName()
    {
        return "Skylands";
    }
}
