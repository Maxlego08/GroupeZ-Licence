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
