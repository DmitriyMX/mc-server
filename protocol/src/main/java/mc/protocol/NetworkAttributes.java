package mc.protocol;

import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class NetworkAttributes {

	public static final AttributeKey<State> STATE = AttributeKey.newInstance("STATE");

	/**
	 * @deprecated костыль
	 */
	@Deprecated
	public static final AttributeKey<Map<String, Object>> CUSTOM_PROPERTIES = AttributeKey.newInstance("CUSTOM_PROPERTIES");
}
