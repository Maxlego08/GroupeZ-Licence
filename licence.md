# Licence

{% api-method method="get" host="https://groupez.dev" path="/api/v1/licence/:key" %}
{% api-method-summary %}
Licence
{% endapi-method-summary %}

{% api-method-description %}
This endpoint allows you to check is licence key is correct.
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="key" type="string" required=true %}
Licence key
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-query-parameters %}
{% api-method-parameter name="unique\_id" type="string" required=true %}
Unique identifier for each resource, you can find this identifier on the dashboard
{% endapi-method-parameter %}
{% endapi-method-query-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Licence is valid.
{% endapi-method-response-example-description %}

```
{    "status": "success",    "message": "The license is valid"    }
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

## Example

{% embed url="https://github.com/Maxlego08/GroupeZ-Licence" %}

{% tabs %}
{% tab title="LicenceVerif" %}
```java
package fr.groupez.licence;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LicenceVerif {

	private final String URL = "https://groupez.dev/api/v1/licence/%s";
	private final String licence;
	private final String uniqueId;
	private String status;
	private String message;
	private boolean isValid;
	private boolean enableLog = false;

	/**
	 * @param licence
	 * @param uniqueId
	 */
	public LicenceVerif(String licence, String uniqueId) {
		super();
		this.licence = licence;
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the enableLog
	 */
	public boolean isEnableLog() {
		return enableLog;
	}

	/**
	 * @param enableLog
	 *            the enableLog to set
	 */
	public void setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @return the licence
	 */
	public String getLicence() {
		return licence;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the isValid
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Check
	 */
	public void check() {
		try {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("unique_id", this.uniqueId);
			Map<String, String> response = this.sendData(jsonObject, this.licence);
			if (response.containsKey("status")) {
				this.status = response.get("status");
				this.message = response.get("message");
				if (status.equals("success"))
					this.isValid = true;
			} else
				this.isValid = false;

		} catch (Exception e) {
			if (this.enableLog)
				e.printStackTrace();
			this.isValid = false;
		}
	}

	/**
	 * 
	 * @param data
	 * @param licence
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> sendData(JsonObject data, String licence) throws Exception {

		if (data == null)
			throw new IllegalArgumentException("Data cannot be null!");

		URL url = new URL(String.format(URL, licence));
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

		byte[] bytes = data.toString().getBytes(StandardCharsets.UTF_8);
		int lenght = bytes.length;

		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Length", String.valueOf(lenght));
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "GroupeZ/1");
		connection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();

		InputStream inputStream = connection.getInputStream();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null)
			builder.append(line);
		bufferedReader.close();

		Gson gson = new Gson();
		return gson.fromJson(builder.toString(), Map.class);
	}

	/**
	 * Check async
	 * 
	 * @param runnable
	 */
	public void asynCheck(Runnable runnable) {
		Thread thread = new Thread(() -> {
			this.check();
			runnable.run();
		});
		thread.start();
	}

}
```
{% endtab %}

{% tab title="Licence" %}
```java
package fr.groupez.licence;

import java.net.UnknownHostException;

public class Licence {

	public static void main(String[] args) throws UnknownHostException {

		if (args.length != 2) {
			System.out.println("Invalid argument: java -jar Licence.jar <licence> <unique_id>");
			return;
		}

		String licence = args[0];
		String uniqueId = args[1];
		LicenceVerif licenceVerif = new LicenceVerif(licence, uniqueId);
		licenceVerif.setEnableLog(true);
		licenceVerif.asynCheck(() -> {
			System.out.println(licenceVerif.isValid() ? "Valid" : "Invalid");
			System.out.println(licenceVerif.getStatus());
			System.out.println(licenceVerif.getMessage());
		});
		System.out.println("Start licence check with key : " + licence);
	}

}
```
{% endtab %}
{% endtabs %}

