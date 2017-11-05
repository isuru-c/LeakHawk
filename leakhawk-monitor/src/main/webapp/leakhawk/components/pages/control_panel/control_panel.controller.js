
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('ControlPanelController', ControlPanelController);

    ControlPanelController.$inject = ['webservice','$stateParams' ,'$state'];

    function ControlPanelController(webservice, $stateParams,$state) {
        var vm = this;
        vm.startLeakHawk = startLeakHawk;
        vm.stopLeakHawk = stopLeakHawk;
        vm.addTwitter = addTwitter;
        vm.removeTwitter = removeTwitter;
        vm.addPatebin = addPastebin;
        vm.saveConfig = saveResourcePath;

        vm.leakHawkButton = false;
        vm.twitterButton = false;
        vm.pastebinButton = false;




        // webservice.call('/getIncident/'+$stateParams.id, 'GET').then(function (response) {
        //
        //     vm.postDetails = response.data.details;
        // });

        function startLeakHawk(){
            $.LoadingOverlay("show");
            webservice.call('configuration/start_leakhawk', 'GET').then(function (response) {
                $.LoadingOverlay("hide");
                vm.responseData += "LeakHawk Started.....\n";
            });
            vm.leakHawkButton = true;
        }

        function stopLeakHawk(){
            $.LoadingOverlay("show");
            webservice.call('configuration/stop_leakhawk', 'GET').then(function (response) {
                $.LoadingOverlay("hide");
                vm.responseData += "LeakHawk Stoped.....\n";
            });
            vm.leakHawkButton = false;
        }


        function addTwitter() {
            $.LoadingOverlay("show");
            webservice.call('configuration/add_twitter', 'GET').then(function (response) {
                console.log(response.data);
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Twitter feed added.....\n";
            vm.twitterButton = true;
        }

        function addPastebin() {
            $.LoadingOverlay("show");
            webservice.call('configuration/add_pastebin', 'GET').then(function (response) {
                console.log(response.data);
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Pastebin feed added.....\n";
            vm.pastebinButtonButton = true;
        }

        function removeTwitter() {
            $.LoadingOverlay("show");
            webservice.call('configuration/removeTwitter', 'GET').then(function (response) {
                console.log(response.data);
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Twitter feed removed.....\n";
            vm.twitterButton = false;
        }

        function saveResourcePath() {
            $.LoadingOverlay("show");
            var sendObj = {"resourcePath":vm.resourcePath};
            webservice.call('configuration/save_config', 'POST', sendObj).then(function (response) {
                $.LoadingOverlay("hide");
            });
            vm.responseData += "Configuration Saved.....\n";
        }
    }

})();
