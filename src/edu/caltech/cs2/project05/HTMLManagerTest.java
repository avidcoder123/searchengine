package edu.caltech.cs2.project05;


public class HTMLManagerTest {
	public static void main(String[] args) {
		// <b>Hi</b><br/>
		Queue<HTMLTag> tags = new Queue<>();
		tags.enqueue(new HTMLTag("b", HTMLTagType.OPENING));        // <b>
		tags.enqueue(new HTMLTag("b", HTMLTagType.CLOSING));        // </b>
		tags.enqueue(new HTMLTag("br", HTMLTagType.SELF_CLOSING));  // <br/>
		
		HTMLManager manager = new HTMLManager(tags);

		// YOUR TESTS GO HERE
	}
}
