package comp3111.coursescraper;

import org.junit.Test;

import static org.junit.Assert.*;

public class ControllerTest {

    Controller control = new Controller();

    @Test
	public void testIsValidSection() {

		Course i = new Course();
		Course p = new Course();
		Section wrongSect = new Section();
		Section correctSect = new Section();

		wrongSect.setLectureSection("R1");
		correctSect.setLectureSection("LA");

		Section[] sections = {wrongSect};
		Section[] correctSections = {correctSect};

		i.setSections(sections);
		p.setSections(correctSections);

		assertEquals(control.isValidSection(i), false);
		assertEquals(control.isValidSection(p), true);
	}

}
