package tconstruct.library.client;

public class FluidRenderProperties
{

    //Constant defaults
    public static final FluidRenderProperties DEFAULT_TABLE = new FluidRenderProperties(Applications.TABLE);
    public static final FluidRenderProperties DEFAULT_BASIN = new FluidRenderProperties(Applications.BASIN);

    public float minHeight, maxHeight, minX, maxX, minZ, maxZ;

    public FluidRenderProperties(float minHeight, float maxHeight, float minX, float maxX, float minZ, float maxZ)
    {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public FluidRenderProperties(float minHeight, float maxHeight, Applications defaults)
    {
        this(minHeight, maxHeight, defaults.minX, defaults.maxX, defaults.minZ, defaults.maxZ);
    }

    public FluidRenderProperties(float minHeight, float maxHeight, float minX, float maxX, Applications defaults)
    {
        this(minHeight, maxHeight, minX, maxX, defaults.minZ, defaults.maxZ);
    }

    public FluidRenderProperties(Applications defaults, float minX, float maxX)
    {
        this(defaults.minHeight, defaults.maxHeight, minX, maxX, defaults.minZ, defaults.maxZ);
    }

    public FluidRenderProperties(Applications defaults, float minX, float maxX, float minZ, float maxZ)
    {
        this(defaults.minHeight, defaults.maxHeight, minX, maxX, minZ, maxZ);
    }

    public FluidRenderProperties(Applications defaults)
    {
        this(defaults.minHeight, defaults.maxHeight, defaults.minX, defaults.maxX, defaults.minZ, defaults.maxZ);
    }

    public static enum Applications
    {
        TABLE(0.9375F, 1F, 0.0625F, 0.9375F, 0.062F, 0.9375F), BASIN(0.25F, 0.95F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);

        public float minHeight, maxHeight, minX, maxX, minZ, maxZ;

        Applications(float minHeight, float maxHeight, float minX, float maxX, float minZ, float maxZ)
        {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }
}