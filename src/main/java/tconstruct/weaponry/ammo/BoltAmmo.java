package tconstruct.weaponry.ammo;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.FletchlingLeafMaterial;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.library.weaponry.AmmoItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.ToolBuilder;

import java.util.List;

public class BoltAmmo extends AmmoItem {
    public BoltAmmo() {
        super(0, "Bolts");
    }

    @Override
    public String getIconSuffix(int partType) {
        switch (partType)
        {
            case 0:
                return "_bolt_head";
            case 1:
                return ""; // Doesn't break
            case 2:
                return "_bolt_shaft";
            case 3:
                return "_bolt_fletching";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix() {
        return "_bolt_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "bolt";
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public void registerAlternatePartPaths (int index, String[] location)
    {
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem() {
        return TinkerWeaponry.partBolt;
    }

    @Override
    public Item getHandleItem() {
        return TinkerWeaponry.partBolt;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerWeaponry.fletching;
    }

    @Override
    public String[] getTraits() {
        return new String[] {"ammo"};
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        // dual material head: we use wooden shafts
        ItemStack headStack = DualMaterialToolPart.createDualMaterial(getHeadItem(), 0, id); // wooden shaft, material head
        ItemStack handleStack = new ItemStack(getAccessoryItem(), 1, 0); // feather Fletchling
        //ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, null, null, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    protected int getDefaultColor(int renderPass, int materialID) {
        if(renderPass != 2)
            return super.getDefaultColor(renderPass, materialID);

        CustomMaterial mat = TConstructRegistry.getCustomMaterial(materialID, FletchingMaterial.class);
        if(mat == null)
            TConstructRegistry.getCustomMaterial(materialID, FletchlingLeafMaterial.class);
        if(mat == null)
            return 0xffffff;

        return mat.color;
    }
}
