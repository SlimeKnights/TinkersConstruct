package tconstruct.util;

import cpw.mods.fml.common.ICrashCallable;

import java.util.List;

// Crash handler for when an unsupported mod is running alongside us
public class CallablePackUnsuppConfig implements ICrashCallable
{

    private List<String> modIds;

    public CallablePackUnsuppConfig(List<String> modIds)
    {
        this.modIds = modIds;
    }

    @Override
    public String getLabel ()
    {
        return "TConstruct Environment";
    }

    @Override
    public String call () throws Exception
    {
        String str = "Environment is NOT clean! THIS CRASH MUST NOT BE REPORTED TO A MOD AUTHOR; however, please report it to your modpack creator(s).\n" +
                     "Unsupported mods: ";
        Boolean firstEntry = true;
        for (String id : modIds)
        {
            str = str + (firstEntry ? id : ", " + id);
            firstEntry = false;
        }

        return str;
    }

}
