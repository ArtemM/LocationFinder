package com.locationfinder.data;

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
