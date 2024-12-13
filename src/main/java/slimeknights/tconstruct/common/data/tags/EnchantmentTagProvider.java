package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class EnchantmentTagProvider extends TagsProvider<Enchantment> {
  public EnchantmentTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
    super(generator, Registry.ENCHANTMENT, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    // upgrade
    modifierTag(ModifierIds.experienced, "cyclic:experience_boost", "ensorcellation:exp_boost");
    modifierTag(ModifierIds.killager, "ensorcellation:damage_illager");
    modifierTag(TinkerModifiers.magnetic.getId(), "cyclic:magnet");
    modifierTag(TinkerModifiers.necrotic.getId(), "cyclic:life_leech", "ensorcellation:leech");
    modifierTag(TinkerModifiers.severing.getId(), "cyclic:beheading", "ensorcellation:vorpal");
    modifierTag(ModifierIds.stepUp, "cyclic:step");
    modifierTag(ModifierIds.soulbound, "ensorcellation:soulbound");
    modifierTag(ModifierIds.trueshot, "ensorcellation:trueshot");

    // defense
    modifierTag(ModifierIds.knockbackResistance, "cyclic:steady");
    modifierTag(ModifierIds.magicProtection, "ensorcellation:magic_protection");
    modifierTag(ModifierIds.revitalizing, "ensorcellation:vitality");

    // ability
    modifierTag(TinkerModifiers.autosmelt.getId(), "cyclic:auto_smelt", "ensorcellation:smelting");
    modifierTag(TinkerModifiers.doubleJump.getId(), "cyclic:launch", "walljump:doublejump");
    modifierTag(TinkerModifiers.expanded.getId(), "cyclic:excavate", "ensorcellation:excavating", "ensorcellation:furrowing");
    modifierTag(ModifierIds.luck, "ensorcellation:hunter");
    modifierTag(TinkerModifiers.multishot.getId(), "cyclic:multishot", "ensorcellation:volley");
    modifierTag(ModifierIds.reach, "cyclic:reach", "ensorcellation:reach");
    modifierTag(ModifierIds.tilling, "ensorcellation:tilling");
    modifierTag(TinkerModifiers.reflecting.getId(), "parry:rebound");
  }

  /** Creates a builder for a tag for the given modifier */
  private void modifierTag(ModifierId modifier, String... ids) {
    TagsProvider.TagAppender<Enchantment> appender = tag(TagKey.create(Registry.ENCHANTMENT_REGISTRY, TConstruct.getResource("modifier_like/" + modifier.getPath())));
    for (String id : ids) {
      appender.addOptional(new ResourceLocation(id));
    }
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Block Enchantment Tags";
  }
}
