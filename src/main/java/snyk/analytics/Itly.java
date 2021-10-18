//
//  Itly.java
//  This file is auto-generated by Iteratively. Run `itly pull jetbrains` to update.
//
//  Works with versions 1.2+ of ly.iterative.itly:sdk and plugins
//  https://search.maven.org/search?q=itly
//

package snyk.analytics;

import java.util.*;
import java.lang.*;

import ly.iterative.itly.Environment;
import ly.iterative.itly.Event;
import ly.iterative.itly.Options;
import ly.iterative.itly.Plugin;
import ly.iterative.itly.SchemaValidatorPlugin;
import ly.iterative.itly.iteratively.IterativelyPlugin;


public class Itly {
    private static volatile Itly singleton = null;

    private static final ly.iterative.itly.jvm.Itly itly = new ly.iterative.itly.jvm.Itly();

    private Itly() {
    }


    /**
     * Initializes Itly
     */
    public void load(
        DestinationsOptions destinations
    ) {
        load(destinations, new Options());
    }

    /**
     * Initializes Itly
     */
    public void load(
        DestinationsOptions destinations,
        Options options
    ) {
        Map<String, String> schemas = new HashMap<>();
        schemas.put("group", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/group\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Group\",\"description\":\"\",\"type\":\"object\",\"properties\":{\"groupId\":{\"description\":\"ID that is used in conjunction with a groupType to specify an Org or a Group association: {groupId: 1234, groupType: \\\"org\\\"}\",\"type\":\"string\"},\"name\":{\"description\":\"The display name of the org\",\"type\":\"string\"},\"internalName\":{\"description\":\"The internal name (org.name) of the org\",\"type\":\"string\"},\"groupType\":{\"description\":\"Key that is used to specify the name of the Segment Group that a groupId is being set for.\",\"enum\":[\"org\",\"group\",\"account\"]},\"plan\":{\"description\":\"The plan of the org\",\"type\":\"string\"},\"groupName\":{\"description\":\"The name of the group associated with the org\",\"type\":\"string\"},\"projectTypes\":{\"description\":\"The types of projects in the org\",\"type\":\"array\",\"items\":{\"type\":\"string\"},\"uniqueItems\":true}},\"additionalProperties\":false,\"required\":[]}");
        schemas.put("identify", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/identify\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Identify\",\"description\":\"\",\"type\":\"object\",\"properties\":{\"isNonUser\":{\"description\":\"Applies to non-user identities, such as Snyk Orgs\",\"type\":\"boolean\"},\"is_snyk\":{\"description\":\"Whether or not the user belongs to the Snyk org (determined by the email address ending with @snyk.io)\",\"type\":\"boolean\"},\"utm_medium\":{\"description\":\"query utm_medium\",\"type\":\"string\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},\"utmMedium\":{\"description\":\"query utm_medium\",\"type\":\"string\"},\"utm_campaign\":{\"description\":\"query utm_campaign\",\"type\":\"string\"},\"adminLink\":{\"description\":\"Link to access more information about the user\",\"type\":\"string\"},\"createdAt\":{\"description\":\"Timestamp of user creation\",\"type\":\"number\"},\"utmSource\":{\"description\":\"query utm_source\",\"type\":\"string\"},\"email\":{\"description\":\"Email address for the user\",\"type\":\"string\"},\"authProvider\":{\"description\":\"Auth provider (login method)\",\"type\":\"string\"},\"isSnyk\":{\"description\":\"Whether or not the user belongs to the Snyk org (determined by the email address ending with @snyk.io)\",\"type\":\"boolean\"},\"utm_source\":{\"description\":\"query utm_source\",\"type\":\"string\"},\"created_at\":{\"description\":\"Timestamp of user creation\",\"type\":\"number\"},\"auth_provider\":{\"description\":\"Auth provider (login method)\",\"type\":\"string\"},\"hasFirstProject\":{\"description\":\"Whether or not the user has their first project imported\",\"type\":\"boolean\"},\"isSnykAdmin\":{\"description\":\"Whether or not the user should be considered a Snyk administrator\",\"type\":\"boolean\"},\"user_id\":{\"description\":\"Public ID of user\",\"type\":\"string\"},\"hasFirstIntegration\":{\"description\":\"Whether or not the user has their first integration set up\",\"type\":\"boolean\"},\"admin_link\":{\"description\":\"Link to access more information about the user\",\"type\":\"string\"},\"username\":{\"description\":\"Username of the user\",\"type\":\"string\"},\"utmCampaign\":{\"description\":\"query utm_campaign\",\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[]}");
        schemas.put("page", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/page\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Page\",\"description\":\"\",\"type\":\"object\",\"properties\":{\"package\":{\"description\":\"The name of the package\",\"type\":\"string\"},\"search\":{\"description\":\"Query parameters in url.\",\"type\":\"string\"},\"url\":{\"description\":\"The url of the page.\",\"type\":\"string\"},\"title\":{\"description\":\"The page title.\",\"type\":\"string\"},\"path\":{\"description\":\"The canonical path of the page\",\"type\":\"string\"},\"ecosystem\":{\"description\":\"Name of the ecosystem (npm|python|docker...)\",\"type\":\"string\"},\"referrer\":{\"description\":\"The page that linked to this page.\",\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[]}");
        schemas.put("Analysis Is Ready", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Analysis%20Is%20Ready/version/2.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Analysis Is Ready\",\"description\":\"Triggered when the analysis is loaded within the IDE.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true},\"analysisType\":{\"description\":\"Analysis types selected by the user for the scan:\\\\n\\\\n* open source vulnerabilities\\\\n\\\\n* code quality issues\\\\n\\\\n* code security vulnerabilities\\\\n\\\\n* advisor issues\\\\n\\\\n* infrastructure as code issues\\\\n\\\\n* container vulnerabilities\",\"enum\":[\"Snyk Advisor\",\"Snyk Code Quality\",\"Snyk Code Security\",\"Snyk Open Source\",\"Snyk Container\",\"Snyk Infrastructure as Code\"]},\"result\":{\"description\":\"\",\"enum\":[\"Success\",\"Error\"]}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\",\"analysisType\",\"result\"]}");
        schemas.put("Analysis Is Triggered", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Analysis%20Is%20Triggered/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Analysis Is Triggered\",\"description\":\"User triggers an analysis or analysis is automatically triggered.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true},\"analysisType\":{\"description\":\"Analysis types selected by the user for the scan: open source vulnerabilities, code quality issues and/or code security vulnerabilities.\",\"type\":\"array\",\"items\":{\"type\":\"string\"},\"uniqueItems\":true},\"triggeredByUser\":{\"description\":\"* True means that the analysis was triggered by the User.\\\\n\\\\n* False means that the analysis was triggered automatically by the plugin.\",\"type\":\"boolean\"}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\",\"analysisType\",\"triggeredByUser\"]}");
        schemas.put("Health Score Is Clicked", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Health%20Score%20Is%20Clicked/version/4.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Health Score Is Clicked\",\"description\":\"Triggered when the user clicks the health score of a package.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"ecosystem\":{\"description\":\"Advisor: what ecosystem for advisor score was used:\\\\n\\\\n* docker\\\\n\\\\n* npm\\\\n\\\\n* python\\\\n\\\\nLearn: the most general grouping for topics/lessons:\\\\n\\\\n* java\\\\n\\\\n* javascript\\\\n\\\\n* kubernetes\",\"enum\":[\"npm\",\"python\",\"docker\",\"java\",\"javascript\",\"kubernetes\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true},\"packageName\":{\"description\":\"Name of the package for which we get a score.\",\"type\":\"string\"}},\"additionalProperties\":false,\"required\":[\"ide\",\"ecosystem\",\"itly\",\"packageName\"]}");
        schemas.put("Issue Is Viewed", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Issue%20Is%20Viewed/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Issue Is Viewed\",\"description\":\"Triggered when the user selects an issue from the issues list and the issue is loaded.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"issueType\":{\"description\":\"Issue type\",\"enum\":[\"Open Source Vulnerability\",\"Licence Issue\",\"Code Quality Issue\",\"Code Security Vulnerability\",\"Advisor\"]},\"severity\":{\"description\":\"Severity of the issue\",\"enum\":[\"High\",\"Medium\",\"Low\",\"Critical\"]},\"issueId\":{\"description\":\"Issue ID as received from the backend.\",\"type\":\"string\"},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true}},\"additionalProperties\":false,\"required\":[\"ide\",\"issueType\",\"severity\",\"issueId\",\"itly\"]}");
        schemas.put("Plugin Is Installed", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Plugin%20Is%20Installed/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Plugin Is Installed\",\"description\":\"Triggered when the user installs the plugin.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\"]}");
        schemas.put("Plugin Is Uninstalled", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Plugin%20Is%20Uninstalled/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Plugin Is Uninstalled\",\"description\":\"Triggered when the user uninstalls the plugin.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\"]}");
        schemas.put("Product Selection Is Viewed", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Product%20Selection%20Is%20Viewed/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Product Selection Is Viewed\",\"description\":\"User has connected the IDE with Snyk and needs to decide which products to enable.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\"]}");
        schemas.put("Welcome Is Viewed", "{\"$id\":\"https://data.amplitude.com/6d7c255e-5bf5-46fc-be87-58b75c7e0ad5/event/Welcome%20Is%20Viewed/version/1.0.0\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"Welcome Is Viewed\",\"description\":\"User installs the IDE plugin and see Snyk's welcome screen.\",\"type\":\"object\",\"properties\":{\"ide\":{\"description\":\"Ide family.\",\"enum\":[\"Visual Studio Code\",\"Visual Studio\",\"Eclipse\",\"JetBrains\"]},\"itly\":{\"description\":\"Set to true for filtering within Segment\",\"const\":true}},\"additionalProperties\":false,\"required\":[\"ide\",\"itly\"]}");

        List<Plugin> plugins = new ArrayList<>(Arrays.<Plugin>asList(
            new SchemaValidatorPlugin(schemas)
        ));
        plugins.addAll(options.plugins);

        itly.load(options.withOverrides(null, plugins));
    }

    /**
     * Returns singleton instance of Itly
     */
    public static Itly getInstance() {
        if (singleton == null) {
            createSingleton();
        }
        return singleton;
    }

    private synchronized static void createSingleton() {
        if (singleton == null) {
            singleton = new Itly();
        }
    }

    /**
     * Alias a user ID.
     * @param userId The user's new ID.
     */
    public void alias(String userId) {
        itly.alias(userId, null);
    }

    /**
     * Alias a user ID to another user ID.
     * @param userId The user's new ID.
     * @param previousId The user's previous ID.
     */
    public void alias(String userId, String previousId) {
        itly.alias(userId, previousId);
    }

    /**
     * Set or update a user's properties.
     * @param properties Required and optional user properties.
     */
    public void identify(Identify properties) {
        itly.identify(null, properties);
    }

    /**
     * Set or update a user's properties.
     * @param userId The user's ID.
     * @param properties Required and optional user properties.
     */
    public void identify(String userId, Identify properties) {
        itly.identify(userId, properties);
    }

    /**
     * Set or update a group's properties.
     * @param userId The user's ID.
     * @param groupId The group's ID.
     * @param properties Required and optional group properties.
     */
    public void group(String userId, String groupId, Group properties) {
        itly.group(userId, groupId, properties);
    }
    /**
     * Triggered when the analysis is loaded within the IDE.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void analysisIsReady(String userId, AnalysisIsReady event) {
        track(userId, event);
    }
    /**
     * User triggers an analysis or analysis is automatically triggered.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void analysisIsTriggered(String userId, AnalysisIsTriggered event) {
        track(userId, event);
    }
    /**
     * Triggered when the user clicks the health score of a package.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void healthScoreIsClicked(String userId, HealthScoreIsClicked event) {
        track(userId, event);
    }
    /**
     * Triggered when the user selects an issue from the issues list and the issue is loaded.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void issueIsViewed(String userId, IssueIsViewed event) {
        track(userId, event);
    }
    /**
     * Triggered when the user installs the plugin.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void pluginIsInstalled(String userId, PluginIsInstalled event) {
        track(userId, event);
    }
    /**
     * Triggered when the user uninstalls the plugin.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void pluginIsUninstalled(String userId, PluginIsUninstalled event) {
        track(userId, event);
    }
    /**
     * User has connected the IDE with Snyk and needs to decide which products to enable.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void productSelectionIsViewed(String userId, ProductSelectionIsViewed event) {
        track(userId, event);
    }
    /**
     * User installs the IDE plugin and see Snyk's welcome screen.
     *
     * Owner: Georgi Mitev
     * @param userId The user's ID.
     * @param event
     */
    public void welcomeIsViewed(String userId, WelcomeIsViewed event) {
        track(userId, event);
    }

    public ly.iterative.itly.jvm.Itly getCore() {
        return itly;
    }

    /**
     * Track an event
     * @param userId The user's ID.
     * @param event The event to track
     */
    public void track(String userId, Event event) {
        itly.track(userId, event);
    }

    /**
     * Called when user is logged out.
     */
    public void reset() {
        itly.reset();
    }

    /**
     * Flushes all pending events
     */
    public void flush() {
        itly.flush();
    }

    /**
     * Stop all run processes and free resources.
     */
    public void shutdown() {
        itly.shutdown();
    }
}
