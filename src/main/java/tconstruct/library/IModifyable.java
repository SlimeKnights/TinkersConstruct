package tconstruct.library;

public interface IModifyable
{
    /**
     * @return The base tag to modify. Ex: InfiTool
     */
    public String getBaseTagName();
    
    public String getModifyType();
    
    public String[] getTraits();

}

