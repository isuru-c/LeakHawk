(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('ControlPanelController', ControlPanelController);

    ControlPanelController.$inject = ['webservice', '$stateParams', '$state', 'Notification'];

    function ControlPanelController(webservice, $stateParams, $state, Notification) {
        var vm = this;
        vm.startLeakHawk = startLeakHawk;
        vm.stopLeakHawk = stopLeakHawk;
        vm.addTwitter = addTwitter;
        vm.removeTwitter = removeTwitter;
        vm.removePastebin = removePastebin;
        vm.addPatebin = addPastebin;
        vm.saveConfig = saveResourcePath;

        vm.leakhawkBoolean = false;
        vm.twitterBoolean = false;
        vm.pastebinBoolean = false;

        $.LoadingOverlay("show");
        webservice.call('configuration/get_config', 'GET').then(function (response) {
            vm.resourcePath = response.data.resourcePath;
            vm.leakhawkBoolean = response.data.leakhawk;
            vm.twitterBoolean = response.data.twitterSensor;
            vm.pastebinBoolean = response.data.pastebinSensor;
            $.LoadingOverlay("hide");
        });

        // webservice.call('/getIncident/'+$stateParams.id, 'GET').then(function (response) {
        //
        //     vm.postDetails = response.data.details;
        // });

        function startLeakHawk() {
            $.LoadingOverlay("show");
            webservice.call('configuration/start_leakhawk', 'GET').then(function (response) {
                if (response.data.status==200) {
                    $.LoadingOverlay("hide");
                    vm.responseData += "LeakHawk Started.....\n";
                    Notification.success('LeakHawk Started');
                    vm.leakhawkBoolean = true;
                }else if(response.data.status == 400){
                    $.LoadingOverlay("hide");
                    Notification.warning('Please insert all needed files to the resource folder.');
                    Notification.error('Leakhawk start failed.');
                }
            });


        }

        function stopLeakHawk() {
            $.LoadingOverlay("show");
            webservice.call('configuration/stop_leakhawk', 'GET').then(function (response) {
                if (response.status == 200) {
                    $.LoadingOverlay("hide");
                    vm.responseData += "LeakHawk Stoped.....\n";
                    Notification.success('LeakHawk Stopped');
                }
            });
            vm.leakhawkBoolean = false;
        }


        function addTwitter() {
            $.LoadingOverlay("show");
            webservice.call('configuration/add_twitter', 'GET').then(function (response) {
                if (response.status == 200) {
                    console.log(response.data);
                    $.LoadingOverlay("hide");
                    Notification.success('Twitter feed added');
                }
            });
            vm.responseData += "Twitter feed added.....\n";
            vm.twitterBoolean = true;
        }

        function addPastebin() {
            $.LoadingOverlay("show");
            webservice.call('configuration/add_pastebin', 'GET').then(function (response) {
                if (response.status == 200) {
                    console.log(response.data);
                    $.LoadingOverlay("hide");
                    Notification.success('Pastebin feed added');
                }
            });
            vm.responseData += "Pastebin feed added.....\n";
            vm.pastebinBoolean = true;
        }

        function removeTwitter() {
            $.LoadingOverlay("show");
            webservice.call('configuration/stop_twitter', 'GET').then(function (response) {
                if (response.status == 200) {
                    console.log(response.data);
                    $.LoadingOverlay("hide");
                    Notification.success('Twitter feed removed');
                }
            });
            vm.responseData += "Twitter feed removed.....\n";
            vm.twitterBoolean = false;
        }

        function removePastebin() {
            $.LoadingOverlay("show");
            webservice.call('configuration/stop_pastebin', 'GET').then(function (response) {
                if (response.status == 200) {
                    console.log(response.data);
                    $.LoadingOverlay("hide");
                    Notification.success('Pastebin feed removed');
                }
            });
            vm.responseData += "Pastebin feed removed.....\n";
            vm.twitterBoolean = false;
        }

        function saveResourcePath() {
            $.LoadingOverlay("show");
            var sendObj = {"resourcePath": vm.resourcePath};
            webservice.call('configuration/save_config', 'POST', sendObj).then(function (response) {
                if(response.data.status==200) {
                    $.LoadingOverlay("hide");
                    Notification.success('Configuration saved.');
                }else if(response.data.status == 400){
                    $.LoadingOverlay("hide");
                    Notification.warning('Please insert all needed files to the given folder.');
                }

            });
            vm.responseData += "Configuration Saved.....\n";
        }
    }

})();
