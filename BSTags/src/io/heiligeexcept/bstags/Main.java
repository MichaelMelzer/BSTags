package io.heiligeexcept.bstags;

import java.util.HashMap;

public class Main {

	public static String[] args_;
	public static HashMap<String, String> args = new HashMap<String, String>();
	
    public static void main(String[] args) {
    	Main.args_ = args;
    	for (int i = 0; i < args.length-1; i++) {
    		Main.args.put(args[i], args[i+1]);
    	}
        new BSVCrawler();
    }

}