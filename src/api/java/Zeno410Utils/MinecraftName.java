
package Zeno410Utils;

import net.minecraft.util.StatCollector;

/**
 *
 * @author Zeno410
 */
public class MinecraftName {
    private final String unlocalized;
    public MinecraftName(String unlocalized) {
        this.unlocalized = unlocalized;
    }
    public String localized() {
        return StatCollector.translateToLocal(this.unlocalized() + ".name");
    }
    public String unlocalized() {return unlocalized;}
}
