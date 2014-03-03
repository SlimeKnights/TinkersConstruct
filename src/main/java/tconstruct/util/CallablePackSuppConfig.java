package tconstruct.util;

import cpw.mods.fml.common.ICrashCallable;

// Crash handler for when an unsupported mod is running alongside us
public class CallablePackSuppConfig implements ICrashCallable
{

    public CallablePackSuppConfig()
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
        return "Environment seems clean, however you are using a modpack; please report it to the pack author, not us!";
    }

}
