package ie.tcd.dsg.surf.weatherforecast.utils;

public enum WeatherMeasurement {

	ALL("all"),
	TEMPERATURE("temperature"),
	HUMIDITY("humidity");

	private final String value;

	private WeatherMeasurement(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static WeatherMeasurement getByValue(String value) throws IllegalArgumentException {
		for (WeatherMeasurement weatherMeasurement : values()) {
			if (weatherMeasurement.getValue().equals(value)) {
				return weatherMeasurement;
			}
		}
		throw new IllegalArgumentException(value + " is not a valid weather measurement.");
	}
}
