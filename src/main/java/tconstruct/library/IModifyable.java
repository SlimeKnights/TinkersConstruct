package tconstruct.library;

public interface IModifyable
{
    /**
     * @return The base tag to modify. Ex: InfiTool
     */
    public String getBaseTag();
    
    public String getModifyType();
    
    public String[] getTraits();
}
