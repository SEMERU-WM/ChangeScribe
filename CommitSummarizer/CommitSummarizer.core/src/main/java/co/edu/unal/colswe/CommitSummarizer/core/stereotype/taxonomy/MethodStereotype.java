package co.edu.unal.colswe.CommitSummarizer.core.stereotype.taxonomy;


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
    
    private final Category category;
    private final Subcategory subcategory;
    
    private MethodStereotype(final String s, final int n, final Category category, final Subcategory subcategory) {
        this.category = category;
        this.subcategory = subcategory;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public Subcategory getSubcategory() {
        return this.subcategory;
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
        GET("GET", 0), 
        PREDICATE("PREDICATE", 1), 
        PROPERTY("PROPERTY", 2), 
        VOID_ACCESSOR("VOID_ACCESSOR", 3), 
        SET("SET", 4), 
        COMMAND("COMMAND", 5), 
        NON_VOID_COMMAND("NON_VOID_COMMAND", 6), 
        CONSTRUCTOR("CONSTRUCTOR", 7), 
        COPY_CONSTRUCTOR("COPY_CONSTRUCTOR", 8), 
        DESTRUCTOR("DESTRUCTOR", 9), 
        FACTORY("FACTORY", 10), 
        COLLABORATOR("COLLABORATOR", 11), 
        CONTROLLER("CONTROLLER", 12), 
        LOCAL_CONTROLLER("LOCAL_CONTROLLER", 13), 
        INCIDENTAL("INCIDENTAL", 14), 
        EMPTY("EMPTY", 15), 
        ABSTRACT("ABSTRACT", 16);

        private String name;
        private int id;
        
		private Subcategory(String name, int id) {
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
}
