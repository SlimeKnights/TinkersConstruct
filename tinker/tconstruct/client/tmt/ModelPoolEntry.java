package tinker.tconstruct.client.tmt;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ModelPoolEntry
{
	public File checkValidPath(String path)
	{
		File file = null;
		
		for(int index = 0; index < fileExtensions.length && (file == null || !file.exists()); index++)
		{
			String absPath = path;
			
			if(!path.endsWith("." + fileExtensions[index]))
				absPath+= "." + fileExtensions[index];
			
			file = new File(absPath);
		}
		if(file == null || !file.exists())
			return null;
		return file;
	}
	
	public abstract void getModel(File file);
	
    /**
     * Sets the current transformation group. The transformation group is used
     * to allow for vertex transformation. If a transformation group does not exist,
     * a new one will be created.
     * @param groupName the name of the transformation group you want to switch to
     */
    protected void setGroup(String groupName)
    {
    	setGroup(groupName, new Bone(0, 0, 0, 0), 1D);
    }
    
    /**
     * Sets the current transformation group. The transformation group is used
     * to allow for vertex transformation. If a transformation group does not exist,
     * a new one will be created.
     * @param groupName the name of the transformation group you want to switch to
     * @param bone the Bone this transformation group is attached to
     * @param weight the weight of the transformation group
     */
    protected void setGroup(String groupName, Bone bone, double weight)
    {
    	if(groups.size() == 0 || !groups.containsKey(groupName))
    		groups.put(groupName, new TransformGroupBone(bone, weight));
    	group = groups.get(groupName);
    }
    
    /**
     * Sets the current texture group, which is used to switch the
     * textures on a per-model base. Do note that any model that is
     * rendered afterwards will use the same texture. To counter it,
     * set a default texture, either at initialization or before
     * rendering.
     * @param groupName The name of the texture group. If the texture
     * group doesn't exist, it creates a new group automatically.
     */
    protected void setTextureGroup(String groupName)
    {
    	if(textures.size() == 0 || !textures.containsKey(groupName))
    	{
    		textures.put(groupName, new TextureGroup());
    	}
    	texture = textures.get(groupName);
    }
    
    protected void applyGroups(Map<String, TransformGroup> groupsMap, Map<String, TextureGroup> texturesMap)
    {
    	Set<String> groupsCol = groups.keySet();
    	Collection<String> texturesCol = textures.keySet();
    	
    	Iterator<String> groupsItr = groupsCol.iterator();
    	Iterator<String> texturesItr = texturesCol.iterator();
    	
    	while(groupsItr.hasNext())
    	{
    		int nameIdx = 0;
    		String groupKey = groupsItr.next();
    		String currentGroup = name + "_" + nameIdx + ":" + groupKey;
    		while(groupsMap.size() > 0 && groupsMap.containsKey(currentGroup))
    		{
    			nameIdx++;
    			currentGroup = name + "_" + nameIdx + ":" + groupKey;
    		}
    		groupsMap.put(currentGroup, groups.get(groupKey));
    	}
    	
    	while(texturesItr.hasNext())
    	{
    		int nameIdx = 0;
    		String groupKey = texturesItr.next();
    		String currentGroup = name + "_" + nameIdx + ":" + groupKey;
    		while(groupsMap.size() > 0 && texturesMap.containsKey(currentGroup))
    		{
    			nameIdx++;
    			currentGroup = name + "_" + nameIdx + ":" + groupKey;
    		}
    		texturesMap.put(currentGroup, textures.get(groupKey));
    	}
    }
    	
    public String name;
	public PositionTransformVertex[] vertices;
	public TexturedPolygon[] faces;
	public Map<String, TransformGroupBone> groups;
	public Map<String, TextureGroup> textures;
	protected TransformGroupBone group;
	protected TextureGroup texture;
	protected String[] fileExtensions;
}
