package main;

public enum Command {
	START("start"), STOP("stop"), SUMMARY("summary"), DESCRIPTION("description"), SIZE("size"), RENAME("rename");
	// @SuppressWarnings("unused")
	// private final String sValue;

	Command(String Value) {
		// this.sValue = Value;
	}

}