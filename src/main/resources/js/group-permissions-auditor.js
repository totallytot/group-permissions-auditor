AJS.toInit(function () {
    AJS.$(".multi-select").auiSelect2();

    //toggle behaviour
    var toggleMail = document.getElementById("outgoing-email");
    AJS.$(document).on('change', toggleMail, function () {
        dataObject.email = toggleMail.checked;
        if (toggleMail.checked === false) AJS.$("#email-receivers").hide();
        else AJS.$("#email-receivers").show();
    });
    var togglePermission = document.getElementById("permission-removal");
    AJS.$(document).on('change', togglePermission, function () {
        dataObject.permission = togglePermission.checked;
    });

    //stores configuration data
    var dataObject = {};
    dataObject.spacesToAdd = [];
    dataObject.spacesToDel = [];
    dataObject.groupsToAdd = [];
    dataObject.groupsToDel = [];
    dataObject.userNamesToAdd = [];
    dataObject.userNamesToDel = [];

    function cleanDataObject() {
        dataObject.spacesToAdd = [];
        dataObject.spacesToDel = [];
        dataObject.groupsToAdd = [];
        dataObject.groupsToDel = [];
        dataObject.userNamesToAdd = [];
        dataObject.userNamesToDel = [];
    }

    //populate data object
    AJS.$(document).on('change', '#ignored-spaces', function (e) {
        if (e.added) {
            console.log('add ' + e.added.id);
            if (dataObject.spacesToDel.includes(e.added.id))
                dataObject.spacesToDel.splice(dataObject.spacesToDel.indexOf(e.added.id), 1);
            else dataObject.spacesToAdd.push(e.added.id);
        }
        if (e.removed) {
            if (dataObject.spacesToAdd.includes(e.removed.id))
                dataObject.spacesToAdd.splice(dataObject.spacesToAdd.indexOf(e.removed.id), 1);
            else dataObject.spacesToDel.push(e.removed.id);
            console.log('remove ' + e.removed.id);
        }
    });

    AJS.$(document).on('change', '#monitored-groups', function (e) {
        if (e.added) {
            console.log('add ' + e.added.id);
            if (dataObject.groupsToDel.includes(e.added.id)) dataObject.groupsToDel.splice(dataObject.groupsToDel.indexOf(e.added.id), 1);
            else dataObject.groupsToAdd.push(e.added.id);

        }
        if (e.removed) {
            if (dataObject.groupsToAdd.includes(e.removed.id)) dataObject.groupsToAdd.splice(dataObject.groupsToAdd.indexOf(e.removed.id), 1);
            else dataObject.groupsToDel.push(e.removed.id);
            console.log('remove ' + e.removed.id);
        }
    });

    AJS.$(document).on('change', '#email-receivers', function (e) {
        if (e.added) {
            console.log('add ' + e.added.id);
            if (dataObject.userNamesToDel.includes(e.added.id)) dataObject.userNamesToDel.splice(dataObject.userNamesToDel.indexOf(e.added.id), 1);
            else dataObject.userNamesToAdd.push(e.added.id);

        }
        if (e.removed) {
            if (dataObject.userNamesToAdd.includes(e.removed.id)) dataObject.userNamesToAdd.splice(dataObject.userNamesToAdd.indexOf(e.removed.id), 1);
            else dataObject.userNamesToDel.push(e.removed.id);
            console.log('remove ' + e.removed.id);
        }
    });

    //AJAX and save button behaviour
    AJS.$("#save-button").click(function (e) {
        e.preventDefault();
        var ignoredSpaces = AJS.$("#ignored-spaces").val();
        var monitoredGroups = AJS.$("#monitored-groups").val();
        if (ignoredSpaces === null || ignoredSpaces.length === 0 || monitoredGroups === null || monitoredGroups.length === 0) {
            AJS.flag({
                type: 'error',
                body: 'Please select spaces to be monitored and affected groups!',
                close: "auto"
            });
        }
        else {
            AJS.$.ajax({
                url: AJS.contextPath() + '/plugins/servlet/groupaudit',
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(dataObject),
                success: function () {
                    console.log("SUCCESS");
                    console.log(JSON.stringify(dataObject));
                    AJS.flag({
                        type: 'success',
                        body: 'Audit Job has been configured.',
                        close: "auto"
                    });
                    cleanDataObject();
                },
                error: function (err) {
                    console.log("ERROR");
                    console.log(err);
                    AJS.flag({
                        type: 'error',
                        body: 'Something went wrong! Check logs!',
                        close: "auto"
                    });
                    cleanDataObject();
                }
            });
        }
    });

    //enable/disable button behaviour + state
    var stateBtn = AJS.$("#state-button");
    var stateInd = AJS.$("#state-indicator");
    stateBtn.click(function () {
        var action = stateBtn.text().toLowerCase();
        AJS.$.ajax({
            url: AJS.contextPath() + '/plugins/servlet/groupaudit',
            traditional: true,
            type: 'POST',
            data: action,
            success: function () {
                console.log("SUCCESS");
                if (action === "disable") {
                    stateBtn.text("Enable");
                    stateInd.removeClass('aui-lozenge aui-lozenge-success')
                        .addClass('aui-lozenge aui-lozenge-error').text("Disabled")
                } else if (action === "enable") {
                    stateBtn.text("Disable");
                    stateInd.removeClass('aui-lozenge aui-lozenge-error')
                        .addClass('aui-lozenge aui-lozenge-success').text("Scheduled")
                }
            },
            error: function (err) {
                console.log("ERROR");
                console.log(err);
                AJS.flag({
                    type: 'error',
                    body: 'Something went wrong! Check logs!',
                    close: "auto"
                });
            }
        });
    });

    //run now button behaviour
    var runBtn = AJS.$('#run-now-button');
    runBtn.click(function () {
        AJS.$.ajax({
            url: AJS.contextPath() + '/plugins/servlet/groupaudit',
            traditional: true,
            type: 'POST',
            data: 'run',
            success: function () {
                console.log("SUCCESS");
            },
            error: function (err) {
                console.log("ERROR");
                console.log(err);
                AJS.flag({
                    type: 'error',
                    body: 'Something went wrong! Check logs!',
                    close: "auto"
                });
            }
        });
    });
});