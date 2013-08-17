package tconstruct.client.cape;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class TinkersConstruct_Utility {

	public static boolean getFullInvisibility(EntityPlayer aPlayer) {
		try {
			if (aPlayer.isInvisible()) {

			}
		} catch (Throwable e) {

		}
		return false;
	}

	public static boolean getPotion(EntityLivingBase aPlayer, int aPotionIndex) {
		try {
			Field tPotionHashmap = null;

			Field[] var3 = EntityLiving.class.getDeclaredFields();
			int var4 = var3.length;

			for (int var5 = 0; var5 < var4; var5++) {
				Field var6 = var3[var5];
				if (var6.getType() == HashMap.class) {
					tPotionHashmap = var6;
					tPotionHashmap.setAccessible(true);
					break;
				}
			}

			if (tPotionHashmap != null)
				return ((HashMap) tPotionHashmap.get(aPlayer)).get(Integer
						.valueOf(aPotionIndex)) != null;
		} catch (Throwable e) {
			
		}
		return false;
	}

}
