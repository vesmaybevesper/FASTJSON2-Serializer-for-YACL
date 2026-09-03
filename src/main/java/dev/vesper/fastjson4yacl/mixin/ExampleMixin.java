package dev.vesper.fastjson4yacl.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public class ExampleMixin {
	// This mixin is load bearing lol, if it gets deleted (Lex)Forge won't build (it wasn't in mixin, so I have no idea why)
}
