package comp3111.coursescraper;


import org.junit.Test;

import comp3111.coursescraper.Course;

import static org.junit.Assert.*;


public class ItemTest {

	@Test
	public void testSetTitle() {
		Course i = new Course();
		i.setTitle("ABCDE");
		assertEquals(i.getTitle(), "ABCDE");
	}

//	@Test
//	public void testIsValidSection() {
//		Course i = new Course();
//		Section s = new Section();
//		s.setLectureSection("R1");
//
//		Section[] sections = {s};
//
//		i.setSections(sections);
//
//		assertEquals(isValidSection())
//	}
}
