package tconstruct.preloader;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;
import java.lang.reflect.Method;

public class AccessTransformers extends AccessTransformer {

    public AccessTransformers() throws IOException {
        super();

        readMapFile("TConstruct_AT.cfg");
    }

    private void readMapFile(String name) {
        TConstructLoaderContainer.logger.info("[AT] Loading AT file: " + name);
        try {
            Method me = AccessTransformer.class.getDeclaredMethod("readMapFile", new Class[]{String.class});
            me.setAccessible(true);
            me.invoke(this, name);
        } catch (Exception ex) {
            TConstructLoaderContainer.logger.info("[AT] Unknown failure occured: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
