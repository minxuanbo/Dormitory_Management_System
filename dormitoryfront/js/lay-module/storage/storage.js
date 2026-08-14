/**
 * author: LindaSilk
 * date: 2021年3月10日, 周三
 * description: 对存取localStorage数据的简单封装
 */
layui.define([], function (exports) {
    exports("storage", storage = {
        setToken:function (token){
            localStorage.setItem("token", token);
        },
        getToken:function (){
            return localStorage.getItem("token");
        },
        set:function (key, value){
            if (typeof value === 'object') {
                value = JSON.stringify(value);
            }
            localStorage.setItem(key, value);
        },
        get:function (key){
            var value = localStorage.getItem(key);
            if (value === null) {
                return null;
            }
            try {
                return JSON.parse(value);
            } catch (e) {
                return value;
            }
        },
        clear:function () {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
        }
    });
});
