package engine.agent;

/**
 * Class to hold information about a piece of
 * glass in the glassline factory
 * 
 * @author Justin
 *
 */
public class Part {
	//10-digit recipe used to track which stations should operate on the glass
	//the first digit, recipe[0], would be considered at workstation 0 and so on
	private String recipe = new String();
	
	/**
	 * Part class constructor
	 * 
	 * @param recipe
	 * 			the 10-digit recipe associated with the processing of this piece of glass
	 */
	public Part( String recipe ) {
		this.recipe = recipe;
	}

	
	/**
	 * Gets the part's recipe
	 * @return
	 * 		part's recipe
	 */
	public String getRecipe() {
		return recipe;
	}
	
	/**
	 * Sets the part's recipe
	 * @param recipe
	 * 		part's recipe
	 */
	public void setRecipe( String recipe ) {
		this.recipe = recipe;
	}
	
	/**
	 * Sets one element in the part's recipe
	 * @param recipe
	 * 		element i in recipe
	 * 		char c is replacement
	 */
	public void setRecipeElement( int i, char c ) {
		recipe = recipe.substring( 0, i ) + c + recipe.substring( i+1 );
		//this.recipe.replace(recipe.charAt(i), c);
	}
}
