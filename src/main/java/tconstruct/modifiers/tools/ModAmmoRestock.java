package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.library.weaponry.IAmmo;
import tconstruct.weaponry.weapons.Shuriken;

import java.util.HashSet;
import java.util.Set;

public class ModAmmoRestock extends ItemModifier {
    public ModAmmoRestock()
    {
        super(new ItemStack[0], 0, "");
    }

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        return canModify(tool, input);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if(!(tool.getItem() instanceof AmmoItem))
            return false;

        IAmmo ammo = (IAmmo)tool.getItem();

        // full?
        if(ammo.getAmmoCount(tool) >= ammo.getMaxAmmo(tool))
            return false;

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        // correct material?

        Set<Integer> materials = new HashSet<Integer>();
        materials.add(tags.getInteger("Head"));
        // shuriken allow all their components
        if(tool.getItem() instanceof Shuriken) {
            materials.add(tags.getInteger("Handle"));
            materials.add(tags.getInteger("Accessory"));
            materials.add(tags.getInteger("Extra"));
        }

        boolean areInputsValid = true;
        for (ItemStack curInput : input)
        {
            if (curInput != null && !materials.contains(PatternBuilder.instance.getPartID(curInput)))
            {
                areInputsValid = false;
                break;
            }
        }

        return areInputsValid;
    }

    private int calculateIncrease (ItemStack tool, int materialValue, int itemsUsed)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        float dur = tags.getInteger("BaseDurability") * ((AmmoItem)tool.getItem()).getAmmoModifier();
        float increase = (5 * itemsUsed + (dur * 0.4f * materialValue/2f));

        int modifiers = tags.getInteger("Modifiers");
        float mods = 1.0f;
        if (modifiers == 2)
            mods = 0.9f;
        else if (modifiers == 1)
            mods = 0.8f;
        else if (modifiers == 0)
            mods = 0.7f;

        increase *= mods;

        int repair = tags.getInteger("RepairCount");
        float repairCount = (100 - repair) / 100f;
        if (repairCount < 0.5f)
            repairCount = 0.5f;
        increase *= repairCount;
        increase /= ((ToolCore) tool.getItem()).getRepairCost();

        return (int)increase;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int itemsUsed = 0;
        int materialValue = 0;
        for (ItemStack curInput : input)
        {
            if (curInput != null)
            {
                materialValue += PatternBuilder.instance.getPartValue(curInput);
                itemsUsed++;
            }
        }

        int increase = calculateIncrease(tool, materialValue, itemsUsed);
        int repair = tags.getInteger("RepairCount");
        repair += itemsUsed;
        tags.setInteger("RepairCount", repair);

        ((IAmmo)tool.getItem()).addAmmo(increase, tool);
    }

    @Override
    public void addMatchingEffect (ItemStack tool)
    {
    }

    public boolean validType (IModifyable input)
    {
        return input.getModifyType().equals("Tool") || input.getModifyType().equals("Armor");
    }
}
