package tconstruct.util;

import cpw.mods.fml.common.ICrashCallable;
import tconstruct.util.config.PHConstruct;

import java.util.List;

// Crash handler for when an unsupported mod is running alongside us
public class CallableSuppConfig implements ICrashCallable
{

    public CallableSuppConfig()
    {
    }

    @Override
    public String getLabel ()
    {
        return "TConstruct Environment";
    }

    @Override
    public String call () throws Exception
    {
        // Check for modpack flag, if so use the pack callable
        if (PHConstruct.isModpack)
            return new CallablePackSuppConfig().call();

        return "Sane and ready for action. Bugs may be reported.";
    }

}
