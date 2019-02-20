AJS.toInit(function () {
    AJS.$(".multi-select").auiSelect2();

    //toggle behaviour and cron
    var toggleMail = document.getElementById("outgoing-email");
    AJS.$(document).on("change", toggleMail, function () {
        dataObject.email = toggleMail.checked;
        if (toggleMail.checked === false) AJS.$("#email-receivers").hide();
        else AJS.$("#email-receivers").show();
    });
    var togglePermission = document.getElementById("permission-removal");
    AJS.$(document).on("change", togglePermission, function () {
        dataObject.permission = togglePermission.checked;
    });
    var cron = AJS.$("#job-interval");
    AJS.$(document).on("change", cron, function () {
        dataObject.cron = cron.val();
    });

    //stores configuration data
    var dataObject = {};
    dataObject.spacesToAdd = [];
    dataObject.spacesToDel = [];
    dataObject.groupsToAdd = [];
    dataObject.groupsToDel = [];
    dataObject.userNamesToAdd = [];
    dataObject.userNamesToDel = [];

    //populate data object
    AJS.$(document).on("change", "#ignored-spaces", function (e) {
        if (e.added) {
            console.log("add " + e.added.id);
            if (dataObject.spacesToDel.includes(e.added.id))
                dataObject.spacesToDel.splice(dataObject.spacesToDel.indexOf(e.added.id), 1);
            else dataObject.spacesToAdd.push(e.added.id);
        }
        if (e.removed) {
            if (dataObject.spacesToAdd.includes(e.removed.id))
                dataObject.spacesToAdd.splice(dataObject.spacesToAdd.indexOf(e.removed.id), 1);
            else dataObject.spacesToDel.push(e.removed.id);
            console.log("remove " + e.removed.id);
        }
    });
    AJS.$(document).on("change", "#monitored-groups", function (e) {
        if (e.added) {
            console.log("add " + e.added.id);
            if (dataObject.groupsToDel.includes(e.added.id)) dataObject.groupsToDel.splice(dataObject.groupsToDel.indexOf(e.added.id), 1);
            else dataObject.groupsToAdd.push(e.added.id);

        }
        if (e.removed) {
            if (dataObject.groupsToAdd.includes(e.removed.id)) dataObject.groupsToAdd.splice(dataObject.groupsToAdd.indexOf(e.removed.id), 1);
            else dataObject.groupsToDel.push(e.removed.id);
            console.log("remove " + e.removed.id);
        }
    });
    AJS.$(document).on("change", "#email-receivers", function (e) {
        if (e.added) {
            console.log("add " + e.added.id);
            if (dataObject.userNamesToDel.includes(e.added.id)) dataObject.userNamesToDel.splice(dataObject.userNamesToDel.indexOf(e.added.id), 1);
            else dataObject.userNamesToAdd.push(e.added.id);

        }
        if (e.removed) {
            if (dataObject.userNamesToAdd.includes(e.removed.id)) dataObject.userNamesToAdd.splice(dataObject.userNamesToAdd.indexOf(e.removed.id), 1);
            else dataObject.userNamesToDel.push(e.removed.id);
            console.log("remove " + e.removed.id);
        }
    });

    //save button behaviour
    AJS.$("#save-button").click(function (e) {
        e.preventDefault();
        var ignoredSpaces = AJS.$("#ignored-spaces").val();
        var monitoredGroups = AJS.$("#monitored-groups").val();
        if (ignoredSpaces === null || ignoredSpaces.length === 0 || monitoredGroups === null || monitoredGroups.length === 0) {
            errorFlag('Please select spaces to be monitored and affected groups!')
        }
        else {
            AJS.$.ajax({
                url: AJS.contextPath() + "/plugins/servlet/groupaudit",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(dataObject),
                success: function (responseJson) {
                    console.log("SUCCESS");
                    console.log(JSON.stringify(dataObject));
                    console.log(responseJson);
                    AJS.$("#job-info > tbody > tr:nth-child(3) > td:nth-child(2)").text(responseJson.date.cron);
                    AJS.flag({
                        type: "success",
                        body: "Audit Job has been configured.",
                        close: "auto"
                    });
                    cleanDataObject();
                },
                error: function (err) {
                    console.log("ERROR");
                    console.log(err);
                    errorFlag("Something went wrong! Check logs!");
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
            url: AJS.contextPath() + "/plugins/servlet/groupaudit",
            traditional: true,
            type: "POST",
            data: action,
            success: function () {
                console.log("SUCCESS");
                if (action === "disable") {
                    stateBtn.text("Enable");
                    stateInd.removeClass("aui-lozenge aui-lozenge-success")
                        .addClass("aui-lozenge aui-lozenge-error").text("Disabled")
                } else if (action === "enable") {
                    stateBtn.text("Disable");
                    stateInd.removeClass("aui-lozenge aui-lozenge-error")
                        .addClass("aui-lozenge aui-lozenge-success").text("Scheduled")
                }
            },
            error: function (err) {
                console.log("ERROR");
                console.log(err);
                errorFlag("Something went wrong! Check logs!");
            }
        });
    });

    //run now button behaviour
    var runBtn = AJS.$("#run-now-button");
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
                errorFlag('Something went wrong! Check logs!');
            }
        });
    });

    //show last report button behaviour
    var reportBtn = AJS.$("#report-button");
    var tableBody = AJS.$("#audit-report > tbody");
    reportBtn.click(function () {
        tableBody.children().remove();
        AJS.$.ajax({
            url: AJS.contextPath() + "/plugins/servlet/groupaudit",
            traditional: true,
            type: "POST",
            data: "show",
            success: function (responseJson) {
                console.log("SUCCESS");
                for (i = 0; i < responseJson.report.length; i++) {
                    var auditReportEntity = responseJson.report[i];
                    tableBody.append('<tr>' +
                        '<td headers="space-key">' + auditReportEntity.spacekey + '</td>' +
                        '<td headers="group">' + auditReportEntity.group + '</td>' +
                        '<td headers="permission">' + auditReportEntity.permission + '</td>' +
                        '<td headers="creator">' + auditReportEntity.violator + '</td>' +
                        '<td headers="date">' + auditReportEntity.date + '</td>' +
                        '</tr>');
                }
            },
            error: function (err) {
                console.log("ERROR");
                console.log(err);
                errorFlag("Something went wrong! Check logs!");
            }
        });
    });

    function errorFlag (message) {
        AJS.flag({
            type: 'error',
            body: message,
            close: "auto"
        });
    }
    function cleanDataObject() {
        dataObject.spacesToAdd = [];
        dataObject.spacesToDel = [];
        dataObject.groupsToAdd = [];
        dataObject.groupsToDel = [];
        dataObject.userNamesToAdd = [];
        dataObject.userNamesToDel = [];
    }
});