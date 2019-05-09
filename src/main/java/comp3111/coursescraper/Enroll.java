package comp3111.coursescraper;

public class Enroll {
	private static final int DEFAULT_MAX_SLOT = 3;
	
	private String coursecode;
	private String coursename;
	private String lecturesection;
	private Slot[] slots;
	private int numSlots;
	
	public Enroll() {
		slots = new Slot[DEFAULT_MAX_SLOT];
		for (int i = 0; i < DEFAULT_MAX_SLOT; i++)
			slots[i] = null;
		numSlots = 0;
	}
	
	public void setCourseCode(String coursecode) {
		this.coursecode = coursecode;
	}
	
	public void setCourseName(String coursename) {
		this.coursename = coursename;
	}
	
	public void setLectureSection(String lecturesection) {
		this.lecturesection = lecturesection;
	}
	
	public void addSlot(Slot s) {
		if (numSlots >= DEFAULT_MAX_SLOT)
			return;
		slots[numSlots++] = s.clone();
	}
	
	public void setNumSlots(int numSlots) {
		this.numSlots = numSlots;
	}
	
	public String getCourseCode() {
		return coursecode;
	}
	
	public String getCourseName() {
		return coursename;
	}
	
	public String getLectureSection() {
		return lecturesection;
	}
	
	public Slot[] getAllSlot() {
		return slots;
	}
	
	public Slot getSlot(int i) {
		if (i >= 0 && i < numSlots)
			return slots[i];
		return null;
	}
	
	public int getNumSlots() {
		return numSlots;
	}
}
