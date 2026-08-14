/**
 * date:2019/08/16
 * author:Mr.Chung
 * description:此处放layui自定义扩展
 */

window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

layui.config({
    base: rootPath + "lay-module/",
    version: true
}).extend({
    miniAdmin: "layuimini/miniAdmin", // layuimini后台扩展
    miniMenu: "layuimini/miniMenu", // layuimini菜单扩展
    miniPage: "layuimini/miniPage", // layuimini 单页扩展
    miniTheme: "layuimini/miniTheme", // layuimini 主题扩展
    miniTongji: "layuimini/miniTongji", // layuimini 统计扩展
    step: 'step-lay/step', // 分步表单扩展
    treetable: 'treetable-lay/treetable', //table树形扩展
    tableSelect: 'tableSelect/tableSelect', // table选择扩展
    iconPickerFa: 'iconPicker/iconPickerFa', // fa图标选择扩展
    echartsTheme: 'echarts/echartsTheme', // echarts图表主题扩展
    wangEditor: 'wangEditor/wangEditor', // wangEditor富文本扩展
    layarea: 'layarea/layarea', //  省市县区三级联动下拉选择器

    axios: 'axios/axios',           // axios扩展
    storage: 'storage/storage',     // 对存取localStorage数据的简单封装

    //echarts: 'echarts/echarts', // echarts图表扩展
    echarts: 'echarts-silk/echarts', // silk添加的echarts图表扩展

});

// ============================================================
// 全站 API 接口基准路径
// 注意：axios 模块已配置 baseURL=/dormitory，layui table 底层也改用 axios，
// upload.js 亦自行拼接 /dormitory/ 前缀；因此此处必须留空，
// 否则 table/upload 请求会出现 /dormitory/dormitory/... 双重前缀导致 404。
// ============================================================
var API_BASE = '';

// ============================================================
// 为所有 jQuery 请求（layui table、layui upload、miniPage 等）
// 统一追加 token 请求头，并在响应后刷新本地 token，
// 解决 layui 表格"数据接口请求异常"的问题。
// ============================================================
layui.use(['jquery', 'storage'], function () {
    var $ = layui.jquery;
    var storage = layui.storage;
    if ($ && $.ajaxSetup) {
        $.ajaxSetup({
            beforeSend: function (xhr) {
                try {
                    var token = storage.getToken();
                    if (token) {
                        xhr.setRequestHeader('token', token);
                    }
                } catch (e) {
                }
            },
            complete: function (xhr) {
                try {
                    var newToken = xhr.getResponseHeader('token');
                    if (newToken) {
                        storage.setToken(newToken);
                    }
                } catch (e) {
                }
            }
        });
    }
});