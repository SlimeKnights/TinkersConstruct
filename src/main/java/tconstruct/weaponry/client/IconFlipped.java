package boni.tinkersweaponry.client;

import net.minecraft.util.IIcon;

// required to fix Mojansg bugs...
public class IconFlipped extends net.minecraft.client.renderer.IconFlipped {
    private final IIcon baseIcon;
    private final boolean flipV;

    public IconFlipped(IIcon p_i1560_1_, boolean p_i1560_2_, boolean p_i1560_3_) {
        super(p_i1560_1_, p_i1560_2_, p_i1560_3_);

        this.baseIcon = p_i1560_1_;
        this.flipV = p_i1560_3_;
    }

    // has a faulty implementation in original class
    public float getMinV()
    {
        return this.flipV ? this.baseIcon.getMaxV() : this.baseIcon.getMinV();
    }
}
