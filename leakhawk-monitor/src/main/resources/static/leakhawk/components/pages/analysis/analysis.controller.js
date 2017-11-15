(function () {
    'use strict';

    angular.module('leakhawk')
        .controller('AnalysisController', AnalysisController);

    AnalysisController.$inject = ['webservice'];

    function AnalysisController(webservice) {
        var vm = this;

        vm.colors1  = ['#3498DB', '#72C02C', '#717984', '#F1C40F'];
        vm.colors  = [ '#A9BCF5', '#0080FF', '#717984', '#F1C40F'];

        webservice.call('/chart/count_prefilter_post', 'GET').then(function (response) {
            vm.preFilterDataList = response.data.dataList;
            vm.preFilterTitleList = response.data.titleList;
        });


        webservice.call('/chart/count_contextfilter_post', 'GET').then(function (response) {
            vm.contextFilterDataList = response.data.dataList;
            vm.contextFilterTitleList = response.data.titleList;
        });


        webservice.call('/chart/count_filter_post', 'GET').then(function (response) {
            vm.filterDataList = response.data.dataList;
            vm.filterTitleList = response.data.titleList;
        });

        webservice.call('/chart/count_content_passed', 'GET').then(function (response) {
            vm.contentClassifierDataList = response.data.dataList;
            vm.contentClassifierTitleList = response.data.titleList;
        });

        webservice.call('/chart/count_evidence_passed', 'GET').then(function (response) {
            vm.evidenceClassifierDataList = response.data.dataList;
            vm.evidenceClassifierTitleList = response.data.titleList;
        });


        webservice.call('/chart/count_sensitivity_level', 'GET').then(function (response) {
            vm.sensitivityLevelDataList = response.data.dataList;
            vm.sensitivityLevelTitleList = response.data.titleList;
        });

        webservice.call('/chart/count_content_type', 'GET').then(function (response) {
            vm.classificationTypeDataList = response.data.dataList;
            vm.classificationTypeTitleList = response.data.titleList;
        });



        // vm.bestDealsLabels.push("A");
        // vm.bestDealsData.push(20);
        // vm.bestDeals.push({
        //     name: "Item1",
        //     color: "red"
        // });
        //
        // vm.bestDealsLabels.push("B");
        // vm.bestDealsData.push(20);
        // vm.bestDeals.push({
        //     name: "Item2",
        //     color: "blue"
        // });
        //
        // vm.bestDealsLabels.push("C");
        // vm.bestDealsData.push(20);
        // vm.bestDeals.push({
        //     name: "Item3",
        //     color: "red"
        // });
        //
        // vm.bestDealsLabels.push("D");
        // vm.bestDealsData.push(20);
        // vm.bestDeals.push({
        //     name: "Item4",
        //     color: "blue"
        // });

        console.log("dfgfd")
    }
})();


