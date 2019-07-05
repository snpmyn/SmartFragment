package application;

import fragmentation.Fragmentation;

/**
 * Created on 2019/1/16.
 *
 * @author 郑少鹏
 * @desc Fragmentation初始化配置
 */
class FragmentationInitConfig {
    static void initFragmentation() {
        // 建于Application初始化
        // 更多查看wiki或demo
        Fragmentation.builder()
                // 栈视图模式默悬浮球模式
                // SHAKE摇一摇唤出、NONE隐藏
                // 仅Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                // 测试场景.debug(true)
                // 实际场景.debug(BuildConfig.DEBUG)
                .debug(true)
                // 可获{@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
                // 遇After onSaveInstanceState不抛异常而回调至下ExceptionHandler
                .handleException(e -> {
                    // 以Bugtags为例（传捕获Exception至Bugtags后台）
                    // Bugtags.sendException(e);
                })
                .install();
    }
}
