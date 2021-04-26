package mc.protocol;

import io.netty.util.AttributeKey;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NetworkAttributes {

	public static final AttributeKey<State> STATE = AttributeKey.newInstance("STATE");
}
