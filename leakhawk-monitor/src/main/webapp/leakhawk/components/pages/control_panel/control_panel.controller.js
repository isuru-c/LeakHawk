
(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('ControlPanelController', ControlPanelController);

    ControlPanelController.$inject = ['webservice','$stateParams' ,'$state'];

    function ControlPanelController(webservice, $stateParams,$state) {
        var vm = this;

        webservice.call('/getIncident/'+$stateParams.id, 'GET').then(function (response) {

            vm.postDetails = response.data.details;
        });
    }

})();
