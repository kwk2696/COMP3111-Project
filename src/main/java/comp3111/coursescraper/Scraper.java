package comp3111.coursescraper;

import java.util.*;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;


/**
 * WebScraper provide a sample code that scrape web content. After it is constructed, you can call the method scrape with a keyword,
 * the client will go to the default url and parse the page by looking at the HTML DOM.
 * <br>
 * In this particular sample code, it access to HKUST class schedule and quota page (COMP).
 * <br>
 * https://w5.ab.ust.hk/wcq/cgi-bin/1830/subject/COMP
 *  <br>
 * where 1830 means the third spring term of the academic year 2018-19 and COMP is the course code begins with COMP.
 * <br>
 * Assume you are working on Chrome, paste the url into your browser and press F12 to load the source code of the HTML. You might be freak
 * out if you have never seen a HTML source code before. Keep calm and move on. Press Ctrl-Shift-C (or CMD-Shift-C if you got a mac) and move your
 * mouse cursor around, different part of the HTML code and the corresponding the HTML objects will be highlighted. Explore your HTML page from
 * body &rarr; div id="classes" &rarr; div class="course" &rarr;. You might see something like this:
 * <br>
 * <pre>
 * {@code
 * <div class="course">
 * <div class="courseanchor" style="position: relative; float: left; visibility: hidden; top: -164px;"><a name="COMP1001">&nbsp;</a></div>
 * <div class="courseinfo">
 * <div class="popup attrword"><span class="crseattrword">[3Y10]</span><div class="popupdetail">CC for 3Y 2010 &amp; 2011 cohorts</div></div><div class="popup attrword"><span class="crseattrword">[3Y12]</span><div class="popupdetail">CC for 3Y 2012 cohort</div></div><div class="popup attrword"><span class="crseattrword">[4Y]</span><div class="popupdetail">CC for 4Y 2012 and after</div></div><div class="popup attrword"><span class="crseattrword">[DELI]</span><div class="popupdetail">Mode of Delivery</div></div>
 *    <div class="courseattr popup">
 * 	    <span style="font-size: 12px; color: #688; font-weight: bold;">COURSE INFO</span>
 * 	    <div class="popupdetail">
 * 	    <table width="400">
 *         <tbody>
 *             <tr><th>ATTRIBUTES</th><td>Common Core (S&amp;T) for 2010 &amp; 2011 3Y programs<br>Common Core (S&amp;T) for 2012 3Y programs<br>Common Core (S&amp;T) for 4Y programs<br>[BLD] Blended learning</td></tr><tr><th>EXCLUSION</th><td>ISOM 2010, any COMP courses of 2000-level or above</td></tr><tr><th>DESCRIPTION</th><td>This course is an introduction to computers and computing tools. It introduces the organization and basic working mechanism of a computer system, including the development of the trend of modern computer system. It covers the fundamentals of computer hardware design and software application development. The course emphasizes the application of the state-of-the-art software tools to solve problems and present solutions via a range of skills related to multimedia and internet computing tools such as internet, e-mail, WWW, webpage design, computer animation, spread sheet charts/figures, presentations with graphics and animations, etc. The course also covers business, accessibility, and relevant security issues in the use of computers and Internet.</td>
 *             </tr>
 *          </tbody>
 *      </table>
 * 	    </div>
 *    </div>
 * </div>
 *  <h2>COMP 1001 - Exploring Multimedia and Internet Computing (3 units)</h2>
 *  <table class="sections" width="1012">
 *   <tbody>
 *    <tr>
 *        <th width="85">Section</th><th width="190" style="text-align: left">Date &amp; Time</th><th width="160" style="text-align: left">Room</th><th width="190" style="text-align: left">Instructor</th><th width="45">Quota</th><th width="45">Enrol</th><th width="45">Avail</th><th width="45">Wait</th><th width="81">Remarks</th>
 *    </tr>
 *    <tr class="newsect secteven">
 *        <td align="center">L1 (1765)</td>
 *        <td>We 02:00PM - 03:50PM</td><td>Rm 5620, Lift 31-32 (70)</td><td><a href="/wcq/cgi-bin/1830/instructor/LEUNG, Wai Ting">LEUNG, Wai Ting</a></td><td align="center">67</td><td align="center">0</td><td align="center">67</td><td align="center">0</td><td align="center">&nbsp;</td></tr><tr class="newsect sectodd">
 *        <td align="center">LA1 (1766)</td>
 *        <td>Tu 09:00AM - 10:50AM</td><td>Rm 4210, Lift 19 (67)</td><td><a href="/wcq/cgi-bin/1830/instructor/LEUNG, Wai Ting">LEUNG, Wai Ting</a></td><td align="center">67</td><td align="center">0</td><td align="center">67</td><td align="center">0</td><td align="center">&nbsp;</td>
 *    </tr>
 *   </tbody>
 *  </table>
 * </div>
 *}
 *</pre>
 * <br>
 * The code
 * <pre>
 * {@code
 * List<?> items = (List<?>) page.getByXPath("//div[@class='course']");
 * }
 * </pre>
 * extracts all result-row and stores the corresponding HTML elements to a list called items. Later in the loop it extracts the anchor tag
 * &lsaquo; a &rsaquo; to retrieve the display text (by .asText()) and the link (by .getHrefAttribute()).
 *
 *
 */

public class Scraper {
	private WebClient client;

	/**
	 * Default Constructor
	 */
	public Scraper() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setRedirectEnabled(false);
	}


	private void addSlot(HtmlElement e, Section se, boolean secondRow) {
		if(se == null) return;
		String times[] =  e.getChildNodes().get(secondRow ? 0 : 3).asText().split(" ");
		String venue = e.getChildNodes().get(secondRow ? 1 : 4).asText();
		String instructor = e.getChildNodes().get(secondRow ? 2 : 5).asText();
		if (times[0].equals("TBA"))
			return;
		for (int j = 0; j < times[0].length(); j+=2) {
			String code = times[0].substring(j , j + 2);
			if (Slot.DAYS_MAP.get(code) == null)
				break;
			Slot s = new Slot();
			s.setDay(Slot.DAYS_MAP.get(code));
			s.setStart(times[1]);
			s.setEnd(times[3]);
			s.setVenue(venue);
			s.setInstructor(instructor);
			se.addSlot(s);
		}
	}

	private void addSection(HtmlElement e, Course c) {
		String lecturesection =  e.getChildNodes().get(1).asText();
		if(lecturesection != null
				&& (lecturesection.contains("L") || lecturesection.contains("LA") || lecturesection.contains("T"))) {
			Section s = new Section();
			s.setLectureSection(lecturesection);
			c.addSection(s);
		}
	}

	public List<Course> scrape(String baseurl, String term, String sub) {

		try {

			HtmlPage page = client.getPage(baseurl + "/" + term + "/subject/" + sub);

			// items : store courses
			List<?> items = (List<?>) page.getByXPath("//div[@class='course']");

			Vector<Course> result = new Vector<Course>();

			for (int i = 0; i < items.size(); i++) {
				Course c = new Course();
				HtmlElement htmlItem = (HtmlElement) items.get(i);

				HtmlElement title = (HtmlElement) htmlItem.getFirstByXPath(".//h2");
				c.setTitle(title.asText());

				List<?> popupdetailslist = (List<?>) htmlItem.getByXPath(".//div[@class='popupdetail']/table/tbody/tr");
				HtmlElement exclusion = null, attributes = null;
				for ( HtmlElement e : (List<HtmlElement>)popupdetailslist) {
					HtmlElement t = (HtmlElement) e.getFirstByXPath(".//th");
					HtmlElement d = (HtmlElement) e.getFirstByXPath(".//td");
					if (t.asText().equals("EXCLUSION")) {
						exclusion = d;
					} else if(t.asText().equals("ATTRIBUTES")) {
						attributes = d;
					}
				}
				c.setExclusion((exclusion == null ? "null" : exclusion.asText()));
				c.setAttributes((attributes == null ? "null" : attributes.asText()));

				List<?> sections = (List<?>) htmlItem.getByXPath(".//tr[contains(@class,'newsect')]");

				for ( HtmlElement e: (List<HtmlElement>)sections) {
					// add sections in courses

					if (e != null && (e.getAttribute("class").contains("newsect secteven") || e.getAttribute("class").contains("newsect sectodd"))) {
						addSection(e, c);
						Section last_section = c.getlastSection();
						//System.out.println(last_section.getLectureSection());
						addSlot(e, last_section, false);
						//System.out.println(last_section.getNumSlots());

						e = (HtmlElement)e.getNextSibling();
						if (e != null && !e.getAttribute("class").contains("newsect")) {
							//System.out.print("check: ");
							//System.out.print(last_section.getLectureSection());

							addSlot(e, last_section, true);
							//System.out.println(last_section.getNumSlots());
						}

					}
				}

				result.add(c);
			}
			client.close();
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * This method will scrape the names of subjects in that term and return it as a String list.
	 * @param baseurl An URL from which this method will scrape information from
	 * @param term The term for which the subject names will be collected
	 * @return A list of String which contains the names of each subjects for that term
	 * @author cyim
	 */
	public List<String> scrapeAll(String baseurl, String term) {

		try {
			HtmlPage page = client.getPage(baseurl + "/" + term + "/" );

			// items : store courses
			List<?> itemsUG = (List<?>) page.getByXPath("//a[@class='ug']");
			List<?> itemsPG = (List<?>) page.getByXPath("//a[@class='pg']");

			List<String> allSubjects = new ArrayList<String>();

			ListIterator<?> itrUG = itemsUG.listIterator();
			ListIterator<?> itrPG = itemsPG.listIterator();

			while(itrUG.hasNext()) {
				String s = itrUG.next().toString();
				allSubjects.add(s.substring(s.length() - 7, s.length()-3));
			}

			while(itrPG.hasNext()) {
				String s = itrPG.next().toString();
				allSubjects.add(s.substring(s.length() - 7, s.length()-3));
			}

			Collections.sort(allSubjects);

			client.close();
			return allSubjects;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 *
	 * @param url  an absolute URL giving the base location of sfq information
	 * @return     the HashMap object whose key is instructor's name and value is SFQElement
	 * 			   containing average SFQ score and the number of included sections
	 * @exception  Exception if there is an error response from the remote server
	 */
	public Map<String, SFQElement> sfq_scrape(String url) {

		try {

			HtmlPage page = client.getPage(url);

			// items : store courses
			List<HtmlTable> tables = page.getByXPath("//table[@border='1']");
			Map<String, SFQElement> map = new HashMap<String, SFQElement>();

			for(HtmlTable table: tables) {
				if(table.getRow(0).getCells().size() != 8) continue;
				for(HtmlTableRow row : table.getRows()) {
					if(row.getCell(0).getTextContent().replace("\u00a0","").equals("") &&
							row.getCell(1).getTextContent().replace("\u00a0","").equals("")) {
						String instructor = row.getCell(2).getTextContent().replace("\u00a0","");
						String temp = row.getCell(4).getTextContent().replace("\u00a0","").split("\\(")[0];
						if(temp.equals("-") || instructor.equals("")) continue;

						double sfq_score = Double.parseDouble(temp);
						if(map.containsKey(instructor)) {
							map.get(instructor).addSection(sfq_score);
						} else {
							SFQElement element = new SFQElement(instructor);
							element.addSection(sfq_score);
							map.put(instructor, element);
						}
					}
				}
			}

			client.close();
			return map;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

	public Map<String, SFQElement> sfq_scrape_for_enrolled(String url) {

		try {

			HtmlPage page = client.getPage(url);

			// items : store courses
			List<HtmlTable> tables = page.getByXPath("//table[@border='1']");
			Map<String, SFQElement> map = new HashMap<String, SFQElement>();

			for(HtmlTable table: tables) {
				if(table.getRow(0).getCells().size() != 8) continue;
				String currentCourse = "";
				for(HtmlTableRow row : table.getRows()) {
					if(row.getCell(0).getColumnSpan() == 3) {
						currentCourse = row.getCell(0).getTextContent().replace("\u00a0"," ").trim();
					} else if (row.getCell(0).getTextContent().replace("\u00a0","").equals("") &&
							row.getCell(2).getTextContent().replace("\u00a0","").equals("")) {
						String temp = row.getCell(3).getTextContent().replace("\u00a0","").split("\\(")[0];
						if(temp.equals("-") || currentCourse.equals("")) continue;

						double sfq_score = Double.parseDouble(temp);

						if(map.containsKey(currentCourse)) {
							map.get(currentCourse).addSection(sfq_score);
						} else {
							SFQElement element = new SFQElement(currentCourse);
							element.addSection(sfq_score);
							map.put(currentCourse, element);
						}
					}
				}
			}

			client.close();
			return map;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
}