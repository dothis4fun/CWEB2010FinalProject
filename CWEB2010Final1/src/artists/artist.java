package artists;

import java.util.ArrayList;
import java.util.List;

public class artist {
	private String name;
	private List<Song> songs = new ArrayList<>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the songs
	 */
	public List<Song> getSongs() {
		return songs;
	}

	/**
	 * @param songs
	 *            the songs to set
	 */
	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

	/**
	 * @param songs
	 *            the song to add
	 */
	public void addSong(Song songs) {
		this.songs.add(songs);
	}
}
