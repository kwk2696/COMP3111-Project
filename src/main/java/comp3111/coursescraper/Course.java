package comp3111.coursescraper;

import javafx.beans.property.SimpleStringProperty;

public class Course {
	private static final int DEFAULT_MAX_SECTION = 20;
	
	private String title;
	private String attributes;
	private String description;
	private String exclusion;
	private Section [] sections;
	private int numSections;
	
	@Override
	public Course clone() {
		Course c = new Course();
		c.title = this.title;
		c.description = this.description;
		c.exclusion = this.exclusion;
		c.sections = new Section[DEFAULT_MAX_SECTION];
		for(int i = 0; i < this.numSections; i++)
			c.sections[i] = this.sections[i].clone();
		c.numSections = this.numSections;
		c.attributes = this.attributes;
		return c;
	}
	
	public Course() {
		sections = new Section[DEFAULT_MAX_SECTION];
		for (int i = 0; i < DEFAULT_MAX_SECTION; i++) sections[i] = null;
		numSections = 0;
	}
	
	public void addSection(Section s) {
		if (numSections >= DEFAULT_MAX_SECTION)
			return;
		sections[numSections++] = s.clone();
	}
	
	public Section getSection(int i) {
		if (i >= 0 && i < numSections)
			return sections[i];
		return null;
	}
	
	public Section[] getAllSection() {
		return sections;
	}
	
	public Section getlastSection() {
		return numSections == 0 ? null : sections[numSections - 1];
	}
	
	public int getNumSections() {
		return numSections;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the exclusion
	 */
	public String getExclusion() {
		return exclusion;
	}

	/**
	 * @param exclusion the exclusion to set
	 */
	public void setExclusion(String exclusion) {
		this.exclusion = exclusion;
	}
	
	/**
	 * @return the attribute
	 */
	public String getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	public void setSections(Section[] sections) {
		for(int i = 0; i < sections.length; i++)
			this.sections[i] = sections[i];
		this.numSections = sections.length;
	}
	
	public void cleanSection() {
		for (int i = 0; i < DEFAULT_MAX_SECTION; i++) sections[i] = null;
		numSections = 0;
	}
	
	public String parseCourseCode() {
		String result = title.substring(0, title.lastIndexOf("-") - 1);
		return result;
	}
	
	public String parseCourseName() {
		String result = title.substring(title.lastIndexOf("-") + 1, title.lastIndexOf("(") - 1);
		return result;
	}
}
