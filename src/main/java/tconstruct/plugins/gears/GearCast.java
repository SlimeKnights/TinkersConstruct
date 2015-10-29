package tconstruct.plugins.gears;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;

public class GearCast extends Item implements IPattern {
    public GearCast() {
        setTextureName("tinker:materials/gear_cast");
        setUnlocalizedName("tconstruct.GearPattern");
    }

    @Override
    public int getPatternCost(ItemStack pattern) {
        return 8;
    }

    @Override
    public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, PatternBuilder.MaterialSet set) {
        return TConstructRegistry.getPartMapping(this, pattern.getMetadata(), set.materialID);
    }
}
