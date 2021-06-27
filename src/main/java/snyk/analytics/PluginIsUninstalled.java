//
//  PluginIsUninstalled.java
//  This file is auto-generated by Iteratively. Run `itly pull jetbrains` to update.
//

package snyk.analytics;

import java.util.HashMap;

import ly.iterative.itly.Event;

public class PluginIsUninstalled extends Event {
    private static final String NAME = "Plugin Is Uninstalled";
    private static final String ID = "5936cb0e-2639-4b76-baea-f0c086b860b0";
    private static final String VERSION = "4.0.0";

    public enum Ide {
        VISUAL_STUDIO_CODE("Visual Studio Code"), VISUAL_STUDIO("Visual Studio"), ECLIPSE("Eclipse"), JETBRAINS("JetBrains");

        private String ide;

        public String getIde()
        {
            return this.ide;
        }

        Ide(String ide)
        {
            this.ide = ide;
        }
    }

    private PluginIsUninstalled(Builder builder) {
        super(NAME, builder.properties, ID, VERSION);
    }

    private PluginIsUninstalled(PluginIsUninstalled clone) {
        super(NAME, new HashMap<>(clone.getProperties()), ID, VERSION);
    }

    public PluginIsUninstalled clone() {
        return new PluginIsUninstalled(this);
    }

    public static IIde builder() { return new Builder(); }

    // Inner Builder class with required properties
    public static class Builder implements IIde, IBuild {
        private final HashMap<String, Object> properties = new HashMap<String, Object>();

        private Builder() {
            this.properties.put("itly", true);
        }

        /**
         * Ide family.
         * <p>
         * Must be followed by by additional optional properties or build() method
         */
        public Builder ide(Ide ide) {
            this.properties.put("ide", ide.getIde());
            return this;
        }

        public PluginIsUninstalled build() {
            return new PluginIsUninstalled(this);
        }
    }

    // Required property interfaces
    public interface IIde {
        Builder ide(Ide ide);
    }

    /** Build interface with optional properties */
    public interface IBuild {
        PluginIsUninstalled build();
    }
}
