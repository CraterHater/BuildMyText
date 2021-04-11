package bmt.craterhater.commandhandler;

public class Argument {

	private ArgumentType type;
	private String description;
	
	private String label = null;
	
	public Argument(ArgumentType type, String description) {
		this.type = type;
		this.description = description;
	}
	
	public Argument(String label) {
		this.type = ArgumentType.LABEL;
		this.description = label;
		this.label = label;
	}

	public ArgumentType getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
}
