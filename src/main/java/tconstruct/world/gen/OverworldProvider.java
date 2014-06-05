package tconstruct.world.gen;

import net.minecraft.world.WorldProvider;

public class OverworldProvider extends WorldProvider
{

    @Override
    public String getDimensionName ()
    {
        return "Overworld";
    }

    @Override
    public boolean getWorldHasVoidParticles ()
    {
        return this.terrainType.hasVoidParticles(this.hasNoSky);
    }

    @Override
    public float calculateCelestialAngle (long worldtime, float par3)
    {
        int timeOfDay = 18000;
        float f1 = ((float) timeOfDay + par3) / 24000.0F - 0.25F;

        if (f1 < 0.0F)
        {
            ++f1;
        }

        if (f1 > 1.0F)
        {
            --f1;
        }

        float f2 = f1;
        f1 = 1.0F - (float) ((Math.cos((double) f1 * Math.PI) + 1.0D) / 2.0D);
        f1 = f2 + (f1 - f2) / 3.0F;
        return f1;
    }

    /*
     * public float calculateCelestialAngle(long worldtime, float par3) { int
     * timeOfDay = (int)(worldtime % 43200L); float f1 = ((float)timeOfDay +
     * par3) / 43200.0F - 0.25F;
     * 
     * if (f1 < 0.0F) { ++f1; }
     * 
     * if (f1 > 1.0F) { --f1; }
     * 
     * float f2 = f1; f1 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) +
     * 1.0D) / 2.0D); f1 = f2 + (f1 - f2) / 3.0F; return f1; }
     * 
     * public int getMoonPhase(long par1) { return (int)(par1 / 43200L) % 8; }
     */
}
