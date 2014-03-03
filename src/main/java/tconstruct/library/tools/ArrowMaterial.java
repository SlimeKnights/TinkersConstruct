package tconstruct.library.tools;

public class ArrowMaterial
{
    public final float mass;
    public final float breakChance;
    public final float accuracy;

    public ArrowMaterial(float weight, float breakChance, float accuracy)
    {
        this.mass = weight;
        this.breakChance = breakChance;
        this.accuracy = accuracy;
    }
}
