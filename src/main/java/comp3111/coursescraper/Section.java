package comp3111.coursescraper;

public class Section {
	private static final int DEFAULT_MAX_SLOT = 3;

	private String lecturesection;
	private Slot[] slots;
	private int numSlots;

	@Override
	public Section clone() {
		Section s = new Section();
		// Check if it is a valid lecture section - RA is NOT a valid lecture section
		s.lecturesection = this.lecturesection;
		s.slots = new Slot[DEFAULT_MAX_SLOT];
		for (int i = 0; i < this.numSlots; i++)
			s.slots[i] = this.slots[i].clone();
		s.numSlots = this.numSlots;
		return s;
	}

	public Section() {
		slots = new Slot[DEFAULT_MAX_SLOT];
		for (int i = 0; i < DEFAULT_MAX_SLOT; i++)
			slots[i] = null;
		numSlots = 0;
	}

	public void addSlot(Slot s) {
		if (numSlots >= DEFAULT_MAX_SLOT)
			return;
		slots[numSlots++] = s.clone();
	}

	public Slot getSlot(int i) {
		if (i >= 0 && i < numSlots)
			return slots[i];
		return null;
	}

	public Slot[] getAllSlot() {
		return slots;
	}

	public String getLectureSection() {
		return lecturesection;
	}

	public void setLectureSection(String lecturesection) {
		this.lecturesection = lecturesection;
	}

	public int getNumSlots() {
		return numSlots;
	}

	public void setNumSlots(int numSlots) {
		this.numSlots = numSlots;
	}
	
	public String parseLectureSection() {
		String result = lecturesection.substring(0, lecturesection.lastIndexOf(" "));
		return result;
	}

}
