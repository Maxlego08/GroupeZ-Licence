package fr.groupez.licence;

import java.net.UnknownHostException;

public class Licence {

	public static void main(String[] args) throws UnknownHostException {
		String licence = "iw7yXu45fJeAIwa";
		LicenceVerif licenceVerif = new LicenceVerif(licence);
		licenceVerif.setEnableLog(true);
		licenceVerif.asynCheck(() -> {
			System.out.println(licenceVerif.isValid() ? "Valid" : "Invalid");
			System.out.println(licenceVerif.getStatus());
			System.out.println(licenceVerif.getMessage());
		});
		System.out.println("Start licence check with key : " + licence);
	}

}
