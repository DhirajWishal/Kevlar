package com.kevlar;

import com.kevlar.client.Client;

public class Main {
	public static void main(String[] args) {
		try {
			System.out.println("Welcome to Kevlar!");

            Application application = new Client();
			application.run();
			application.cleanup();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}