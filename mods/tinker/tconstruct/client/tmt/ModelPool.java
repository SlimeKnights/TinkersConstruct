package mods.tinker.tconstruct.client.tmt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

public class ModelPool
{
    public static ModelPoolEntry addFile (String file, Class modelClass, Map<String, TransformGroup> group, Map<String, TextureGroup> textureGroup)
    {
        ModelPoolEntry entry = null;
        if (modelMap.containsKey(file))
        {
            entry = modelMap.get(file);
            entry.applyGroups(group, textureGroup);
            return entry;
        }
        try
        {
            entry = (ModelPoolEntry) modelClass.newInstance();
        }
        catch (Exception e)
        {
            System.out.println("A new " + entry.getClass().getName() + " could not be initialized.");
            System.out.println(e.getMessage());
            return null;
        }
        File modelFile = null;
        for (int i = 0; i < resourceDir.length && (modelFile == null || !modelFile.exists()); i++)
        {
            String absPath = new File(Minecraft.getMinecraft().mcDataDir, (resourceDir[i])).getAbsolutePath();
            if (!absPath.endsWith("/") || !absPath.endsWith("\\"))
                absPath += "/";
            modelFile = entry.checkValidPath(absPath + file);
        }
        if (modelFile == null || !modelFile.exists())
        {
            System.out.println("The model with the name " + file + " does not exist.");
            return null;
        }
        entry.groups = new HashMap<String, TransformGroupBone>();
        entry.textures = new HashMap<String, TextureGroup>();
        entry.name = file;
        entry.setGroup("0");
        entry.setTextureGroup("0");
        entry.getModel(modelFile);
        entry.applyGroups(group, textureGroup);
        modelMap.put(file, entry);
        return entry;
    }

    private static Map<String, ModelPoolEntry> modelMap = new HashMap<String, ModelPoolEntry>();
    private static String[] resourceDir = new String[] { "minecraft/resources/models/", "minecraft/resources/mod/models/" };
    public static final Class OBJ = ModelPoolObjEntry.class;
}
