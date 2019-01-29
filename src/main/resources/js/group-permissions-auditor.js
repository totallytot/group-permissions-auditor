AJS.toInit(function () {
    AJS.$(".multi-select").auiSelect2();

    //will store all data
    var dataObject = {};
    dataObject.spacesToAdd = [];
    dataObject.spacesToDel = [];
    dataObject.groupsToAdd = [];
    dataObject.groupsToDel = [];

    function cleanDataObject() {
        dataObject.spacesToAdd = [];
        dataObject.spacesToDel = [];
        dataObject.groupsToAdd = [];
        dataObject.groupsToDel = [];
    }

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
                success: function (resp) {
                    console.log("SUCCESS");
                    console.log(resp);
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
});