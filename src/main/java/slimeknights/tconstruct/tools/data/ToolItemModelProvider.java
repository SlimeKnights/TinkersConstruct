package slimeknights.tconstruct.tools.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.AbstractToolItemModelProvider;
import slimeknights.tconstruct.tools.TinkerTools;

import java.io.IOException;

import static slimeknights.tconstruct.TConstruct.getResource;

/** Provider for tool models, mostly used for duplicating displays */
public class ToolItemModelProvider extends AbstractToolItemModelProvider {
  public ToolItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, existingFileHelper, TConstruct.MOD_ID);
  }

  @Override
  protected void addModels() throws IOException {
    JsonObject toolBlocking = readJson(getResource("base/tool_blocking"));
    JsonObject shieldBlocking = readJson(getResource("base/shield_blocking"));

    // blocking //
    // pickaxe
    tool(TinkerTools.pickaxe, toolBlocking, "head");
    tool(TinkerTools.sledgeHammer, toolBlocking, "head", "front", "back");
    tool(TinkerTools.veinHammer, toolBlocking, "head", "front");
    // shovel
    tool(TinkerTools.pickadze, toolBlocking, "pick");
    tool(TinkerTools.mattock, toolBlocking, "axe", "pick");
    tool(TinkerTools.excavator, toolBlocking, "head");
    // axe
    tool(TinkerTools.handAxe, toolBlocking, "head");
    tool(TinkerTools.broadAxe, toolBlocking, "head", "back");
    // sword
    tool(TinkerTools.dagger, toolBlocking, "head");
    tool(TinkerTools.sword, toolBlocking, "head");
    tool(TinkerTools.cleaver, toolBlocking, "head", "shield");
    // scythe
    tool(TinkerTools.kama, toolBlocking, "head");
    tool(TinkerTools.scythe, toolBlocking, "head");
    // armor
    // travelers goggles use a base texture for the glass
    armor("travelers", TinkerTools.travelersGear, new Type[] {Type.HELMET},"base", "cuirass", "metal");
    armor("travelers", TinkerTools.travelersGear, new Type[] {Type.CHESTPLATE, Type.LEGGINGS, Type.BOOTS}, "cuirass", "metal");
    armor("plate", TinkerTools.plateArmor, "plating", "maille");
    armor("slime", TinkerTools.slimesuit, "tool");
    // shield
    shield("travelers", TinkerTools.travelersShield, shieldBlocking, "cuirass", "wood");
    shield("plate", TinkerTools.plateShield, readJson(getResource("base/shield_large_blocking")), "plating", "core");
    // misc
    tool(TinkerTools.flintAndBrick, shieldBlocking, "tool");
    // bow
    bow(TinkerTools.longbow, toolBlocking, new LongbowAmmo(new Vec2[] {
      new Vec2(-3, -4), new Vec2(-2, -3), new Vec2(-1, -2)
    }, new Vec2[] {
      new Vec2(-2, -2), new Vec2(0, 0), new Vec2(1, 1)
    }, true, true), "limb_bottom", "limb_top", "bowstring");
    bow(TinkerTools.crossbow, toolBlocking, new CrossbowAmmo(new Vec2(-1, -1), true, false), "bowstring");
    String[] rodParts = { "string", "hook" };
    fishingRod(TinkerTools.fishingRod, readJson(getResource("tool/fishing_rod/blocking_display")), rodParts, rodParts);
    tool(TinkerTools.javelin, toolBlocking, "head");
    // staff
    staff(TinkerTools.skyStaff, toolBlocking);
    staff(TinkerTools.earthStaff, toolBlocking);
    staff(TinkerTools.ichorStaff, toolBlocking);
    staff(TinkerTools.enderStaff, toolBlocking);
    // ancient
    charged(TinkerTools.meltingPan, shieldBlocking, "head");
    bow(TinkerTools.warPick, toolBlocking, new CrossbowAmmo(new Vec2(1, -1), false, true), "bowstring");
    tool(TinkerTools.battlesign, null, "head");
    pulling(TinkerTools.swasher, readJson(getResource("tool/swasher/blocking_display")), AmmoType.NONE, "blade", 2, "barrel");
    tool(TinkerTools.minotaurAxe, toolBlocking, "front");
  }

  @Override
  public String getName() {
    return "Tinkers Construct Tool Item Model Provider";
  }
}
