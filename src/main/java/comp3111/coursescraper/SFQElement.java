package comp3111.coursescraper;
/**
 * Represents a SFQ element
 * @author Hyeonjae Kim
 *
 */
public class SFQElement {
	/**
	 * Represents the element's key.
	 * Instructor's name or course name is usually assigned.
	 */
	private String key;
	private double average;
	private int num_sections;
	
	/**
	 * Creates a SFQ Element with the specified key
	 * @param key SFQ Element's key (i.e., instructor's name or course name)
	 */
	SFQElement(String key) {
		this.key = key;
		average = 0;
		num_sections = 0;
	}
	
	/**
	 * Compute the average SFQ score again when the new section is added
	 * @param score Added section's score
	 */
	public void addSection(double score) {
		average = (average * num_sections + score) / (double) (num_sections + 1);
		num_sections++;
	}
	
	/**
	 * @return string value containing key and average
	 */
	@Override
	public String toString() {
		return key + "'s average SFQ score is " + average;
	}
}
