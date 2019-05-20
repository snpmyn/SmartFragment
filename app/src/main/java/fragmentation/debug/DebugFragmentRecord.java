package fragmentation.debug;

import java.util.List;

/**
 * @decs: 调试碎片记录
 * @author: 郑少鹏
 * @date: 2019/5/20 9:24
 */
public class DebugFragmentRecord {
    public CharSequence fragmentName;
    public List<DebugFragmentRecord> childFragmentRecord;

    public DebugFragmentRecord(CharSequence fragmentName, List<DebugFragmentRecord> childFragmentRecord) {
        this.fragmentName = fragmentName;
        this.childFragmentRecord = childFragmentRecord;
    }
}
