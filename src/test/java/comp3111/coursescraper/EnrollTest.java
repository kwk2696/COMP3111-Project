package comp3111.coursescraper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class EnrollTest extends ApplicationTest{
	
	@Test
	public void testEnroll() {
		Enroll en = new Enroll();
		Slot s = new Slot();
		en.setCourseCode("COMP3111");
		en.setCourseName("Software Engineering");
		en.setLectureSection("L1");
		en.addSlot(s);
		assertEquals(en.getCourseCode(), "COMP3111");
		assertEquals(en.getCourseName(), "Software Engineering");
		assertEquals(en.getLectureSection(), "L1");
		assertEquals(en.getNumSlots(), 1);
		assertEquals(en.getSlot(1), null);
	}
}

