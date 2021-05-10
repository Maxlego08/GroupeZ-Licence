# Version verification

{% api-method method="get" host="https://groupez.dev/api" path="/v1/resource/version/:id" %}
{% api-method-summary %}
Get resource version
{% endapi-method-summary %}

{% api-method-description %}
This endpoint allows you to get resource version.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="id" type="number" required=true %}
Resource id
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-headers %}
{% api-method-parameter name="User-agent" type="string" required=true %}
Mozilla/5.0
{% endapi-method-parameter %}
{% endapi-method-headers %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Version successfully retrieved.
{% endapi-method-response-example-description %}

```
1.0.0.0
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

## Example

You will be able to check if the version of the plugin is the same as the version on the site. You can then warn users to update the plugin.

```java
public class VersionChecker implements Listener {

	private final String URL_API = "https://groupez.dev/api/v1/resource/version/%s";
	private final String URL_RESOURCE = "https://groupez.dev/resources/%s";
	private final Plugin plugin;
	private final int pluginID;
	private boolean useLastVersion = false;

	/**
	 * Class constructor
	 * 
	 * @param plugin
	 * @param pluginID
	 */
	public VersionChecker(Plugin plugin, int pluginID) {
		super();
		this.plugin = plugin;
		this.pluginID = pluginID;
	}

	/**
	 * Allows to check if the plugin version is up to date.
	 */
	public void useLastVersion() {

		Bukkit.getPluginManager().registerEvents(this, this.plugin); // Register
																		// event

		String pluginVersion = plugin.getDescription().getVersion();
		AtomicBoolean atomicBoolean = new AtomicBoolean();
		this.getVersion(version -> {

			long ver = Long.valueOf(version.replace(".", ""));
			long plVersion = Long.valueOf(pluginVersion.replace(".", ""));
			atomicBoolean.set(plVersion >= ver);
			this.useLastVersion = atomicBoolean.get();
			if (atomicBoolean.get())
				Logger.info("No update available.");
			else {
				Logger.info("New update available. Your version: " + pluginVersion + ", latest version: " + version);
				Logger.info("Download plugin here: " + String.format(URL_RESOURCE, this.pluginID));
			}
		});

	}

	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!useLastVersion && event.getPlayer().hasPermission("zplugin.notifs")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					String prefix = "GroupeZ ";
					player.sendMessage(prefix + "§cYou do not use the latest version of the plugin! Thank you for taking the latest version to avoid any risk of problem!");
					player.sendMessage(prefix + "§fDownload plugin here: §a" + String.format(URL_RESOURCE, pluginID));
				}
			}.runTaskLater(plugin, 20 * 2);
		}
	}

	/**
	 * Get version by plugin id
	 * 
	 * @param consumer
	 *            - Do something after
	 */
	public void getVersion(Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			final String apiURL = String.format(URL_API, this.pluginID);
			try {
				URL url = new URL(apiURL);
				URLConnection hc = url.openConnection();
				hc.setRequestProperty("User-Agent", "Mozilla/5.0");
				Scanner scanner = new Scanner(hc.getInputStream());
				if (scanner.hasNext())
					consumer.accept(scanner.next());
				scanner.close();

			} catch (IOException exception) {
				this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
			}
		});
	}

}
```



