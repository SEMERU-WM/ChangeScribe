package co.edu.unal.colswe.changescribe.core.stereotype.taxonomy;


public enum MethodStereotype implements CodeStereotype {
    GET("GET", 0, Category.ACCESSOR, Subcategory.GET), 
    PREDICATE("PREDICATE", 1, Category.ACCESSOR, Subcategory.PREDICATE), 
    PROPERTY("PROPERTY", 2, Category.ACCESSOR, Subcategory.PROPERTY), 
    VOID_ACCESSOR("VOID_ACCESSOR", 3, Category.ACCESSOR, Subcategory.VOID_ACCESSOR), 
    SET("SET", 4, Category.MUTATOR, Subcategory.SET), 
    COMMAND("COMMAND", 5, Category.MUTATOR, Subcategory.COMMAND), 
    NON_VOID_COMMAND("NON_VOID_COMMAND", 6, Category.MUTATOR, Subcategory.NON_VOID_COMMAND), 
    CONSTRUCTOR("CONSTRUCTOR", 7, Category.CREATIONAL, Subcategory.CONSTRUCTOR), 
    COPY_CONSTRUCTOR("COPY_CONSTRUCTOR", 8, Category.CREATIONAL, Subcategory.COPY_CONSTRUCTOR), 
    DESTRUCTOR("DESTRUCTOR", 9, Category.CREATIONAL, Subcategory.DESTRUCTOR), 
    FACTORY("FACTORY", 10, Category.CREATIONAL, Subcategory.FACTORY), 
    COLLABORATOR("COLLABORATOR", 11, Category.COLLABORATIONAL, Subcategory.COLLABORATOR), 
    CONTROLLER("CONTROLLER", 12, Category.COLLABORATIONAL, Subcategory.CONTROLLER), 
    LOCAL_CONTROLLER("LOCAL_CONTROLLER", 13, Category.COLLABORATIONAL, Subcategory.LOCAL_CONTROLLER), 
    INCIDENTAL("INCIDENTAL", 14, Category.DEGENERATE, Subcategory.INCIDENTAL), 
    EMPTY("EMPTY", 15, Category.DEGENERATE, Subcategory.EMPTY), 
    ABSTRACT("ABSTRACT", 16, Category.DEGENERATE, Subcategory.ABSTRACT);
    
    private final int id;
    private final Category category;
    private final Subcategory subcategory;
    
    private MethodStereotype(final String s, final int n, final Category category, final Subcategory subcategory) {
    	this.id = n;
        this.category = category;
        this.subcategory = subcategory;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public Subcategory getSubcategory() {
        return this.subcategory;
    }
    
    public int getId() {
		return id;
	}

	public enum Category {
        ACCESSOR("ACCESSOR", 0), 
        MUTATOR("MUTATOR", 1), 
        CREATIONAL("CREATIONAL", 2), 
        COLLABORATIONAL("COLLABORATIONAL", 3), 
        DEGENERATE("DEGENERATE", 4);
        
        private String name;
        private int id;
        
		private Category(String name, int id) {
			this.setName(name);
			this.setId(id);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
    }
    
    public enum Subcategory {
    	
        GET("GET", 0, "Returns a local field directly"), 
        PREDICATE("PREDICATE", 1, "Returns a Boolean value that is not a local field"), 
        PROPERTY("PROPERTY", 2, "Returns information about local fields"), 
        VOID_ACCESSOR("VOID_ACCESSOR", 3, "Returns information about local fields through the parameters"), 
        SET("SET", 4, "Changes only one local field"), 
        COMMAND("COMMAND", 5, "Changes more than one local fields"), 
        NON_VOID_COMMAND("NON_VOID_COMMAND", 6, "Command whose return type is not void or Boolean"), 
        CONSTRUCTOR("CONSTRUCTOR", 7, "Invoked when creating an object"), 
        COPY_CONSTRUCTOR("COPY_CONSTRUCTOR", 8, "Creates a new object as a copy of the existing one"), 
        DESTRUCTOR("DESTRUCTOR", 9, "Performs any necessary cleanups before the object is destroyed"), 
        FACTORY("FACTORY", 10, "Instantiates an object and returns it"), 
        COLLABORATOR("COLLABORATOR", 11, "Connects one object with other type of objects"), 
        CONTROLLER("CONTROLLER", 12, "Provides control logic by invoking only external methods"), 
        LOCAL_CONTROLLER("LOCAL_CONTROLLER", 13, "Provides control logic by invoking only local methods"), 
        INCIDENTAL("INCIDENTAL", 14, "Any other case"), 
        EMPTY("EMPTY", 15, "Has no statements"), 
        ABSTRACT("ABSTRACT", 16, "Has no body");

        private String name;
        private int id;
        private String description;
        
		private Subcategory(String name, int id, String description) {
			this.setName(name);
			this.setId(id);
			this.setDescription(description);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
    }
}
