package net.specialattack.forge.core.config;

import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * Class used for configurations
 *
 * @author heldplayer
 */
public class Config<T> extends ConfigCategory<T> {

    protected Configuration config;

    /**
     * Creates a new Config instance from given file
     *
     * @param file
     *         The configuration file, usually from
     *         {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent#getSuggestedConfigurationFile()}
     */
    public Config(File file) {
        super("", "root", null);

        this.config = new Configuration(file);
        super.config = this;
    }

    /**
     * Saves the configuration, does not take in account if the entries have
     * changed or not
     */
    public void save() {
        this.config.save();
    }

    /**
     * Saves the configuration if any key has been changed
     */
    public void saveOnChange() {
        for (ConfigCategory<?> category : this.children) {
            if (category.isChanged()) {
                this.config.save();

                break;
            }
        }
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void addValue(ConfigValue<?> value) {
    }

    /**
     * Loads the configuration
     */
    @Override
    public void load() {
        for (ConfigCategory<?> category : this.children) {
            category.load();
        }
    }

    @SuppressWarnings("rawtypes")
    public List<IConfigElement> getConfigElements() {
        return this.getChildElements();
    }

}
