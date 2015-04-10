package net.specialattack.forge.core.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({ "net.specialattack.forge.core.asm" })
public class SpACorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    protected static boolean debug = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.specialattack.forge.core.asm.SpACoreModTransformer", //
                //"net.specialattack.forge.core.asm.SpACoreDebugTransformer", //
                "net.specialattack.forge.core.asm.SpACoreLoggerTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "net.specialattack.forge.core.asm.SpACoreModContainer";
    }

    @Override
    public String getSetupClass() {
        return "net.specialattack.forge.core.asm.SpACorePlugin";
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // Enable when NEI bugs
        /*
        if (FMLLaunchHandler.side().isClient() && !((Boolean) data.get("runtimeDeobfuscationEnabled"))) {
            debug = true;
        }
        //*/
    }

    @Override
    public String getAccessTransformerClass() {
        return "net.specialattack.forge.core.asm.SpACoreAccessTransformer";
    }

    @Override
    public Void call() {
        return null;
    }

}
