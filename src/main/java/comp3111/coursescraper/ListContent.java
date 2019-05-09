package comp3111.coursescraper;

import javafx.beans.property.SimpleStringProperty;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;

public class ListContent {
	private final SimpleStringProperty CourseCode;
	private final SimpleStringProperty LectureSection;
	private final SimpleStringProperty CourseName;
	private final SimpleStringProperty Instructor;
	private CheckBox EnrollBox;
	private Course course;
	
	public ListContent(String Title, String LectureSection, String Instructor, CheckBox EnrollBox, Course course) {
	
		this.CourseCode = new SimpleStringProperty(parseCourseCode(Title));
		this.LectureSection = new SimpleStringProperty(LectureSection);
		this.CourseName = new SimpleStringProperty(parseCourseName(Title));
		this.Instructor = new SimpleStringProperty(Instructor);
		this.EnrollBox = EnrollBox;
		
		this.course = course.clone();
		
	}
	
	/**
	 * @return the course code
	 */
	public String getCourseCode() {
		return CourseCode.get();
	}
	
	/**
	 * @return the lecture section
	 */
	public String getLectureSection() {
		return LectureSection.get();
	}
	
	/**
	 * @return the instructor
	 */
	public String getInstructor() {
		return Instructor.get();
	}
	
	/**
	 * @return the course name
	 */
	public String getCourseName() {
		return CourseName.get();
	}
	
	/**
	 * @return the check box 
	 */
	public CheckBox getEnrollBox() {
		return EnrollBox;
	}
	
	/**
	 * @return course
	 */
	public Course getCourse() {
		return course;
	}
	
	/**
	 * @param Title title to set course code
	 * @return course code parsed from title
	 */
	private String parseCourseCode(String Title) {
		String coursecode = Title.substring(0, Title.lastIndexOf("-") - 1);
		return coursecode;
	}
	
	/**
	 * @param Title title to set course name
	 * @return course name parsed from title
	 */
	private String parseCourseName(String Title) {
		String coursename = Title.substring(Title.lastIndexOf("-") + 2, Title.lastIndexOf("(") - 1);
		return coursename;
	}
}

