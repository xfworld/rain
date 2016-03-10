/*初始化树区*/
$(function () {
    $('#tt').tree({
        lines: true,
        animate: true,
        url: '/model/list',
        formatter: function (node) {
            return node.text.split('@')[0];
        },
        onClick: function (node) {
            /*  $.post('/model/url', {id: node.id}, function (data) {
             if (data != "" && data != null && data != 0) {
             }
             });*/
            var data = node.text.split('@')[1];
            if (data != "" && data != null && data != 0) {
                open1(node.text.split('@')[0], data);
            }
        }
    });
});
//打开节点内容
function open1(name, url) {
    if ($('#ttx').tabs('exists', name)) {
        $('#ttx').tabs('select', name);
    } else {
        var content = '<iframe scrolling="no" frameborder="0"' +
            'src="' + url + '" style="width:100%;height:100%;"></iframe>';
        $('#ttx').tabs('add', {
            id: name,
            title: name,
            content: content,
//            href: url,
            closable: true,
            tools: [
                {
                    iconCls: 'icon-mini-refresh',
                    handler: function () {
                        var tab = $('#ttx').tabs('getSelected');
                        $("#ttx").tabs('update', {
                            tab: tab,
                            options: {
                                content: content
                            }
                        });
                        tab.panel('refresh');
                    }
                }
            ]
        });
    }
}
/*   function doExpand(){
 $('#tt').tree('expandAll');
 }

 function doCollapse(){
 $('#tt').tree('collapseAll');
 }*/

/*监控项列表 监听行点击事件*/
var nowItem = "";
$(function () {
    $("#monitor_item").datagrid({
        onClickRow: function (index, row) {
            nowItem = row.id;
            $("#remind_grid").datagrid({url: '/server/item/remind/list?itemId=' + nowItem}).datagrid("reload");
            $("#item_setting_grid").datagrid({url: '/server/item/response/list?itemId=' + nowItem}).datagrid("reload");
        }
    });
});

/****************************** 属性 format 区 ***********************************/


var abnLevelFormat = function (value, row) {
    var abnLevel = {"1": "健康", "2": "信息", "3": "警告", "4": "错误"};
    return abnLevel[value];
};

var attrStatusFormat = function (value, row) {
    if (value == "1") {
        return "健康";
    }
    else {
        return "异常";
    }
};

var jobStatusFormat = function (value, row) {
    if (row.jobStatusName == "运行") {
        return "<span style='color:green;font-family:Monaco'>" + row.jobStatusName + "</span>";
    } else {
        return row.jobStatusName;
    }
};

var itemUrlFormat = function (value, row) {
    return "<a href='" + value + "' title='" + row.itemNameCh + "' class='easyui-tooltip' target='_blank'>" + value + "</a>";
};


/****************************** 分割线 按钮事件区 ***********************************/

var itemFormUrl = "";

var newItem = function () {
    itemFormUrl = "/server/item/add";
    $("#add_server_item").dialog("open").dialog("setTitle", "增加一个服务监控项");
    return false;
};

var pSaveItem = function () {
    $("#form_server_item").form("submit",
        {
            url: itemFormUrl,
            onSubmit: function () {
                return $(this).form("validate");
            },
            success: function (result) {
                result = eval('(' + result + ')');
                alert(result.message);
                if (result.success) {
                    $("#add_server_item").dialog("close");
                    $("#monitor_item").datagrid("reload");
                }
            }
        });
    return false;
};

var pCancelSaveItem = function () {
    $("#add_server_item").dialog("close");
    return false;
};

var updateItem = function () {
    var row = $("#monitor_item").datagrid("getSelected"); //Return the first selected row record or null.
    itemFormUrl = "/server/item/update";
    $("#form_server_item").form("load", row);
    $("#add_server_item").dialog("open").dialog("setTitle", "修改服务监控");
    return false;
};

var deleteItem = function () {
    var row = $("#monitor_item").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (confirm("确定要删除监控：[" + row.itemName + "]？")) {
        var _url = "/server/item/delete";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#monitor_item").datagrid("reload");
            }
        }, "json");
    }
    return false;
};

var searchItem = function () {
    var _serverName = $("#item_name").val();
    var _serverUrl = $("#item_url").val();
    //装入数据
    $("#monitor_item").datagrid({
        url: "/server/item/list?itemName="
        + _serverName + "&itemUrl=" + _serverUrl
    }).datagrid("reload");
    return false;
};


var searchUser = function () {
    var _role = $("input[name='roles']").val();
    var _start = $("input[name='start']").val();
    //装入数据
    $("#user_grid").datagrid({
        queryParams: {
            roles: _role,
            createTime: _start
        }
    }).datagrid("reload");
    return false;
};

var startJob = function () {
    var row = $("#monitor_item").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (row.jobStatus == "1") {
        alert("监控已经是开启状态了。");
        return false;
    }
    if (confirm("确定要开启监控：[" + row.itemName + "]的job？")) {
        var _url = "/server/item/job/start";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#monitor_item").datagrid("reload");
            }
        }, "json");
    }
    return false;
};

var pauseJob = function () {
    var row = $("#monitor_item").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (row.jobStatus == "0") {
        alert("监控已经是暂停状态了。");
        return false;
    }
    if (confirm("确定要关闭监控：[" + row.itemName + "]的job？")) {
        var _url = "/server/item/job/pause";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#monitor_item").datagrid("reload");
            }
        }, "json");
    }
    return false;
};

var newUser = function () {
    itemFormUrl = "/user/add";
    $("#input_username").removeAttr("readonly").attr("required", "true");
    $("#add_user_dlg").dialog("open").dialog("setTitle", "增加用户");

    $.get('/user/update', function (result) {
        if (result.success) {

            $("#roles").empty();
            $.each(result.allroles, function (index, field) {
                $("#roles").append('<input id="roleList' + index + '" name="roles" type="checkbox" value="' + field.id + '">' + field.name);


            });
        }
    }, "json");

    return false;
};

var pSaveUser = function () {
    $("#user_form").form("submit", {
        url: itemFormUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            $.messager.alert('提示', result.message, 'info');
            if (result.success) {
                $("#add_user_dlg").dialog("close");
                $("#user_grid").datagrid("reload");
            }
        }
    });
    return false;
};

var pCancelSaveUser = function () {
    $("#add_user_dlg").dialog("close");
    return false;
};

var updateUser = function () {
    var row = $("#user_grid").datagrid("getSelected");
    itemFormUrl = "/user/update";
    $("#input_username").removeAttr("required").attr("readonly", "true");
    $("#user_form").form("load", row);
    $("#add_user_dlg").dialog("open").dialog("setTitle", "修改用户信息");

    $.get(itemFormUrl, {id: row.id}, function (result) {
        if (result.success) {
            var temp = [];
            $.each(result.uroles, function (index, field) {
                if (field) {
                    temp.push(field.name);
                }
            });
            $("#roles").empty();
            $.each(result.allroles, function (index, field) {
                var val = 0;
                var checked = "";
                if ($.inArray(field.name, temp) > -1) {
                    val = 1;
                    checked = 'checked="checked"';
                }
                $("#roles").append('<input id="roleList' + index + '" name="roles" type="checkbox" value="' + field.id + '" ' + checked + '>' + field.name);


            });
        }
    }, "json");

    return false;
};

var deleteUser = function () {
    var row = $("#user_grid").datagrid("getSelected");
    if (row == undefined) {
        $.messager.alert('警告', "请选择一行后删除。", 'warning');
        return false;
    }
    $.messager.confirm('确认框', "确定要删除：[" + row.chName + "]？", function (r) {
        if (r) {
            var _url = "/user/delete";
            $.post(_url, {id: row.id}, function (result) {
                $.messager.alert(result.message, 'info');
                if (result.success) {
                    $("#user_grid").datagrid("reload");
                }
            }, "json");
        }
    });
    return false;
};


var newRole = function () {
    itemFormUrl = "/role/add";
    $("#input_rolename").removeAttr("readonly").attr("required", "true");
    $("#add_role_dlg").dialog("open").dialog("setTitle", "增加用户");
    return false;
};

var pSaveRole = function () {
    $("#role_form").form("submit", {
        url: itemFormUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            $.messager.alert('提示', result.message, 'info');
            if (result.success) {
                $("#add_role_dlg").dialog("close");
                $("#role_grid").datagrid("reload");
            }
        }
    });
    return false;
};

var pCancelSaveRole = function () {
    $("#add_role_dlg").dialog("close");
    return false;
};

var updateRole = function () {
    var row = $("#role_grid").datagrid("getSelected");
    itemFormUrl = "/role/update";
    $("#input_rolename").removeAttr("readonly");
    $("#role_form").form("load", row);
    $("#add_role_dlg").dialog("open").dialog("setTitle", "修改角色信息");

    $.get(itemFormUrl, {id: row.id}, function (result) {
        if (result.success) {
            var temp = [];
            $.each(result.rusers, function (index, field) {
                if (field) {
                    temp.push(field.username);
                }
            });
            $("#users").empty();
            $.each(result.allusers, function (index, field) {
                var val = 0;
                var checked = "";
                if ($.inArray(field.username, temp) > -1) {
                    val = 1;
                    checked = 'checked="checked"';
                }
                $("#users").append('<input id="userList' + index + '" name="users" type="checkbox" value="' + field.id + '" ' + checked + '>' + field.chName);

            });
        }
    }, "json");

    return false;
};

var deleteRole = function () {
    var row = $("#role_grid").datagrid("getSelected");
    if (row == undefined) {
        $.messager.alert('警告', "请选择一行后删除。", 'warning');
        return false;
    }
    $.messager.confirm('确认框', "确定要删除：[" + row.chName + "]？", function (r) {
        if (r) {
            var _url = "/role/delete";
            $.post(_url, {id: row.id}, function (result) {
                $.messager.alert(result.message, 'info');
                if (result.success) {
                    $("#role_grid").datagrid("reload");
                }
            }, "json");
        }
    });
    return false;
};

//model tree的增删改查

var newModel = function () {
    itemFormUrl = "/model/add";
    $("#input_text").removeAttr("readonly").attr("required", "true");
    $("#add_model_dlg").dialog("open").dialog("setTitle", "增加功能tree");
    //取出 parentid  选择框
    $.get('/model/selectType', function (result) {
        if (result.success) {
            $("#parentId").empty();
            $.each(result.allparent, function (index, field) {
                if (field.url == "0" || field.url == "" || field.url == null) {
                    $("#parentId").append('<option value="' + field.id + '">' + field.text + '</option>');
                }
            });
            $("#parentId").append('<option value="0"><span style="color:red">根目录</span></option>');
        }
    }, "json");
    return false;
};


//select onChange
function onSelect() {
    if ($('#parentId').val() == 0) {
        $("#input_url").val(0); //默认为0 根目录 不打开链接
        $("#input_url").attr("readonly", "true");
    } else {
        var row = $("#model_grid").datagrid("getSelected");
        $("#input_url").val(row.url);
        $("#input_url").removeAttr("readonly").attr("required", "true");
    }
    return false;
}
var pSaveModel = function () {
    $("#model_form").form("submit", {
        url: itemFormUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            $.messager.alert('提示', result.message, 'info');
            if (result.success) {
                $("#add_model_dlg").dialog("close");
                $("#model_grid").datagrid("reload");
            }
        }
    });
    return false;
};

var pCancelSaveModel = function () {
    $("#add_model_dlg").dialog("close");
    return false;
};

var updateModel = function () {
    var row = $("#model_grid").datagrid("getSelected");
    if (row == undefined) {
        $.messager.alert('警告', "请选择一行进行修改。", 'warning');
        return false;
    }
    itemFormUrl = "/model/update";
    $("#add_model_dlg").dialog("open").dialog("setTitle", "修改功能信息");

    //取出 parentid  选择框
    $.get('/model/selectType', function (result) {
        if (result.success) {

            $("#parentId").empty();
            $.each(result.allparent, function (index, field) {
                if (field.url == "0" || field.url == "" || field.url == null) {
                    $("#parentId").append('<option value="' + field.id + '">' + field.text + '</option>');
                }
            });
            $("#parentId").append('<option value="0">根目录</option>');
        }
        $("#model_form").form("load", row);
    }, "json");
    return false;
};

var deleteModel = function () {
    var row = $("#model_grid").datagrid("getSelected");
    if (row == undefined) {
        $.messager.alert('警告', "请选择一行后删除。", 'warning');
        return false;
    }
    $.messager.confirm('确认框', "确定要删除：[" + row.text + "]？", function (r) {
        if (r) {
            var _url = "/model/delete";
            $.post(_url, {id: row.id}, function (result) {
                $.messager.alert(result.message, '删除成功。');
                if (result.success) {
                    $("#model_grid").datagrid("reload");
                }
            }, "json");
        }
    });
    return false;
};

/*详细监控信息 操作按钮事件*/
var itemSettingUrl = "";
var addSettingAttr = function () {
    var row = $("#monitor_item").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一个监控项为期添加监控属性。");
        return false;
    }
    itemSettingUrl = "/server/item/response/add";
    $("#setting_item_type").val(row.typeCode);
    $("#setting_item_id").val(nowItem);
    $("#item_setting_dialog").dialog("open").dialog("setTitle", "增加一个监控属性");
    return false;
};
var modifySettingAttr = function () {
    var row = $("#item_setting_grid").datagrid("getSelected");
    itemSettingUrl = "/server/item/response/update";
    $("#item_setting_form").form("load", row);
    $("#item_setting_dialog").dialog("open").dialog("setTitle", "修改监控属性");
    return false;
};
var deleteSettingAttr = function () {
    var row = $("#item_setting_grid").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (confirm("确定要删除监控属性：" + row.attributeName + "？")) {
        var _url = "/server/item/response/delete";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#item_setting_grid").datagrid("reload");
            }
        }, "json");
    }
    return false;
};
var saveSettingAttr = function () {
    $("#item_setting_form").form("submit", {
        url: itemSettingUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            alert(result.message);
            if (result.success) {
                $("#item_setting_dialog").dialog("close");
                $("#item_setting_grid").datagrid("reload");
            }
        }
    });
    return false;
};
var cancelSaveSettingAttr = function () {
    $("#item_setting_dialog").dialog("close");
    return false;
};

/******************************提醒列表的代码****************************/

var remindFormUrl = "";
var addRemind = function () {
    remindFormUrl = "/data/item/remind/add";
    $("#item_id").val(nowSelectItemId);
    $("#input_remind_user").removeAttr("readonly");
    $("#remind_dialog").dialog("open").dialog("setTitle", "增加一个提醒");
    return false;
};
var modifyRemind = function () {
    var row = $("#remind_grid").datagrid("getSelected");
    remindFormUrl = "/data/item/remind/update";
    $("#input_remind_user").attr("readonly", "true");
    $("#remind_form").form("load", row);
    $("#remind_dialog").dialog("open").dialog("setTitle", "修改提醒");
    return false;
};
var deleteRemind = function () {
    var row = $("#remind_grid").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (confirm("确定要删除提醒：" + row.typeName + "--提醒--[" + row.chName + "]？")) {
        var _url = "/data/item/remind/delete";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#remind_grid").datagrid("reload");
            }
        }, "json");
    }
    return false;
};
var saveRemind = function () {
    $("#remind_form").form("submit", {
        url: remindFormUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            alert(result.message);
            if (result.success) {
                $("#remind_dialog").dialog("close");
                $("#remind_grid").datagrid("reload");
            }
        }
    });
    return false;
};


/*提醒操作按钮*/
var cancelSaveRemind = function () {
    $("#remind_dialog").dialog("close");
    return false;
};
var remindFormUrl = "";
var addRemind = function () {
    remindFormUrl = "/server/item/remind/add";
    $("#item_id").val(nowItem);
    $("#input_remind_user").removeAttr("readonly");
    $("#remind_dialog").dialog("open").dialog("setTitle", "增加一个提醒");
    return false;
};
var modifyRemind = function () {
    var row = $("#remind_grid").datagrid("getSelected");
    remindFormUrl = "/server/item/remind/update";
    $("#input_remind_user").combo("readonly");
    $("#remind_form").form("load", row);
    $("#remind_dialog").dialog("open").dialog("setTitle", "修改提醒");
    return false;
};
var deleteRemind = function () {
    var row = $("#remind_grid").datagrid("getSelected");
    if (row == undefined) {
        alert("请选择一行后删除。");
        return false;
    }
    if (confirm("确定要删除提醒：" + row.typeName + "--提醒--[" + row.chName + "]？")) {
        var _url = "/server/item/remind/delete";
        $.post(_url, {id: row.id}, function (result) {
            alert(result.message);
            if (result.success) {
                $("#remind_grid").datagrid("reload");
            }
        }, "json");
    }
    return false;
};
var saveRemind = function () {
    $("#remind_form").form("submit", {
        url: remindFormUrl, onSubmit: function () {
            return $(this).form("validate");
        },
        success: function (result) {
            result = eval('(' + result + ')');
            alert(result.message);
            if (result.success) {
                $("#remind_dialog").dialog("close");
                $("#remind_grid").datagrid("reload");
            }
        }
    });
    return false;
};
var cancelSaveRemind = function () {
    $("#remind_dialog").dialog("close");
    return false;
};


