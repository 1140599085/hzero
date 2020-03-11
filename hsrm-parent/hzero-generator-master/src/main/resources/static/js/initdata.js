layui.use(['tree', 'util', 'upload', 'form'], function () {
    var tree = layui.tree
        , layer = layui.layer
        , util = layui.util
        , upload = layui.upload
        , form = layui.form

    var env = 'dev';
    var version = 'saas'
    var dataDir = '';
    var groovyDir = '';

    getServiceList = function () {
        $.ajax({
            type: "GET",
            url: "sys/export-datas/services?env=" + env + "&version=" + version,
            contentType: "application/json",
            success: function (data) {
                //实例调用
                tree.render({
                    elem: '#data'
                    , showCheckbox: true
                    , id: 'demoId'
                    , data: data
                })
            }
        });
    };
    window.onload = function () {
        getServiceList()
    };
    $(document).ready(function () {
        layui.use('form', function () {
            var form = layui.form;
            form.render();
            form.on('select(env)', function (data) {
                env = data.value;
                getServiceList();
            });
            form.on('select(version)', function (data) {
                version = data.value;
                getServiceList();
            });
        })
    });
    //按钮事件
    util.event('lay-demo', {
        exportInitData: function (othis) {
            var serviceDTO = tree.getChecked('demoId'); //获取选中节点的数据
            if (serviceDTO.length == 0) {
                layer.msg('请先选择需要导出的数据', {
                    time: 2000, //2s后自动关闭
                });
                return true;
            }

            var loading = layer.msg("打包中，请等待。。。", {
                icon: 16
                , shade: 0.01
                , time: 6 * 60 * 1000
            })
            var req = new XMLHttpRequest();
            req.open('POST', 'sys/export-datas/export?env=' + env, true);
            req.responseType = 'blob';
            req.setRequestHeader('Content-Type', 'application/json');
            req.onload = function () {
                var data = req.response;
                var a = document.createElement('a');
                var blob = new Blob([data]);
                var blobUrl = window.URL.createObjectURL(blob);
                a.style.display = 'none';
                a.download = 'ExportData' + new Date().getTime() + '.zip';
                a.href = blobUrl;
                a.click();
                layer.close(loading)
            };
            req.send(JSON.stringify(serviceDTO));
        }
    });
    // 获取目录下数据列表
    getInitData = function () {
        dataDir = document.getElementById("initData").value;
        if (dataDir.length == 0) {
            layer.msg('先输入文件存放路径', {
                time: 2000,
            })
            return true;
        }
        $.ajax({
            type: "GET",
            url: "sys/import-datas/data-services?dir=" + encodeURI(dataDir),
            contentType: "text/plain",
            success: function (data) {
                if (data.length == 0) {
                    layer.msg('数据不存在', {
                        time: 2000, //2s后自动关闭
                    });
                }
                $("#import").empty();
                form.render();
                for (var i=0;i<data.length; i++) {
                    $("#import").append("<input type='checkbox' lay-skin='primary' name='" + data[i].name + "' title='" + data[i].description + "(" + data[i].schema + ")'>")
                }
                form.render();
            }
        });
    };
    // 获取目录下groovy脚本列表
    getGroovy = function () {
        groovyDir = document.getElementById("groovy").value;
        if (groovyDir.length == 0) {
            layer.msg('先输入文件存放路径', {
                time: 2000,
            })
            return true;
        }
        $.ajax({
            type: "GET",
            url: "sys/import-datas/groovy-services?dir=" + encodeURI(groovyDir),
            contentType: "text/plain",
            success: function (data) {
                if (data.length == 0) {
                    layer.msg('数据不存在', {
                        time: 2000, //2s后自动关闭
                    });
                }
                $("#groovyData").empty();
                form.render();
                for (var i=0;i<data.length; i++) {
                    $("#groovyData").append("<input type='checkbox' lay-skin='primary' name='" + data[i].schema + "' title='" + data[i].description + "(" + data[i].schema + ")'>")
                }
                form.render();
            }
        });
    };
    importInitData = function () {
        var services = [];
        $("#import").children("input:checked").map(function () {
            services.push(this.name);
        })
        if (services.length == 0) {
            layer.msg('请先选择需要导入的数据', {
                time: 2000, //2s后自动关闭
            });
            return true;
        };
        //询问框
        layer.confirm('是否确认环境选择正确和备份好了原数据？', {
            btn: ['确定','取消'] //按钮
        }, function(){
            var loading = layer.msg("导入中，请等待。。。", {
                icon: 16
                , shade: 0.01
                , time: 60 * 60 * 1000
            })
            $.ajax({
                type: "POST",
                url: "sys/import-datas/init-data?dir=" + encodeURI(dataDir) + "&env=" + env,
                data: JSON.stringify(services),
                contentType: "application/json",
                success: function (data) {
                    layer.close(loading)
                    layer.msg('导入数据完成', {
                        icon: 1,
                        time: 2000, //2s后自动关闭
                    });
                }
            });
        }, function(){
            layer.msg('这就去操作', {
                icon: 6,
                time: 2000, //2s后自动关闭
            });
        });

    };
    updateGroovy = function () {
        var services = [];
        $("#groovyData").children("input:checked").map(function () {
            services.push(this.name);
        })
        if (services.length == 0) {
            layer.msg('请先选择需要更新的数据', {
                time: 2000, //2s后自动关闭
            });
            return true;
        }
        var loading = layer.msg("更新中，请等待。。。", {
            icon: 16
            , shade: 0.01
            , time: 60 * 60 * 1000
        })
        $.ajax({
            type: "POST",
            url: "sys/import-datas/update-groovy?dir=" + encodeURI(groovyDir) + "&env=" + env,
            data: JSON.stringify(services),
            contentType: "application/json",
            success: function (data) {
                layer.close(loading)
                layer.msg('更新数据库完成', {
                    icon: 1,
                    time: 2000, //2s后自动关闭
                });
            }
        });
    };
});

