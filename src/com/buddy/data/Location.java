package com.buddy.data;

/**
 * This class is used to hold locations which was received from server
 */
public class Location {

	private String name;
	private String city;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns simple representation of taken location to be represented in the
	 * locations list.
	 * 
	 * @return <tt>String</tt> representing location suitable for list. If no
	 *         name specified returns empty <tt>String</tt>
	 */
	@Override
	public String toString() {
		if (name != null && !"".equals(name.trim())) {
			if (city != null && !"".equals(city.trim())) {
				return name + " (" + city + ")";
			} else {
				return name;
			}
		}
		return "";
	}

}
