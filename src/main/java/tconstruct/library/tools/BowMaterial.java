package tconstruct.library.tools;

public class BowMaterial
{
    public final int durability;
    public final int drawspeed;
    public final float flightSpeedMax;

    public BowMaterial(int durability, int drawspeed, float attack)
    {
        this.durability = durability;
        this.drawspeed = drawspeed;
        this.flightSpeedMax = attack;
    }
}
