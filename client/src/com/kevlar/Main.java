package com.kevlar;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
	public static void main(String[] args) {
		try {
			byte[] bytes = Hasher.getSHA256("Thidas").getBytes(StandardCharsets.UTF_8);
			System.out.println(new String(bytes, StandardCharsets.UTF_8));
			Sender sender = new Sender(Base64.getEncoder().encodeToString(bytes));
			System.out.println(sender.getResponse());

			Application application = new Application();
			application.run();
			application.cleanup();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}