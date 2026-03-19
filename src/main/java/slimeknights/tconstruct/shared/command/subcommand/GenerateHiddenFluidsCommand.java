package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagManager;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.command.GeneratePackHelper;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.TinkerTags.Fluids;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Command generating the relevant tag to hide fluids related to unused materials. */
public class GenerateHiddenFluidsCommand {
  private static final DynamicCommandExceptionType ERROR_WRITING_TAG = new DynamicCommandExceptionType(tag -> Component.translatable("command.mantle.modify_tag.write_error", "fluid", tag));

  /**
   * Registers this sub command with the root command
   * @param subCommand Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS)).executes(GenerateHiddenFluidsCommand::run);
  }

  /** Runs the command */
  private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    // setup the pack
    CommandSourceStack source = context.getSource();
    Path pack = GeneratePackHelper.getDatapackPath(source.getServer());
    GeneratePackHelper.saveMcmeta(pack);

    // fetch existing tag, if it exists
    ResourceLocation tag = Fluids.HIDDEN_IN_RECIPE_VIEWERS.location();
    Path tagPath = pack.resolve(PackType.SERVER_DATA.getDirectory() + '/' + tag.getNamespace() + '/' + TagManager.getTagDir(Registries.FLUID) + '/' + tag.getPath() + ".json");

    // load in existing tag from the path, not using resource managers as we are just modifying locally
    List<TagEntry> add = new ArrayList<>();
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      if (!compat.isPresent()) {
        FluidObject<?> fluid = compat.getFluid();
        if (fluid instanceof FlowingFluidObject<?> flowing) {
          add.add(TagEntry.tag(flowing.getLocalTag().location()));
        } else {
          add.add(TagEntry.element(compat.getFluid().getId()));
        }
      }
    }
    // save the new tag
    saveTag(tagPath, tag, new TagFile(add, false, List.of()));

    // success
    source.sendSuccess(() -> Component.translatable(
      "command.tconstruct.generate_hidden_fluids",
      add.size(),
      GeneratePackHelper.getPathComponent(Component.literal(tag.toString()), tagPath.toString()),
      GeneratePackHelper.getOutputComponent(pack)), true);
    return add.size();
  }

  /** Saves the passed tag */
  private static void saveTag(Path path, ResourceLocation tag, TagFile contents) throws CommandSyntaxException {
    try {
      Files.createDirectories(path.getParent());
      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
        writer.write(JsonHelper.DEFAULT_GSON.toJson(JsonHelper.serialize(TagFile.CODEC, contents)));
      }
    } catch (IOException ex) {
      Mantle.logger.error("Couldn't save fluid tag {} to {}", tag, path, ex);
      throw ERROR_WRITING_TAG.create(tag);
    }
  }
}
