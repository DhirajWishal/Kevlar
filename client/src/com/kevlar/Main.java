package com.kevlar;

public class Main {
	public static void main(String[] args) {
		try {
			Connector connector = new Connector();
			

			Application application = new Application();
			application.run();
			application.cleanup();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}