package test.doodlegrid;

import java.io.File;

import junit.framework.TestCase;
import com.doodlegrid.data.DataGrid;
import com.doodlegrid.translation.DataGridBinder;
import com.doodlegrid.translation.XmlUtils;

public class TestDataGridBinder extends TestCase {

	public void testDocumentToJava() {
		DataGridBinder dgb = new DataGridBinder();
		DataGrid dg = null;
		try {
			File file = new File("schema/DoodleGrid.xml");
			dg = (DataGrid) dgb.documentToJava(XmlUtils
					.getDocumentForFile(file));

			if (dg != null) {
				System.out.println("As String: " + dg.gridToStr());
				System.out.println("As XML: "
						+ XmlUtils.domToXMLString(dgb.javaToDocument(dg)));
			}
		} catch (Exception e) {
			fail("Exception: " + e);
		}

	}

	public void testJavaToDocument() {
		fail("Not yet implemented");
	}

}
