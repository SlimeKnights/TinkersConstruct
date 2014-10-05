package tconstruct.library.tools;

public class ArrowMaterial
{
    public final float mass;
    public final float breakChance;

    public ArrowMaterial(float weight, float breakChance)
    {
        this.mass = weight;
        this.breakChance = breakChance;
    }

    @Deprecated
    public ArrowMaterial(float weight, float breakChance, float accuraccy)
    {
        this(weight, breakChance);
    }
}
