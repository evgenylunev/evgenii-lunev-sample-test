var protojson = angular.module('protojson', []);
/*
protojson.config(['$interpolateProvider', function($interpolateProvider) {
  $interpolateProvider.startSymbol('{[');
  $interpolateProvider.endSymbol(']}');
}]);*/


var ProtoBuf = dcodeIO.ProtoBuf;
var WeatherList, WeatherItem;

ProtoBuf.loadProtoFile("/protofiles/weatherentity.proto", function(err, builder) {
 console.log(err);
  WeatherItem = builder.build("WeatherItem");
  WeatherList = builder.build("WeatherList");
});

protojson.controller('WeatherCtrl', function($scope, $http) {
  //$scope.weatherlist = [];
  
  $scope.useProtobuf = false;
  $scope.getContacts = function() {
	  var count = parseInt($scope.numberOfRows) + 1;
	  $scope.weatherlist = [];
	  $http.get('/epam/rest/weather/' + count + '/')
      .success(function (data) {
          $scope.weatherlist = data;
      })
      .error(function (data, status, headers, config) {
          $scope.errorMessage = "Couldn't load the list of weather, error # " + status;
      });  
    
   
  };

  $scope.getContactsProtobuf = function() {
	 var count = parseInt($scope.numberOfRows) + 1;
    $scope.weatherlist = [];
    var req = {
      method: 'GET',
      url: '/epam/rest/protoweather/' + count + '/',
      responseType: 'arraybuffer'
    };
    $http(req).success(function(data) {
      var msg = WeatherList.decode(data);
      $scope.weatherlist = msg.list;
    });
  };

  
  // Init page with json data
  $scope.numberOfRows = 5;
  $scope.getContacts();
});



