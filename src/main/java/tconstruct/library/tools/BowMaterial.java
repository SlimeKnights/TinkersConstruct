package tconstruct.library.tools;

public class BowMaterial
{
    public final int drawspeed;
    public final float flightSpeedMax;

    public BowMaterial(int drawspeed, float attack)
    {
        this.drawspeed = drawspeed;
        this.flightSpeedMax = attack;
    }

    @Deprecated
    public BowMaterial(int durability, int drawspeed, float attack)
    {
        this(drawspeed, attack);
    }

    @Deprecated
    public int durability = 0; // tic-tooltips compatibility
}
