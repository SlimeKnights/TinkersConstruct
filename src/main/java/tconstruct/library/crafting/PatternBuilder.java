package tconstruct.library.crafting;

/** How to build tool parts? With patterns! */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.event.PartBuilderEvent;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.util.IPattern;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;

public class PatternBuilder
{
    public static PatternBuilder instance = new PatternBuilder();
    //Map items to their parts with a hashmap
    public List<ItemKey> materials = new ArrayList<ItemKey>();
    public HashMap materialSets = new HashMap<String, MaterialSet>();

    //We could use IRecipe if it wasn't tied to InventoryCrafting
    public List<IPattern> toolPatterns = new ArrayList<IPattern>();

    /* Register methods */
    public void registerMaterial (ItemStack material, int value, String key)
    {
        materials.add(new ItemKey(material.getItem(), material.getItemDamage(), value, key));
    }

    public void registerMaterialSet (String key, ItemStack shard, ItemStack rod, int materialID)
    {
        materialSets.put(key, new MaterialSet(shard, rod, materialID));
        materials.add(new ItemKey(shard.getItem(), shard.getItemDamage(), 1, key));
    }

    public void registerFullMaterial (ItemStack material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
    {
        materials.add(new ItemKey(material.getItem(), material.getItemDamage(), value, key));
        materials.add(new ItemKey(shard.getItem(), shard.getItemDamage(), 1, key));
        materialSets.put(key, new MaterialSet(shard, rod, materialID));
    }

    public void addToolPattern (IPattern item)
    {
        toolPatterns.add(item);
    }

    /* Build tool parts from patterns, single pattern version */
    public ItemStack[] getToolPart (ItemStack material, ItemStack pattern)
    {
        if (material != null && pattern != null)
        {
            PartBuilderEvent.NormalPart event = new PartBuilderEvent.NormalPart(material, pattern, null);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.getResult() == Result.ALLOW)
            {
                return event.getResultStacks();
            }
            else if (event.getResult() == Result.DENY)
            {
                return new ItemStack[2];
            }

            ItemKey key = getItemKey(material);
            if (key != null)
            {
                MaterialSet mat = (MaterialSet) materialSets.get(key.key);
                ItemStack toolPart = getMatchingPattern(pattern, material, mat);

                if (toolPart != null)
                {
                    int patternValue = ((IPattern) pattern.getItem()).getPatternCost(pattern);
                    int totalMaterial = key.value * material.stackSize;

                    if (totalMaterial < patternValue) // Not enough material
                        return new ItemStack[2];

                    else if (patternValue == key.value) //Material only
                        return new ItemStack[] { toolPart, null };

                    else
                    {
                        if (patternValue % 2 == 1)
                        {
                            return new ItemStack[] { toolPart, mat.shard.copy() }; //Material + shard
                        }
                        else
                            return new ItemStack[] { toolPart, null };
                    }
                }
            }
        }
        return new ItemStack[2];
    }

    private ItemStack getValidPart (ItemKey key, ItemStack pattern, ItemStack material)
    {
        MaterialSet mat = (MaterialSet) materialSets.get(key.key);
        ItemStack toolPart = getMatchingPattern(pattern, material, mat);
        int totalMaterial = key.value * material.stackSize;

        if (toolPart != null)
        {
            int patternValue = ((IPattern) pattern.getItem()).getPatternCost(pattern);

            if (totalMaterial < patternValue) // Not enough material
                return null;

            else
                return toolPart;
        }
        return null;
    }

    /* Checks to see whether a given material is a valid material */
    public boolean validItemPart (ItemStack material, ItemStack pattern)
    {
        ItemKey key = getItemKey(material);
        PartBuilderEvent.BeginBuild event = new PartBuilderEvent.BeginBuild(material, pattern, null);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.getResult() == Result.ALLOW)
            return true;
        else if (event.getResult() == Result.DENY)
            return false;

        if (key != null)
            return true;

        return false;
    }

    /* Build tool parts from patterns, double pattern version */
    public ItemStack[] getToolPart (ItemStack material, ItemStack pattern, ItemStack otherPattern)
    {
        if (material != null && pattern != null)
        {
            PartBuilderEvent.NormalPart event = new PartBuilderEvent.NormalPart(material, pattern, otherPattern);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.getResult() == Result.ALLOW)
            {
                return event.getResultStacks();
            }
            else if (event.getResult() == Result.DENY)
            {
                return new ItemStack[2];
            }

            ItemKey key = getItemKey(material);
            if (key != null)
            {
                MaterialSet mat = (MaterialSet) materialSets.get(key.key);
                ItemStack toolPart = getMatchingPattern(pattern, material, mat);

                if (toolPart != null)
                {
                    int patternValue = ((IPattern) pattern.getItem()).getPatternCost(pattern);
                    int totalMaterial = key.value * material.stackSize;

                    if (totalMaterial < patternValue) // Not enough material
                        return new ItemStack[2];

                    else if (patternValue == key.value) //Material only
                        return new ItemStack[] { toolPart, null };

                    else
                        return new ItemStack[] { toolPart, getValidPart(key, otherPattern, mat.shard) };
                }
            }
        }
        return new ItemStack[2];
    }

    public int getPartID (ItemStack material)
    {
        if (material != null)
        {
            ItemKey key = getItemKey(material);
            if (key != null)
            {
                MaterialSet set = (MaterialSet) materialSets.get(key.key);
                return set.materialID;
            }
        }
        return Short.MAX_VALUE;
    }

    public int getPartValue (ItemStack material)
    {
        if (material != null)
        {
            ItemKey key = getItemKey(material);
            if (key != null)
                return key.value;

            for (CustomMaterial mat : TConstructRegistry.customMaterials)
            {
                if (material.isItemEqual(mat.input))
                    return mat.value;
            }
        }
        return 0;
    }

    public ItemKey getItemKey (ItemStack material)
    {
        Item mat = material.getItem();
        int damage = material.getItemDamage();
        for (ItemKey ik : materials)
        {
            if (mat == ik.item && (ik.damage == Short.MAX_VALUE || damage == ik.damage))
                return ik;
        }
        return null;
    }

    public ItemStack getMatchingPattern (ItemStack stack, ItemStack input, MaterialSet set)
    {
        Item item = stack.getItem();
        for (IPattern pattern : toolPatterns)
        {
            if (pattern == item)
                return pattern.getPatternOutput(stack, input, set);
        }
        return null;
    }

    public ItemStack getShardFromSet (String materialset)
    {
        MaterialSet set = (MaterialSet) materialSets.get(materialset);
        if (set != null)
            return set.shard.copy();
        return null;
    }

    public ItemStack getRodFromSet (String materialset)
    {
        MaterialSet set = (MaterialSet) materialSets.get(materialset);
        if (set != null)
            return set.rod.copy();
        return null;
    }

    //Small data classes. I would prefer the struct from C#, but we do what we can.
    public class ItemKey
    {
        public final Item item;
        public final int damage;
        public final int value;
        public final String key;

        public ItemKey(Item i, int d, int v, String s)
        {
            item = i;
            damage = d;
            value = v;
            key = s;
        }
    }

    public class MaterialSet
    {
        public final ItemStack shard;
        public final ItemStack rod;
        public final int materialID;

        public MaterialSet(ItemStack s, ItemStack r, int id)
        {
            shard = s;
            rod = r;
            materialID = id;
        }
    }

    //Helper Methods
    public void registerMaterial (Block material, int value, String key)
    {
        registerMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key);
    }

    public void registerMaterial (Item material, int value, String key)
    {
        registerMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key);
    }

    public void registerFullMaterial (Block material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
    {
        registerFullMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key, shard, rod, materialID);
    }

    public void registerFullMaterial (Item material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
    {
        registerFullMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key, shard, rod, materialID);
    }

    /*public void registerFullMaterial (Block material, int value, String key, int materialID)
    {
    	registerFullMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key, new ItemStack(TContent.toolShard, 1, materialID), new ItemStack(TContent.toolRod, 1, materialID), materialID);
    }

    public void registerFullMaterial (Item material, int value, String key, int materialID)
    {
    	registerFullMaterial(new ItemStack(material, 1, Short.MAX_VALUE), value, key, new ItemStack(TContent.toolShard, 1, materialID), new ItemStack(TContent.toolRod, 1, materialID), materialID);
    }*/
}