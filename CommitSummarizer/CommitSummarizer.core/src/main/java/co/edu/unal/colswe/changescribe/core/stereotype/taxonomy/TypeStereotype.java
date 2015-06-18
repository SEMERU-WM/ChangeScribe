package co.edu.unal.colswe.changescribe.core.stereotype.taxonomy;

public enum TypeStereotype implements CodeStereotype {
	
    ENTITY("ENTITY", 0), 
    MINIMAL_ENTITY("MINIMAL_ENTITY", 1), 
    DATA_PROVIDER("DATA_PROVIDER", 2), 
    COMMANDER("COMMANDER", 3), 
    BOUNDARY("BOUNDARY", 4), 
    FACTORY("FACTORY", 5), 
    CONTROLLER("CONTROLLER", 6), 
    PURE_CONTROLLER("PURE_CONTROLLER", 7), 
    LARGE_CLASS("LARGE_CLASS", 8), 
    LAZY_CLASS("LAZY_CLASS", 9), 
    DEGENERATE("DEGENERATE", 10), 
    DATA_CLASS("DATA_CLASS", 11), 
    POOL("POOL", 12), 
    INTERFACE("INTERFACE", 13);
    
    private String name;
    private int id;
    
	private TypeStereotype(String name, int id) {
		this.setName(name);
		this.setId(id);
	}

	@SuppressWarnings("unused")
	private String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unused")
	private int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
}
