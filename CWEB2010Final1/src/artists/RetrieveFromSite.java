package artists;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.scene.control.Button;

public class RetrieveFromSite {

	/** Retrieves songs (up to 500) from the website for entered artist
	 * @param artistName name of the artist to search for
	 * @param timeFrame time frame to initiate the search against
	 * @return an artist with a list of tracks */
	public static artist getSongs(String artistName, String timeFrame) {
		ArrayList<Button> buttonList = new ArrayList<>();
		Button tempButton = new Button();
		Song currentSong;
		artist currentArtist = new artist();
		try {
			Document doc = Jsoup.connect(
					"https://www.last.fm/music/" + artistName + "/+tracks?date_preset=" + timeFrame.toUpperCase())
					.get();
			currentArtist.setName(doc.select("h1.header-title").first().child(0).text());
			Element totalPages = doc.select("ul.pagination-list").first();
			int pages = Integer.parseInt(totalPages.select("li.pagination-page").last().child(0).text());
			for (int x = 1; x <= pages; ++x) {
				if (x != 1) {
					doc = Jsoup.connect("https://www.last.fm/music/" + artistName + "/+tracks?date_preset="
							+ timeFrame.toUpperCase() + "&page=" + (x)).get();
				}
				Elements songInfo = doc.select("tbody").first().children();
				for (Element row : songInfo) {
					if (!(row.hasClass("chartlist-row--interlist-ad"))) {
						if (!row.child(1).children().isEmpty()) {

							currentSong = new Song(Integer.parseInt(row.child(0).text()), row.child(3).child(0).text(),
									row.child(1).child(0).attr("href"), tempButton, currentArtist.getName());
							buttonList.add(tempButton);
							tempButton = new Button();
						} else {
							currentSong = new Song(Integer.parseInt(row.child(0).text()), row.child(3).child(0).text(),
									currentArtist.getName());
						}
						currentArtist.addSong(currentSong);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// System.out.println(e.getStackTrace());
			// System.out.println("failed");
		}
		return currentArtist;
	}
}
