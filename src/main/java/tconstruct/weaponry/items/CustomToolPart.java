package tconstruct.weaponry.items;

import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.DynamicToolPart;

public class CustomToolPart extends WeaponryToolPart {
    private final Class<? extends CustomMaterial> customMat;

    public CustomToolPart(String texture, String name, Class<? extends CustomMaterial> customMatClass) {
        super(texture, name);

        this.customMat = customMatClass;
    }

    @Override
    public int getMaterialID (ItemStack stack)
    {
        if(TConstructRegistry.getCustomMaterial(stack.getItemDamage(), customMat) != null)
            return stack.getItemDamage();

        return -1;
    }
}
