package tconstruct.plugins;

/**
 * Interface for TiC compat plugins.
 *
 * Do not include mod API usage directly in this file except for IMC! This must be constructable even
 * when the target mod isn't available due to Java not allowing static abstracts.
 */
public interface ICompatPlugin
{

    // Mod ID the plugin handles
    public abstract String getModId ();

    // Called during TCon PreInit
    public abstract void preInit ();

    // Called during TCon Init
    public abstract void init ();

    // Called during TCon PostInit
    public abstract void postInit ();

}
