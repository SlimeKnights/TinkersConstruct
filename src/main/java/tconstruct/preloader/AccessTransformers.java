package tconstruct.preloader;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class AccessTransformers extends AccessTransformer
{
    public AccessTransformers() throws IOException
    {
        super("TConstruct_at.cfg");
    }
}