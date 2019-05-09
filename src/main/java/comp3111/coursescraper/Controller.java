package comp3111.coursescraper;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.time.LocalTime;
import java.util.*;


/**
 * Represents the GUI controller of this application
 * 
 * @author Chanhyeok Yim, Woo Kyung Kim, Hyeonjae Kim
 *
 */
public class Controller {

	@FXML
	private Tab tabMain;

	@FXML
	private TextField textfieldTerm;

	@FXML
	private TextField textfieldSubject;

	@FXML
	private Button buttonSearch;

	@FXML
	private TextField textfieldURL;

	@FXML
	private Tab tabStatistic;

	@FXML
	private ComboBox<?> comboboxTimeSlot;

	@FXML
	private Tab tabFilter;

	@FXML
	private AnchorPane filterPane;

	@FXML
	private Tab tabList;

	@FXML
	private Tab tabTimetable;

	@FXML
	private Tab tabAllSubject;

	@FXML
	private Button buttonAllSearch;

	@FXML
	private ProgressBar progressbar;

	@FXML
	private TextField textfieldSfqUrl;

	@FXML
	private Button buttonSfqEnrollCourse;

	@FXML
	private Button buttonInstructorSfq;

	@FXML
	private TextArea textAreaConsole;

	@FXML
	private TableView<ListContent> ListTable;

	@FXML
	private TableColumn<ListContent, String> CourseCodeColumn;

	@FXML
	private TableColumn<ListContent, String> SectionColumn;

	@FXML
	private TableColumn<ListContent, String> CourseNameColumn;

	@FXML
	private TableColumn<ListContent, String> InstructorColumn;

	@FXML
	private TableColumn<ListContent, CheckBox> EnrollColumn;

	private Scraper scraper = new Scraper();
	private Filter filter = new Filter();
	private ObservableList<ListContent> list = FXCollections.observableArrayList();
	private List<Course> v = new ArrayList<Course>();

	private List<Course> FilterList = new ArrayList<Course>();
	private HashMap<String, Enroll> enrollList = new HashMap<String, Enroll>();
	private List<Course> EnrollList = new ArrayList<Course>();
	private List <Label> enrollLabel = new ArrayList<Label>();
	
	/**
	 * After FXML elements are loaded, this method is adding an event to "search"
	 * button and "all subject search" button. After two buttons are clicked, the
	 * button for scraping SFQ info is enabled
	 */
	@FXML
	public void initialize() {
		EventHandler<ActionEvent> button_eh = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getSource() instanceof Button) {
					Button b = (Button) event.getSource();
					if (b.getText().equals("Search")) {
						search();
						buttonSfqEnrollCourse.setDisable(false);
					} else if (b.getText().equals("All Subject Search")) {
						allSubjectSearch();
						buttonSfqEnrollCourse.setDisable(false);
					}
				}
			}
		};

		buttonSearch.setOnAction(button_eh);
		buttonAllSearch.setOnAction(button_eh);
	}

	/**
	 * When filtering CheckBox is checked/unchecked, this method will update the
	 * Filter object accordingly and show the filtered result on console.
	 * 
	 * @param event ActionEvent object
	 */
	@FXML
	void onFilterChecked(ActionEvent event) {
		CheckBox chk = (CheckBox) event.getSource();
		filter.setFilterFlag(chk.getId(), chk.isSelected());
		FilterList = new ArrayList<Course>(filter.filterList(v));
		textAreaConsole.clear();
		displayFilterInfo();
	}

	/**
	 * When "Select All" button in Filter tab is clicked, all filtering conditions
	 * are applied. When "De-select All" button in Filter tab is clicked, all
	 * filtering conditions become invalid.
	 * 
	 * @param event ActionEvent
	 */
	@FXML
	void onSelectAllButtonClicked(ActionEvent event) {
		Button b = (Button) event.getSource();
		ObservableList<Node> temp = filterPane.getChildren().filtered(child -> {
			return child instanceof CheckBox;
		});
		if (b.getText().equals("Select All")) {
			b.setText("De-select All");
			temp.forEach(checkbox -> {
				((CheckBox) checkbox).setSelected(true);
			});
			filter.setFilterFlag(true);
			FilterList = new ArrayList<Course>(filter.filterList(v));
			textAreaConsole.clear();
			displayFilterInfo();
		} else if (b.getText().equals("De-select All")) {
			b.setText("Select All");
			filter.setFilterFlag(false);
			temp.forEach(checkbox -> {
				((CheckBox) checkbox).setSelected(false);
			});
			FilterList = new ArrayList<Course>(filter.filterList(v));
			textAreaConsole.clear();
			displayFilterInfo();
		}
	}

	private boolean clicked = false;
	private String previousTerm = "";
	private double percentage = 0.0;

	private List<String> allSubjects = new ArrayList<>();

	/**
	 *
	 */
	@FXML
	void allSubjectSearch() {
		if (previousTerm == "") {
			previousTerm = textfieldTerm.getText();
		}

		if (clicked == false) {

			// Error Handling - when any of the search field except subject is empty
			if (textfieldURL.getText().isEmpty() || textfieldTerm.getText().isEmpty()) {
				textAreaConsole.setText("404 Page Not Found - please make sure to fill in the search field.");
				return;
			}

			// Error Handling - if textfieldTerm contains any character
			if (!textfieldTerm.getText().matches("[0-9]+")) {
				textAreaConsole.setText("404 Page Not Found - please make sure only numbers are in the Term search field.");
				return;
			}

			allSubjects.clear();
			allSubjects = scraper.scrapeAll(textfieldURL.getText(), textfieldTerm.getText()); // String List of All Categories

			//Error Handling - if list is empty
			if (allSubjects == null) {
				textAreaConsole.setText("404 Page Not Found - No search result. Please make sure to input proper URL, Term or Subject.");
				return;
			}
			progressbar.setProgress(0.0);
			textAreaConsole.clear();

			textAreaConsole.setText(textAreaConsole.getText() + "Total Number of Categories/Code Prefix: " + allSubjects.size() + "\n");

			clicked = true;

		} else if (clicked && !(previousTerm.equals(textfieldTerm.getText()))) { // For the case where term input has been changed after clicking once.
			textAreaConsole.clear();
			System.out.println("The term has been changed");
			allSubjects.clear();
			allSubjects = scraper.scrapeAll(textfieldURL.getText(), textfieldTerm.getText()); // String List of All Categories
			textAreaConsole.setText(textAreaConsole.getText() + "Total Number of Categories/Code Prefix: " + allSubjects.size() + "\n");
			previousTerm = textfieldTerm.getText();

		} else { // When button is clicked again
			percentage = 0.0;
			v.clear();

			Thread pb = new Thread() {
				@Override
				public void run() {
					int totalCourses = 0;
					ListIterator<String> allSubjectsItr = allSubjects.listIterator();

					while (allSubjectsItr.hasNext()) { // Scrape for each subject when the button is clicked again
						v.addAll(scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(), allSubjectsItr.next()));
						totalCourses = v.size();
						System.out.println("Subject is DONE");
						percentage += (1.0 / allSubjects.size());
						progressbar.setProgress(percentage);
					}

					textAreaConsole.setText(textAreaConsole.getText() + "Total Number of Courses " + totalCourses + "\n");
					clicked = false;
				}
			};

			pb.start();
		}
	}

	/**
	 * Prints out all instructors' name and their unadjusted SFQ scores on console
	 * This method is invoked when the button "List instructors' average SFQ" is
	 * clicked
	 */
	@FXML
	void findInstructorSfq() {
		textAreaConsole.clear();
		Map<String, SFQElement> result = scraper.sfq_scrape(textfieldSfqUrl.getText());
		if (result != null) {
			result.forEach((k, v) -> {
				textAreaConsole.appendText(v + "\n");
			});
		} else {
			textAreaConsole.appendText("Please try again.\n");
		}
	}

	String prev_course_title;
	/**
	 * Prints out unadjusted SFQ data of the enrolled courses on console. This
	 * method is invoked when the button "Find SFQ with my enrolled courses" is
	 * clicked
	 */
	@FXML
	void findSfqEnrollCourse() {
		textAreaConsole.clear();
		Map<String, SFQElement> result = scraper.sfq_scrape_for_enrolled(textfieldSfqUrl.getText());
		
		if (!enrollList.isEmpty()) {
			enrollList.forEach((key, value) -> {
				if (value.getCourseCode().equals(prev_course_title)) return;
				prev_course_title = value.getCourseCode();
				if (result.containsKey(value.getCourseCode())) {
					textAreaConsole.appendText("Course " + result.get(value.getCourseCode()).toString() + "\n");
				} else {
					textAreaConsole
							.appendText("Course " + value.getCourseCode() + " does not have SFQ score information.\n");
				}
			});
		} else {
			textAreaConsole.appendText("There is no enrolled course. Please enroll any course first.\n");
		}
	}
	

	/**
	 *
	 * @return boolean
	 * @author cyim
	 */
	public boolean isValidSection(Course c) {
		int sectionCount = c.getNumSections();

		for (int i = 0; i < c.getNumSections(); i++) {
			if (c.getSection(i).getLectureSection().contains("R")) {
				sectionCount--;
			}
		}
		if (sectionCount <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 *  Returns a boolean value to check whether there is an error or not while searching.
	 *  It will check for 3 cases - when any of the search field is empty,
	 *  when textfieldTerm contains any character,
	 *  when textfieldSubject contains any number.
	 *  returns true when there is error, returns false otherwise.
	 *
	 * @return boolean
	 * @author cyim
	 */
	public boolean isThereError() {
		// Error Handling - when any of the search field is empty
		if (textfieldURL.getText().isEmpty() || textfieldTerm.getText().isEmpty() || textfieldSubject.getText().isEmpty()) {
			textAreaConsole.setText("404 Page Not Found - please make sure to fill in the search field.");
			return true;
		}

		// Error Handling - if textfieldTerm contains any character
		if (!textfieldTerm.getText().matches("[0-9]+")) {
			textAreaConsole.setText("404 Page Not Found - please make sure only numbers are in the Term search field.");
			return true;
		}

		// Error Handling - if textfieldSubject contains any number
		if (!textfieldSubject.getText().matches("^[a-zA-Z]*$")) {
			textAreaConsole.setText("404 Page Not Found - please make sure only alphabets are in the Subject search field.");
			return true;
		}

		// Error Handling - if course list is empty
		v.clear();
		v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),textfieldSubject.getText());

		if (v == null) {
			textAreaConsole.setText("404 Page Not Found - No search result. Please make sure to input proper URL, Term or Subject.");
			return true;
		}

		return false;
	}

	/**
	 *
	 * @param courseList
	 * @param validCourseList
	 * @param instructorList
	 * @param count
	 */
	public void displayInstructorsAndSectionCount(List<Course> courseList, List<Course> validCourseList, List<String> instructorList, int count) {
		for (Course c : courseList) {
			if (!isValidSection(c)) { // If invalid course
//			System.out.println(c.getTitle() + "is an invalid section");
				validCourseList.remove(c); // remove from the list
			} else {
				for (int i = 0; i < c.getNumSections(); i++) {
					if (!c.getSection(i).getLectureSection().contains("R")) {
						count++;
						for (int j = 0; j < c.getSection(i).getNumSlots(); j++) { // Iterate through each slot for each Section
							int slotDay = c.getSection(i).getSlot(j).getDay();
							LocalTime slotStartTime = c.getSection(i).getSlot(j).getStart();
							LocalTime slotEndTime = c.getSection(i).getSlot(j).getEnd();
							LocalTime threeTenPM = LocalTime.of(3, 10);
							String[] instructorNames = c.getSection(i).getSlot(j).getInstructor().split("\r\n");

							if (slotDay != 1) { // If slotDay is not Tu(esday)
								if (threeTenPM.isBefore(slotStartTime) || threeTenPM.isAfter(slotEndTime)) { // If 3:10 PM does not fall under time between start to the end time
									for (int k = 0; k < instructorNames.length; k++) {
										if (!instructorList.contains(instructorNames[k])) { // For no duplicates
											instructorList.add(instructorNames[k]);
										}
									}
								}
							}
						}
					}
				}
			}
			instructorList.remove("TBA");

			textAreaConsole.setText("Total number of courses in this search: " + v.size() + "\n");
			textAreaConsole.setText(textAreaConsole.getText() + "Total number of different sections in this search: " + count + "\n");
			textAreaConsole.setText(textAreaConsole.getText() + "Instructors who has teaching assignment this term but does not need to teach at Tu 3:10 pm: " + "\n");

			Collections.sort(instructorList); // Sorting alphabetically
			ListIterator<String> itr = instructorList.listIterator();

			while (itr.hasNext()) {
				textAreaConsole.setText(textAreaConsole.getText() + itr.next() + "\n");
			}
		}
	}

	public void displaySectionAndSlot(List<Course> validCourseList) {
		for (Course c : validCourseList) {
			int num = 0;
			String newline = c.getTitle() + "\n";
			for (int i = 0; i < c.getNumSections(); i++) {
				if (!c.getSection(i).getLectureSection().contains("R")) // Print only valid sections
				{
					newline += "Section: " + c.getSection(i).getLectureSection() + "\n";
				}
				for (int j = 0; j < c.getSection(i).getNumSlots(); j++) {
					Slot t = c.getSection(i).getSlot(j);
					newline += "Slot " + num++ + ":" + t + "\n";
				}
			}
			textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
		}
	}

	/**
	 *
	 */
	@FXML
	void search() {
		textAreaConsole.clear();

		if(isThereError()) {
			return;
		}

		// Displays the total number of different sections in this search
		// Displays the total number of courses in this search
		// Display instructors who has teaching assignment who does not need to teach at Tu 3:10 PM

		int sectionCount = 0;
		List<String> instructors = new ArrayList<String>(); // For Instructors
		List<Course> onlyValidCourses = new ArrayList<Course>(v);

		displayInstructorsAndSectionCount(v, onlyValidCourses, instructors, sectionCount);

		displaySectionAndSlot(onlyValidCourses);
	}

	/**
	 * When List tab is clicked, fill List table with filtered courses.
	 * When check box is clicked, the section is enrolled and enrolled courses are
	 * displayed on the console
	 */
	@FXML
	void clickList() {
		
		list.clear();
		ListTable.setEditable(true);
		displayEnrollInfo();
		
		List<Course> temp = new ArrayList<Course>();
		if (FilterList.isEmpty())
			temp = v;
		else
			temp = FilterList;

		for (Course c : temp) {
			for (int i = 0; i < c.getNumSections(); i++) {
				if (c.getSection(i).getNumSlots() != 0) {
					makeCheckBox(c,i);
				}
			}
		}

		CourseCodeColumn.setCellValueFactory(new PropertyValueFactory<ListContent, String>("CourseCode"));
		SectionColumn.setCellValueFactory(new PropertyValueFactory<ListContent, String>("LectureSection"));
		CourseNameColumn.setCellValueFactory(new PropertyValueFactory<ListContent, String>("CourseName"));
		InstructorColumn.setCellValueFactory(new PropertyValueFactory<ListContent, String>("Instructor"));
		EnrollColumn.setCellValueFactory(new PropertyValueFactory<ListContent, CheckBox>("EnrollBox"));

		ListTable.setItems(list);
	}
	
	/**
	 * Makes single check box and apply event to it. 
	 * @param c Course
	 * @param i Number of Slot
	 */
	private void makeCheckBox(Course c, int i) {
		
		Course course = c.clone();
		Section section = course.getSection(i).clone();
		String key = course.getTitle() + section.getLectureSection();
		
		EventHandler<ActionEvent> actionCheckBox = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				
				if(enrollList.containsKey(key)) {
					enrollList.remove(key);				
				}
				else {
					Enroll enroll = new Enroll();
					String coursecode_temp = course.parseCourseCode();
					String lecturesection_temp = section.parseLectureSection();
					String coursename_temp = course.parseCourseName();
					
					enroll.setCourseCode(coursecode_temp);
					enroll.setCourseName(coursename_temp);
					enroll.setLectureSection(lecturesection_temp);
					for(int i = 0; i < section.getNumSlots(); i++) {
						Slot slot = section.getSlot(i).clone();
						enroll.addSlot(slot);
					}
					enrollList.put(key, enroll);
				}
				
				displayEnrollInfo();
			}
		};
		
		CheckBox cb = new CheckBox();
		if(enrollList.containsKey(key)) {
			cb.setSelected(true);
		}

		cb.setOnAction(actionCheckBox);
		ListContent item = new ListContent(c.getTitle(), c.getSection(i).getLectureSection(),
				c.getSection(i).getSlot(0).getInstructor(), cb, c);
		list.add(item);
	}

	/**
	 * When Time Table tab clicks it fills the timetable based on 
	 * the enrolled list. 
	 */
	@FXML
    void clickTimeTable() {
		
		AnchorPane ap = (AnchorPane)tabTimetable.getContent();
		
		for(int i = 0; i < enrollLabel.size(); i++) {
			ap.getChildren().remove(enrollLabel.get(i));
		}
		enrollLabel.clear();
		
		for(String s : enrollList.keySet()){	 
            Enroll value = enrollList.get(s);
            String labelcontent = value.getCourseCode()+" ("+value.getLectureSection()+")";
            
            Random random = new Random();
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);
            Color color = Color.rgb(r, g, b, 0.7);
            
            BackgroundFill background = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
            
            
            for(int i = 0; i < value.getNumSlots(); i++) {
            	Slot slot = value.getSlot(i);
 
            	Label label = new Label(labelcontent);
            	label.setFont(new Font("Verdan", 10));
            	double start = (double) (slot.getStartHour() * 20 - 140) + (double) slot.getStartMinute() * 1/3;
            	double end = (double) (slot.getEndHour() * 20 - 140) + (double) slot.getEndMinute() * 1/3;
            	double amount = end - start;

            	int intday = slot.getDay();
            	double day = 0.0;
            	switch (intday) {
            	case 0:
            		day = 100.0;
            		break;
            	case 1:
            		day = 200.0;
            		break;
            	case 2:
            		day = 300.0;
            		break;
            	case 3:
            		day = 400.0;
            		break;
            	case 4:
            		day = 500.0;
            		break;
            	case 5:
            		day = 600.0;
            		break;
            	}
            		
            	label.setBackground(new Background(background));
            	label.setLayoutX(day + 1.0);
            	label.setLayoutY(start);
            	label.setMinWidth(100.0);
            	label.setMaxWidth(100.0);
            	label.setMinHeight(amount);
            	label.setMaxHeight(amount);
            	
            	ap.getChildren().addAll(label);
            	enrollLabel.add(label);
            }
        }
    }
	
	/**
	 * Prints out filtered courses on console.
	 */
	private void displayFilterInfo() {
		/*
		for (Course c : FilterList) {
			int num = 0;
			String newline = c.getTitle() + "\n";
			for (int i = 0; i < c.getNumSections(); i++) {
				for (int j = 0; j < c.getSection(i).getNumSlots(); j++) {
					Slot t = c.getSection(i).getSlot(j);
					newline += "Slot " + num++ + ":" + t + "\n";
				}
			}
			textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
		}*/
		displaySectionAndSlot(FilterList);
	}

	/**
	 * Prints out enrolled courses on console.
	 */
	private void displayEnrollInfo() {
		textAreaConsole.setText("The following sections are enrolled:\n");
		
		
		for(String s : enrollList.keySet()){
			Enroll value = enrollList.get(s);
			 
			String newline = "";
			newline += value.getCourseCode() + " ";
			newline += value.getCourseName() + " ";
			newline += value.getLectureSection();
			textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
		}
	}

}
