package comp3111.coursescraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the filter for scrapped courses
 * @author Hyeonjae Kim
 *
 */
public class Filter {
	/**
	 * Represents whether each CheckBox in the Filter tab is checked or not
	 */
	private boolean filter_flag[];
	/**
	 * Represents the total number of filtering conditions 
	 */
	private final int NUM_FILTER = 11;
	
	/**
	 * Creates Filter object and initializes the filter_flag array with false
	 */
	public Filter() {
		filter_flag = new boolean[NUM_FILTER];
		for(int i = 0; i < NUM_FILTER; ++i) {
			filter_flag[i] = false;
		}
	}
	
	/**
	 * Set the value of filter_flag array according to value of the respective CheckBox
	 * 
	 * @param id id of CheckBox
	 * @param value the selected status of CheckBox 
	 */
	public void setFilterFlag(String id, boolean value) {
		filter_flag[filterIdToIdx(id)] = value;
	}
	
	/**
	 * Set the all values of fiilter_flag array with the value entered 
	 * 
	 * @param value to-be-applied boolean value
	 */
	public void setFilterFlag(boolean value) {
		for(int i = 0; i < NUM_FILTER; ++i) filter_flag[i] = value;
	}
	
	/**
	 * Return the List<Course> after applying filter to the input list
	 * 
	 * @param input list to be filtered
	 * @return filtered array satisfying all filtering conditions
	 */
	public List<Course> filterList(List<Course> input) {
		List<Course> output = new ArrayList<Course>();
		for(Course c : input) {
			output.add(c.clone());
		}
		
		output.removeIf(course -> {
			  if(course.getNumSections() != 0) {
				  List<Section> temp = new ArrayList<Section>(Arrays.asList(course.getAllSection()));
				  List<Slot> slot = new ArrayList<Slot>();
				  for(Section sec: temp) {
					  if(sec != null) {
						  for(Slot s: sec.getAllSlot()) if(s != null) slot.add(s);
					  }
				  }
				  
				  return !(satisfySectionConditions(slot) && satisfyCourseConditions(course));
			  } else {
				  return true;
			  }
		});

		return output;
	}
	
	/**
	 * Return boolean value whether a list of slots satisfy filtering conditions
	 * 
	 * @param slots a list of slots to be checked
	 * @return true if a list of slots satisfy filtering conditions, false otherwise
	 */
	private boolean satisfySectionConditions(List<Slot> slots) {
		boolean[] note = new boolean[NUM_FILTER - 3];
		System.arraycopy(filter_flag, 0, note, 0, NUM_FILTER - 3);

		for(Slot s: slots) {
			if(s == null) continue;
			if(filter_flag[0] && s.getStartHour() < 12) note[0] = false;
			if(filter_flag[1] && s.getEndHour() >= 12) note[1] = false;
			if(filter_flag[2] && s.getDay() == 0) note[2] = false;
			if(filter_flag[3] && s.getDay() == 1) note[3] = false;
			if(filter_flag[4] && s.getDay() == 2) note[4] = false;
			if(filter_flag[5] && s.getDay() == 3) note[5] = false;
			if(filter_flag[6] && s.getDay() == 4) note[6] = false;
			if(filter_flag[7] && s.getDay() == 5) note[7] = false;
		}
		
		for(boolean value: note)
			if(value) return false;
		
		return true;
	}
	
	/**
	 * Return boolean value whether a course satisfies filtering condition
	 * 
	 * @param course a course to be checked
	 * @return true if a course satisfies all applied filtering conditions, false otherwise
	 */
	private boolean satisfyCourseConditions(Course course) {
		boolean[] note = new boolean[3];
		System.arraycopy(filter_flag, NUM_FILTER - 3, note, 0, 3);

		if(filter_flag[NUM_FILTER - 3] && course.getAttributes().indexOf("Common Core") != -1) note[0] = false;
		if(filter_flag[NUM_FILTER - 2] && course.getExclusion() == "null") note[1] = false;
		if(filter_flag[NUM_FILTER - 1]) {
			List<Section> temp = new ArrayList<Section>(Arrays.asList(course.getAllSection()));
			temp.forEach(section -> {
				if(section != null && 
						(section.getLectureSection().indexOf("LA") != -1 || section.getLectureSection().indexOf("T") != -1)) 
					note[2] = false;	
			});
		}
			
		for(boolean value: note)
			if(value) return false;
		
		return true;
	}
	
	/**
	 * Convert CheckBox id to the corresponding filter_array index
	 * 
	 * @param id CheckBox element's id
	 * @return filter_array index corresponding to CheckBox element's id 
	 */
	private int filterIdToIdx(String id) {
		switch(id) {
			case "am":
				return 0;
			case "pm":
				return 1;
			case "mon":
				return 2;
			case "tue":
				return 3;
			case "wed":
				return 4;
			case "thu":
				return 5;
			case "fri":
				return 6;
			case "sat":
				return 7;
			case "cc":
				return 8;
			case "ne":
				return 9;
			case "wlt":
				return 10;
			default:
				return -1;
		}
	}
}