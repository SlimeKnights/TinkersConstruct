package tconstruct.library.modifier;

public interface IModifyable
{
    /**
     * @return The base tag to modify. Ex: InfiTool
     */
    public String getBaseTagName ();

    public String getModifyType ();

    public String[] getTraits ();
}
