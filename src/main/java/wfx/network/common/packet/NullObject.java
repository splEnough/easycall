package wfx.network.common.packet;

import java.io.Serializable;

/**
 * RPC调用传递null参数时，默认为当前的参数填充一个NullObject的值，在接收端进行特殊的处理
 * @author 翁富鑫 2019/3/9 16:36
 */
public class NullObject implements Serializable {
}
