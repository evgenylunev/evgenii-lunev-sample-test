var weatherApp = angular.module('weatherApp', []);
weatherApp.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);

weatherApp.controller('MasterDetailCtrl',
		  function ($scope, $http) {

		    

		    // And we'll get Angular to populate this Customer's list of Orders (and the Products in them) into this variable
		    $scope.listOfWeather = null;

		    // When we first load our Controller, we'll call our first web service, which fetchs a list of all Customer records.
		    $http.get('/epam/rest/weather/11/')
		        .success(function (data) {
		            $scope.listOfWeather = data.weatherClass;

		           
		        })
		        .error(function (data, status, headers, config) {
		            $scope.errorMessage = "Couldn't load the list of weather, error # " + status;
		        });

		    $scope.loadWeather = function () {
		    	var count = parseInt($scope.numberOfRows) + 1;
		        $http.get('/epam/rest/weather/' + count + '/')
		                .success(function (data) {
		                    $scope.listOfWeather = data.weatherClass;
		                })
		                .error(function (data, status, headers, config) {
		                    $scope.errorMessage = "Couldn't load the list of weather, error # " + status;
		                });
		    }
		    
		     // When the user selects a new Customer from our list, we'll call this function, which calls our second web service, 
		    // which loads the list of Orders that this Customer has made. 
		   
		});