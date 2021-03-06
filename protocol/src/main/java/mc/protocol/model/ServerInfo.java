package mc.protocol.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import mc.protocol.model.text.Text;

import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter
@ToString
public class ServerInfo {

	private final Version version = new Version();
	private final Players players = new Players();

	private Text description;
	private String favicon;

	@Getter
	@Setter
	@ToString
	public static class Version {
		private String name;
		private int protocol;
	}

	@Getter
	@Setter
	@ToString
	public static class Players {
		private int max;
		private int online;
		private List<SamplePlayer> sample;
	}

	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	@ToString
	public static class SamplePlayer {
		private final String id;
		private final String name;
	}
}
